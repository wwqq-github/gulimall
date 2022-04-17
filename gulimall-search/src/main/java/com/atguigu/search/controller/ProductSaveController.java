package com.atguigu.search.controller;

import com.atguigu.common.utils.R;
import com.atguigu.search.dto.SkuEsModule;
import com.atguigu.search.service.ProductSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/search")
public class ProductSaveController {

    @Autowired
    private ProductSearchService productSearchService;

    @RequestMapping("/product/save")
    public R saveProduct(@RequestBody List<SkuEsModule> skuEsModules) throws IOException {

        List<String> ids=productSearchService.saveProduct(skuEsModules);
        return R.ok().put("data",ids);
    }
}
