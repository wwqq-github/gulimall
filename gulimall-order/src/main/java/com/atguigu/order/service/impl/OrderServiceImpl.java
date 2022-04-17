package com.atguigu.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberVO;
import com.atguigu.order.entity.OrderItemEntity;
import com.atguigu.order.entity.PaymentInfoEntity;
import com.atguigu.order.exception.OrderException;
import com.atguigu.order.feign.MemberFeign;
import com.atguigu.order.feign.ProductFeign;
import com.atguigu.order.feign.WareFeign;
import com.atguigu.order.service.OrderItemService;
import com.atguigu.order.service.PaymentInfoService;
import com.atguigu.order.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.order.dao.OrderDao;
import com.atguigu.order.entity.OrderEntity;
import com.atguigu.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private WareFeign wareFeign;
    @Autowired
    private MemberFeign memberFeign;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public ConfirmVO confirm(Long memberId) {
        ConfirmVO confirm=new ConfirmVO();
        BigDecimal totalPrice = new BigDecimal("0");
//        获取购物车中选中的所有商品
        List<OrderProduct> orderProducts = checkStock(memberId);
        Integer productSum=0;
        List<Long> skuIds=new ArrayList<>();
        //遍历所有的商品 并统计价格 设置库存
        for (OrderProduct orderProduct:orderProducts){
            BigDecimal multiply = orderProduct.getSkuPrice().multiply(new BigDecimal(orderProduct.getSkuNum()));
            totalPrice = totalPrice.add(multiply);
            productSum+=orderProduct.getSkuNum();
            skuIds.add(orderProduct.getSkuId());
        }
        Map<Long,Boolean> stocks=wareFeign.getStock(skuIds);
        orderProducts = orderProducts.stream().map(item -> {
            item.setStock(stocks.get(item.getSkuId()));
            return item;
        }).collect(Collectors.toList());
        confirm.setProductSum(productSum);
        confirm.setTotalPrice(totalPrice);
        confirm.setOrderProducts(orderProducts);
        confirm.setAddress(getMemberAdd(memberId));

        String token = UUID.randomUUID().toString().replace("-","");
        stringRedisTemplate.opsForValue().set("order:token"+memberId,token);
        confirm.setToken(token);
        return confirm;
    }

    @Override
    public OrderEntity saveOrder(SubmitOrderVO submitOrderVO) {
        //TODO
        //获取的同时应该删除订单的token 防止重复提交 后续需要改进
        String token = stringRedisTemplate.opsForValue().get("order:token" + submitOrderVO.getMemberId());
        if(token==null||!token.equals(submitOrderVO.getOrderToken())){
            throw new OrderException("提交订单错误 有重复提交或者订单已失效");
        }
        System.err.println(System.currentTimeMillis());
        OrderEntity order = saveOrderIten(submitOrderVO);
        System.err.println(System.currentTimeMillis());
        return order;
    }

    @Override
    public List<DetailVO> orderList(Long id) {
        List<OrderEntity> orderList = this.list(new QueryWrapper<OrderEntity>().eq("member_id",id));
        List<DetailVO> collect = orderList.stream().map(item -> {
            DetailVO detailVO = new DetailVO();
            detailVO.setOrder(item);
            List<OrderItemEntity> itemList = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_id",item.getId()));
            detailVO.setOrderItems(itemList);
            return detailVO;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public DetailVO getOrder(Long orderId) {
        DetailVO detailVO = new DetailVO();
        OrderEntity orderEntity = this.baseMapper.selectById(orderId);
        detailVO.setOrder(orderEntity);
        List<OrderItemEntity> orderItems = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_id",orderId));
        detailVO.setOrderItems(orderItems);
        return detailVO;
    }

    @Override
    @Transactional
    public String updatePayState(AliPayVO aliPayVO) {
        OrderEntity order=new OrderEntity();
        order.setId(aliPayVO.getOut_trade_no());
        order.setPayType(1);
        order.setPaymentTime(aliPayVO.getGmt_create());
        order.setModifyTime(new Date());
        this.updateById(order);
        PaymentInfoEntity pay=new PaymentInfoEntity();
        pay.setOrderId(order.getId());
        pay.setOrderSn(order.getOrderSn());
        pay.setAlipayTradeNo(aliPayVO.getTrade_no());
        pay.setPaymentStatus(aliPayVO.getTrade_status());
        pay.setCreateTime(new Date());
        pay.setCallbackTime(aliPayVO.getNotify_time());
        pay.setCallbackContent(aliPayVO.getBody());
        pay.setSubject(aliPayVO.getSubject());
        pay.setConfirmTime(aliPayVO.getGmt_close());
        paymentInfoService.save(pay);
        return "SUCCESS";
    }


    //获取购物车中选中的所有商品
    public List<OrderProduct> checkStock(Long memberId){
        String s = stringRedisTemplate.opsForValue().get("cart:" + memberId);
        List<OrderProduct> orderProduct =new ArrayList<>();
        if (StringUtils.isNotEmpty(s)&& JSON.parseObject(s, List.class).size()>0){
            orderProduct=JSON.parseArray(s,OrderProduct.class);
            orderProduct=orderProduct.stream().filter(item->item.getCheck()).collect(Collectors.toList());
        }else {
            throw new OrderException("修改购物车失败 购物车数据已过期");
        }

        return orderProduct;
    }

    private List<Address> getMemberAdd(Long id){
        return memberFeign.getMemberddr(id);
    }

    //添加订单和订单项
    @Transactional
    public OrderEntity saveOrderIten(SubmitOrderVO submitOrderVO) {
        //设置订单信息
        OrderEntity order= new OrderEntity();
        order.setMemberId(submitOrderVO.getMemberId());
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        order.setOrderSn(format.format(date)+System.currentTimeMillis());
        order.setCreateTime(new Date());
        order.setTotalAmount(submitOrderVO.getPayPrice());
        order.setStatus(0);
        order.setAutoConfirmDay(10);
        order.setNote(submitOrderVO.getNote());
        SimpleDateFormat orderId = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        order.setId(orderId.format(date)+System.currentTimeMillis());
        order.setCreateTime(date);
        //远程调用 获取用户地址信息
        R info = memberFeign.info(submitOrderVO.getAddrId());
        if (info.getCode()==0){
            Address addres =(Address) info.getData("memberReceiveAddress", new TypeReference<Address>() {
            });
            order.setReceiverName(addres.getName());
            order.setReceiverPhone(addres.getPhone());
            order.setReceiverProvince(addres.getProvince());
            order.setReceiverCity(addres.getCity());
            order.setReceiverDetailAddress(addres.getDetailAddress());
            order.setReceiverRegion(addres.getRegion());
            order.setReceiverPostCode(addres.getPostCode());
        }else {
            throw new OrderException("订单失败 获取地址信息失败");
        }
        //远程调用 获取用户信息
        R member = memberFeign.member(submitOrderVO.getMemberId());
        if (member.getCode()==0){
            MemberVO memberVO =(MemberVO) member.getData("member", new TypeReference<MemberVO>() {
            });
            order.setMemberUsername(memberVO.getUsername());
        }else {
            throw new OrderException("订单失败 获取用户名错误");
        }
        BigDecimal freightAmount = new BigDecimal("0");
        //远程获取运费信息
        order.setFreightAmount(freightAmount);
        //远程获取促销金额
        BigDecimal promotionAmount =new BigDecimal("0");
        order.setPromotionAmount(promotionAmount);
        //远程获取积分优惠金额
        BigDecimal integrationAmount=new BigDecimal("0");
        order.setIntegrationAmount(integrationAmount);
        //远程获取优惠券金额
        BigDecimal couponAmount = new BigDecimal("0");
        order.setCouponAmount(couponAmount);
        //远程获取打折信息
        BigDecimal discount = new BigDecimal("0");
        order.setDiscountAmount(discount);

        //远程获取成长值
        Integer growth=0;
        order.setGrowth(growth);
        Integer integration=0;
        order.setGrowth(integration);
        BigDecimal payAmount  =new BigDecimal("0");
        payAmount=submitOrderVO.getPayPrice().add(freightAmount).subtract(promotionAmount)
                .subtract(integrationAmount)
                .subtract(couponAmount)
                .subtract(discount);
        order.setPayAmount(payAmount);

        //保存订单
        this.save(order);
        //验证成功
        //验证价格是否一致
        //获取购物车中的所有选中的信息
        List<OrderProduct> orderProducts = checkStock(submitOrderVO.getMemberId());
        BigDecimal totalPrice=new BigDecimal("0");
        List<Long>   skuIds=new ArrayList<>();
        Map<Long,OrderProduct> cartMap =new HashMap<>();
        List<LockStockVO> stockVOS =new ArrayList<>();
        //统计价格  统计数量
        for (OrderProduct op:orderProducts){
            skuIds.add(op.getSkuId());
            cartMap.put(op.getSkuId(),op);
            totalPrice= totalPrice.add(op.getSkuPrice().multiply(new BigDecimal(op.getSkuNum())));
            stockVOS.add(new LockStockVO(op.getSkuId(),op.getSkuNum()));
        }
        //验证价格
        if (submitOrderVO.getPayPrice().compareTo(totalPrice) != 0) {
            throw new OrderException("提交订单错误 价格错误");
        }
        //远程调用商品服务获取商品的信息
        R r = productFeign.orderProduct(skuIds);
        List<OrderItemEntity> orderItems=(List<OrderItemEntity>)r.getData("orderItems",new TypeReference<List<OrderItemEntity>>(){});
        orderItems=orderItems.stream().map(i->{
            i.setOrderId(order.getId());
            i.setOrderSn(order.getOrderSn());
            OrderProduct orderProduct = cartMap.get(i.getSkuId());
            i.setSkuQuantity(orderProduct.getSkuNum());
            i.setSkuAttrsVals(orderProduct.getSkuAttrsVals());
            return i;
        }).collect(Collectors.toList());
        //添加购物项
        orderItemService.saveBatch(orderItems);

        //锁库存
        R lockStock = wareFeign.lockStock(stockVOS);

        //发消息到rabbitmq
        rabbitTemplate.convertAndSend("exchange","dead.exchange",order);
        return order;
    }

    //查询订单信息渲染到页面上去
    private void selectOrder(Long id) {
    }



    //监控rabbitmq中的队列  等订单超时之后处理订单 解锁库存
    @RabbitListener(queues = "order.queue")
    public void listener(OrderEntity s){
        OrderEntity byId = this.getById(s.getId());
        //解锁库存
        if (byId.getStatus()==0||byId.getStatus()==4||byId.getStatus()==5){
            List<OrderItemEntity> list = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_id", s.getId()));

            List<LockStockVO> collect = list.stream().map(item -> {
                return new LockStockVO(item.getSkuId(), item.getSkuQuantity());
            }).collect(Collectors.toList());
            R r = wareFeign.lockStock(collect);
            s.setStatus(4);
            this.updateById(s);
        }
    }
}