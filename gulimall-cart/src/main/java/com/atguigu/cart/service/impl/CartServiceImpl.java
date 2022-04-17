package com.atguigu.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.cart.exception.CartException;
import com.atguigu.cart.feign.ProductFeign;
import com.atguigu.cart.service.CartService;
import com.atguigu.cart.vo.AddProductVO;
import com.atguigu.cart.vo.CartPriceVO;
import com.atguigu.cart.vo.CartVO;
import com.atguigu.cart.vo.Exist;
import com.atguigu.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.descriptor.web.SecurityRoleRef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ProductFeign productFeign;

    @Autowired
    private SessionProperties sessionProperties;
    //加入购物车
    @Override
    public void addToCart(AddProductVO addProductVO,Long memberId) {
        //  加入购物车
       addCart(addProductVO,memberId);
    }

    //获取用户购物车信息
    @Override
    public CartPriceVO getUserCart(Long id) {
        String s = stringRedisTemplate.opsForValue().get("cart:" + id);
        List<CartVO> carts=JSON.parseArray(s,CartVO.class);
        CartPriceVO cartPrice=new CartPriceVO();
        cartPrice.setCarts(carts);
        cartPrice.setTotalPrice(getTotalPrice(carts));
        return cartPrice;
    }

    //添加购物车商品数量
    @Override
    public CartPriceVO addProductNum(Long skuId,Long memberId){
        CartPriceVO change = change(skuId, memberId, 1,false);
        return change;
    }

    //减少购物车商品数量
    @Override
    public CartPriceVO subProductNum(Long skuId,Long memberId){
        return change(skuId,memberId,-1,false);
    }

    //改变购物车商品选中状态
    @Override
    public CartPriceVO checkItem(Long skuId, Long id) {
        return change(skuId,id,0,true);
    }

    //删除购物车商品
    @Override
    public CartPriceVO delect(Long skuId, Long id) {
        CartPriceVO cartPriceVO = new CartPriceVO();
        String s = stringRedisTemplate.opsForValue().get("cart:" + id);
        List<CartVO> carts =new ArrayList<>();
        if (StringUtils.isNotEmpty(s)&&JSON.parseObject(s, List.class).size()>0){
            carts=JSON.parseArray(s,CartVO.class);
            carts=carts.stream().filter(item-> !skuId.equals(item.getSkuId())).collect(Collectors.toList());
            stringRedisTemplate.opsForValue().set("cart:" + id, JSON.toJSONString(carts));
            System.err.println("删除购物车商品="+carts.size());
        }else {
            throw new CartException("修改购物车失败 购物车数据已过期");
        }
        cartPriceVO.setCarts(carts);
        cartPriceVO.setTotalPrice(getTotalPrice(carts));
        return cartPriceVO;
    }

    //添加购物车
    private void addCart(AddProductVO addProductVO,Long memberId){
        //获取缓存中的用户购物车信息 转换成java几盒
        String s = stringRedisTemplate.opsForValue().get("cart:" + memberId);
        List<CartVO> carts =JSON.parseArray(s,CartVO.class);
        //存在用户的购物车
        if (carts!=null){
            final Exist exist =new Exist();
            //缓存中有数据
            carts =  carts.stream().map(item->{
                //判断便利的是否是客户选中的sku 如果是的话 数量+1  状态变成已经存在
                if (item.getSkuId().equals(addProductVO.getSkuId())){
                    item.setSkuNum(item.getSkuNum()+addProductVO.getNum());
                    exist.setExist(true);
                }
                return item;
            }).collect(Collectors.toList());
            //缓存中没有当前sku的数据
            if (!exist.getExist()){
                //创建对象
                //远程调用 获取sku的信息

                R r=productFeign.info(addProductVO.getSkuId());
                CartVO cartVO  =(CartVO)r.getData("cartItem",new TypeReference<CartVO>(){});
                if (cartVO==null){
                    throw new CartException("商品已经删除 请重新添加");
                }
                //添加数据到购物车
                cartVO.setSkuNum(addProductVO.getNum());
                //添加数据到购物车
                carts.add(cartVO);
            }
        }else {
            carts = new ArrayList<>();
            //缓存中没有 不存在用户购物车
            //添加到redis缓存
            CartVO cartVO = new CartVO();
            //远程调用
            R r = productFeign.info(addProductVO.getSkuId());
            if (r.getCode()==0) {
                cartVO = (CartVO) r.getData("cartItem", new TypeReference<CartVO>() {
                });
            }
            //设置数量
            cartVO.setSkuNum(addProductVO.getNum());
            //添加
            carts.add(cartVO);
        }
        stringRedisTemplate.opsForValue().set("cart:" + memberId, JSON.toJSONString(carts));

    }


    //改变购物车状态
    public CartPriceVO change(Long skuId,Long id,Integer num,Boolean check){
        CartPriceVO cartPriceVO=new CartPriceVO();
        //获取购物车信息
        String s = stringRedisTemplate.opsForValue().get("cart:" + id);
        List<CartVO> carts =new ArrayList<>();
        //判断购物车是否有数据
        if (StringUtils.isNotEmpty(s)&&JSON.parseObject(s, List.class).size()>0){
            carts=JSON.parseArray(s,CartVO.class);
            //遍历购物车数据
            carts=carts.stream().map(item->{
                if (item.getSkuId().equals(skuId)){
                    //改库存
                    item.setSkuNum(item.getSkuNum()+num);
                    //改状态
                    if (check){
                        item.setCheck(!item.getCheck());
                    }
                }

                return item;
            }).collect(Collectors.toList());
            stringRedisTemplate.opsForValue().set("cart:" + id, JSON.toJSONString(carts));
        }else {
            throw new CartException("修改购物车失败 购物车数据已过期");
        }
        cartPriceVO.setCarts(carts);
        cartPriceVO.setTotalPrice(getTotalPrice(carts));
        return cartPriceVO;
    }


    private BigDecimal getTotalPrice(List<CartVO> carts){
        BigDecimal totalPrice=new BigDecimal("0");
        if (carts!=null&&carts.size()>0){
            for (CartVO cart:carts){
                if (cart.getCheck()){
                    totalPrice=totalPrice.add(cart.getSkuPrice().multiply(new BigDecimal(cart.getSkuNum())));
                }
            }
        }
        return totalPrice;
    }
}
