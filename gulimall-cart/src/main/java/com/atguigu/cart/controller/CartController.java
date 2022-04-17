package com.atguigu.cart.controller;

import com.atguigu.cart.service.CartService;
import com.atguigu.cart.vo.AddProductVO;
import com.atguigu.cart.vo.CartPriceVO;
import com.atguigu.cart.vo.CartVO;
import com.atguigu.common.vo.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;



    @GetMapping("cartList.html")
    public String cart(Model model, HttpSession session){
        MemberVO member= (MemberVO) session.getAttribute("userSession");
        CartPriceVO cartPrice=cartService.getUserCart(member.getId());
        model.addAttribute("cartPrice",cartPrice);
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(AddProductVO addProductVO,Model model, HttpSession session){
        MemberVO member= (MemberVO) session.getAttribute("userSession");
        cartService.addToCart(addProductVO,member.getId());
        return "redirect:http://cart.gulimall.com/cartList.html";
    }


    @GetMapping("/addNum")
    @ResponseBody
    public CartPriceVO addNum(@RequestParam("skuId") Long skuId, HttpSession session){
        MemberVO userSession =(MemberVO) session.getAttribute("userSession");
        return  cartService.addProductNum(skuId,userSession.getId());
//        return "redirect:http://cart.gulimall.com/cartList.html";
    }

    @GetMapping("/subNum")
    @ResponseBody
    public CartPriceVO subNum(@RequestParam("skuId") Long skuId, HttpSession session){
        MemberVO userSession =(MemberVO) session.getAttribute("userSession");
        return  cartService.subProductNum(skuId,userSession.getId());
//        return "redirect:http://cart.gulimall.com/cartList.html";
    }

    @GetMapping("/checkItem")
    @ResponseBody
    public CartPriceVO checkItem(@RequestParam("skuId") Long skuId, HttpSession session){
        MemberVO userSession =(MemberVO) session.getAttribute("userSession");
        return cartService.checkItem(skuId,userSession.getId());
//        return "redirect:http://cart.gulimall.com/cartList.html";
    }
    @GetMapping("/delect")
    @ResponseBody
    public CartPriceVO delect(@RequestParam("skuId") Long skuId, HttpSession session){
        MemberVO userSession =(MemberVO) session.getAttribute("userSession");
        return cartService.delect(skuId,userSession.getId());
//        return "redirect:http://cart.gulimall.com/cartList.html";
    }


    @GetMapping("/toTrade")
    public String toTrade(){
        return "redirect:http://cart.gulimall.com/cartList.html";
    }
}