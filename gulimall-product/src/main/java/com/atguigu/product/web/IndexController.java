package com.atguigu.product.web;

import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.service.CategoryService;
import com.atguigu.product.vo.CategoryTreeVO;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;


    @RequestMapping(value={"/index.html","/"})
    public String index(Model model, HttpSession session){
        List<CategoryEntity> categoryEntityList=categoryService.getOneLevel();
        model.addAttribute("categories",categoryEntityList);
        return "index";
    }

    @ResponseBody
    @RequestMapping("/index/catelog.json")
    public Map<String,List<CategoryTreeVO>> getCategoryJson(){
        return categoryService.getCategoryJson();
    }
}
