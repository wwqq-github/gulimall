package com.atguigu.order.web;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.common.vo.MemberVO;
import com.atguigu.order.entity.OrderEntity;
import com.atguigu.order.service.OrderService;
import com.atguigu.order.vo.ConfirmVO;
import com.atguigu.order.vo.DetailVO;
import com.atguigu.order.vo.SubmitOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayClient alipayClient;

    @GetMapping("toTrade")
    public String toTrade(HttpSession session, Model model){
        MemberVO userSession =(MemberVO) session.getAttribute("userSession");
        ConfirmVO confirm=orderService.confirm(userSession.getId());
        confirm.setMemberId(userSession.getId());
        model.addAttribute("confirm",confirm);
        return "confirm";
    }
    @GetMapping("{orderId}.html")
    public String detail(@PathVariable("orderId") Long orderId,Model model){
        DetailVO detailVO=orderService.getOrder(orderId);
        model.addAttribute("detail",detailVO);

        return "detail";
    }
    @GetMapping("list.html")
    public  String list(HttpSession session, Model model){
        MemberVO member =(MemberVO) session.getAttribute("userSession");
        List<DetailVO> orderList = orderService.orderList(member.getId());
        model.addAttribute("orderList",orderList);
        return "list";
    }
    @GetMapping("pay.html")
    public String pay(){
        return "pay";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(SubmitOrderVO submitOrderVO,Model model){
        OrderEntity order=orderService.saveOrder(submitOrderVO);
        model.addAttribute("order",order);
        return "pay";
    }

    @GetMapping("/alipay")
    @ResponseBody
    public String alipay(@RequestParam("orderId") Long orderId,@RequestParam("orderPrict") BigDecimal orderPrice) throws AlipayApiException, UnsupportedEncodingException {
        //??????????????????
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        //  ???????????? ??????????????????
        alipayRequest.setReturnUrl("http://order.gulimall.com/list.html");
        //???????????????????????????????????????????????????????????????http/https?????????
        alipayRequest.setNotifyUrl("http://order.vaiwan.com/alipay/notify");

        //?????????????????????????????????????????????????????????????????????
        String out_trade_no = new String(orderId.toString().getBytes("ISO-8859-1"),"UTF-8");
        //?????????????????????
        String total_amount = new String(orderPrice.setScale(2, BigDecimal.ROUND_HALF_UP).toString().getBytes("ISO-8859-1"),"UTF-8");
        //?????????????????????
        String subject = new String("chengdongliang".getBytes("ISO-8859-1"),"UTF-8");
        //?????????????????????
        String body = new String("gulishangcheng".getBytes("ISO-8859-1"),"UTF-8");

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //?????????BizContent?????????????????????????????????????????????????????????????????????timeout_express???????????????
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //?????????????????????????????????????????????API??????-alipay.trade.page.pay-?????????????????????

        //??????
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //??????
        System.out.println(result);


        return result;
    }

}
