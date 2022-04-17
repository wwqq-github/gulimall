package com.atguigu.search.controller;


import com.atguigu.search.service.ProductSearchService;
import com.atguigu.search.vo.SearchResultVo;
import com.atguigu.search.vo.SearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class ProductSerachController {

    @Autowired
    private ProductSearchService productSearchService;

    @GetMapping("/list.html")
    public String searchProduct(SearchVO search, Model model) throws IOException {
        SearchResultVo result=productSearchService.asynSearchProduct(search);
        model.addAttribute("result",result);
        return "list";
    }
}
