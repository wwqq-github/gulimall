package com.atguigu.product.web;

import com.atguigu.product.service.SpuInfoService;
import com.atguigu.product.vo.SpuItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ItemController {

    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 异步执行任务
     * @param skuId
     * @param model
     * @return
     */
    @RequestMapping("{skuId}.html")
    public String spuItem(@PathVariable("skuId") Long skuId, Model model){
        SpuItemVO spuItemVO=spuInfoService.asynSpuItem(skuId);
        model.addAttribute("spuItem",spuItemVO);
        return "item";
    }

}
