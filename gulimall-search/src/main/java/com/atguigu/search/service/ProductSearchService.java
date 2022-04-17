package com.atguigu.search.service;

import com.atguigu.search.dto.SkuEsModule;
import com.atguigu.search.vo.SearchResultVo;
import com.atguigu.search.vo.SearchVO;

import java.io.IOException;
import java.util.List;

public interface ProductSearchService {

    List<String> saveProduct(List<SkuEsModule> skuEsModules) throws IOException;

    SearchResultVo searchProduct(SearchVO search) throws IOException;

    SearchResultVo asynSearchProduct(SearchVO search) throws IOException;
}
