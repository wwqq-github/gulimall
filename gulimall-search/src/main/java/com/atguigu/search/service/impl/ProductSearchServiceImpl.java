package com.atguigu.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.search.config.SearchConfig;
import com.atguigu.search.dto.SkuEsModule;
import com.atguigu.search.service.ProductSearchService;
import com.atguigu.search.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private ThreadPoolExecutor threadPool;
    @Override
    public List<String> saveProduct(List<SkuEsModule> skuEsModules) throws IOException {
        BulkRequest request = new BulkRequest();
        for (SkuEsModule skuEsModule : skuEsModules) {
            request.add(new IndexRequest("product").id(skuEsModule.getSkuId().toString())
                    .source(JSON.toJSONString(skuEsModule), XContentType.JSON));
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(request, SearchConfig.COMMON_OPTIONS);
        boolean b = bulkResponse.hasFailures();
        if (b) {
            List<String> collect = Arrays.asList(bulkResponse.getItems()).stream().map(item -> item.getId()).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    @Override
    public SearchResultVo searchProduct(SearchVO search) {
        SearchResponse searchResponse = getSearchResponse(search);
        SearchResultVo searchResultVo = getSearchResultVo(searchResponse,search);
        return searchResultVo;
    }

    @Override
    public SearchResultVo asynSearchProduct(SearchVO search) throws IOException {
        SearchResponse searchResponse = asynSearchResponse(search);
        SearchResultVo searchResultVo = asynSearchResultVo(searchResponse,search);
        return searchResultVo;
    }

    //分类检索结果 封装成SearchResultVo
    private SearchResultVo getSearchResultVo(SearchResponse searchResponse,SearchVO search){
        SearchResultVo result = new SearchResultVo();
        ArrayList<Product> products = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits()) {
            SkuEsModule skuEsModule = JSONObject.parseObject(searchHit.getSourceAsString(), SkuEsModule.class);
            Product product = new Product();
            product.setSkuId(skuEsModule.getSkuId());
            product.setSkuImg(skuEsModule.getSkuDefaultImg());
            product.setSkuPrice(skuEsModule.getSkuPrice());
            product.setSkuTitle(skuEsModule.getSkuTitle());
            products.add(product);
        }
        result.setProducts(products);
        //封装品牌聚合信息
        ParsedLongTerms brandAgg = searchResponse.getAggregations().get("brand_agg");
        List<Brand> brands = brandAgg.getBuckets().stream().map(item -> {
            Brand brand = new Brand();
            brand.setBrandId(item.getKeyAsNumber().longValue());
            ParsedStringTerms brandNameAgg = item.getAggregations().get("brandName_agg");
            brand.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());
            ParsedStringTerms brandImageAgg = item.getAggregations().get("brandImg_agg");
            brand.setBrandImg(brandImageAgg.getBuckets().get(0).getKeyAsString());
            return brand;
        }).collect(Collectors.toList());
        result.setBrands(brands);
        //封装分类聚合信息
        ParsedLongTerms catalogAgg = searchResponse.getAggregations().get("catalogId_agg");
        List<Catelog> catelogs = catalogAgg.getBuckets().stream().map(item -> {
            Catelog catelog = new Catelog();
            catelog.setCatelogId(item.getKeyAsNumber().longValue());
            ParsedStringTerms catalogNameAgg = item.getAggregations().get("catalogName_agg");
            catelog.setCatelogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());
            return catelog;
        }).collect(Collectors.toList());
        result.setCatelogs(catelogs);
        //封装attr聚合信息
        ParsedNested attrAgg = searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrId_agg");
        List<Attr> attrs = attrIdAgg.getBuckets().stream().map(item->{
            Attr attr = new Attr();
            ParsedStringTerms attrNameAgg = item.getAggregations().get("attrName_agg");
            attr.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
            ParsedStringTerms attrValueAgg = item.getAggregations().get("attrValue_agg");
            List<AttrValue> values = attrValueAgg.getBuckets().stream().map(attrValue -> {
                AttrValue value = new AttrValue();
                value.setAttrId(item.getKeyAsNumber().longValue());
                value.setAttrValue(attrValue.getKeyAsString());
                return value;
            }).collect(Collectors.toList());
            attr.setAttrValues(values);
            return attr;
        }).collect(Collectors.toList());
        result.setAttrs(attrs);

        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        result.setTotalPages(totalHits.value/40l+1l);

        result.setTotal(totalHits.value);
        result.setPageNum(search.getPageNum()==null?1:search.getPageNum());

        return result;
    }

    //组装el的查询语句
    private SearchResponse getSearchResponse(SearchVO search) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("product");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //关键字查询
        if (StringUtils.isNotEmpty(search.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", search.getKeyword()));

        }
        //CatelogId查询
        if (search.getCatelogId() != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("catalogId", search.getCatelogId()));
        }

        //品牌查询
        if (search.getBrandId() != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("brandId", search.getBrandId()));
        }
        //属性查询
        if(search.getAttrs()!=null&&search.getAttrs().size()!=0){

            for (String attr:search.getAttrs()) {
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                if (s.length==2){
                    String[] attrValues = s[1].split(":");//这个属性检索用的值
                    String[] attrVals = s[1].split(":");//这个属性检索用的值
                    query.must(QueryBuilders.termQuery("attrs.attrId",s[0]));
                    query.must(QueryBuilders.termsQuery("attrs.attrValue",attrVals));

                    NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs",query, ScoreMode.None);
                    boolQueryBuilder.filter(nestedQueryBuilder);
                }

            }
        }

        //价格筛选
        if (StringUtils.isNotEmpty(search.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] pricts = search.getSkuPrice().split("_");
            int i = search.getSkuPrice().indexOf("_");
            if (i == 0) {
                BigDecimal max=new BigDecimal(search.getSkuPrice().substring(1));
                rangeQuery.gt(0);
                rangeQuery.lt(max);
            }else if (search.getSkuPrice().length()-i==1){
                BigDecimal min=new BigDecimal(pricts[0]);
                rangeQuery.gt(min);
            }
            else {
                BigDecimal min=new BigDecimal(pricts[0]);
                BigDecimal max=new BigDecimal(pricts[1]);
                if (max.compareTo(min)==1){
                    rangeQuery.gt(min);
                    rangeQuery.lt(max);
                }
            }
            boolQueryBuilder.filter(rangeQuery);
        }
        //排序
        if (StringUtils.isNotEmpty(search.getSort())){
            String[] s = search.getSort().split("_");
            if (s.length==2){
                SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
                searchSourceBuilder.sort(s[0],order);

            }
        }
        //品牌聚合分析
        searchSourceBuilder.query(boolQueryBuilder);
        TermsAggregationBuilder brandIdAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        brandIdAgg.subAggregation(AggregationBuilders.terms("brandName_agg").field("brandName")).size(100);
        brandIdAgg.subAggregation(AggregationBuilders.terms("brandImg_agg").field("brandImg")).size(100);
        searchSourceBuilder.aggregation(brandIdAgg);

        //分类聚合信息
        TermsAggregationBuilder catalogIdAgg = AggregationBuilders.terms("catalogId_agg").field("catalogId").size(50);
        catalogIdAgg.subAggregation(AggregationBuilders.terms("catalogName_agg").field("catalogName")).size(100);
        searchSourceBuilder.aggregation(catalogIdAgg);

        //属性聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attdIdAgg = AggregationBuilders.terms("attrId_agg").field("attrs.attrId").size(50);
        attdIdAgg.subAggregation(AggregationBuilders.terms("attrName_agg").field("attrs.attrName")).size(100);
        attdIdAgg.subAggregation(AggregationBuilders.terms("attrValue_agg").field("attrs.attrValue")).size(100);
        nested.subAggregation(attdIdAgg);


        searchSourceBuilder.aggregation(nested);

        searchSourceBuilder.size(40);
        if (search.getPageNum()!=null){
            Long pageNum=(search.getPageNum()-1)*40;
            searchSourceBuilder.from(Integer.valueOf(pageNum.toString()));

        }

        searchRequest.source(searchSourceBuilder);
        System.err.println(searchRequest.source());
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, SearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponse;
    }

    //异步封装检索结果 封装成SearchResultVo
    private SearchResultVo asynSearchResultVo(SearchResponse searchResponse,SearchVO search){
        SearchResultVo result = new SearchResultVo();
        CompletableFuture<Void> productsAsync = CompletableFuture.runAsync(() -> {
            ArrayList<Product> products = new ArrayList<>();
            for (SearchHit searchHit : searchResponse.getHits()) {
                SkuEsModule skuEsModule = JSONObject.parseObject(searchHit.getSourceAsString(), SkuEsModule.class);
                Product product = new Product();
                product.setSkuId(skuEsModule.getSkuId());
                product.setSkuImg(skuEsModule.getSkuDefaultImg());
                product.setSkuPrice(skuEsModule.getSkuPrice());
                product.setSkuTitle(skuEsModule.getSkuTitle());
                products.add(product);
            }
            result.setProducts(products);
        }, threadPool);
        CompletableFuture<Void> brandsAsync = CompletableFuture.runAsync(() -> {
            //封装品牌聚合信息
            ParsedLongTerms brandAgg = searchResponse.getAggregations().get("brand_agg");
            List<Brand> brands = brandAgg.getBuckets().stream().map(item -> {
                Brand brand = new Brand();
                brand.setBrandId(item.getKeyAsNumber().longValue());
                ParsedStringTerms brandNameAgg = item.getAggregations().get("brandName_agg");
                brand.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());
                ParsedStringTerms brandImageAgg = item.getAggregations().get("brandImg_agg");
                brand.setBrandImg(brandImageAgg.getBuckets().get(0).getKeyAsString());
                return brand;
            }).collect(Collectors.toList());
            result.setBrands(brands);
        }, threadPool);
        CompletableFuture<Void> catelogsAsync = CompletableFuture.runAsync(() -> {
            //封装分类聚合信息
            ParsedLongTerms catalogAgg = searchResponse.getAggregations().get("catalogId_agg");
            List<Catelog> catelogs = catalogAgg.getBuckets().stream().map(item -> {
                Catelog catelog = new Catelog();
                catelog.setCatelogId(item.getKeyAsNumber().longValue());
                ParsedStringTerms catalogNameAgg = item.getAggregations().get("catalogName_agg");
                catelog.setCatelogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());
                return catelog;
            }).collect(Collectors.toList());
            result.setCatelogs(catelogs);
        }, threadPool);
        CompletableFuture<Void> attrsAsync = CompletableFuture.runAsync(() -> {
            //封装attr聚合信息
            ParsedNested attrAgg = searchResponse.getAggregations().get("attr_agg");
            ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrId_agg");
            List<Attr> attrs = attrIdAgg.getBuckets().stream().map(item->{
                Attr attr = new Attr();
                ParsedStringTerms attrNameAgg = item.getAggregations().get("attrName_agg");
                attr.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
                ParsedStringTerms attrValueAgg = item.getAggregations().get("attrValue_agg");
                List<AttrValue> values = attrValueAgg.getBuckets().stream().map(attrValue -> {
                    AttrValue value = new AttrValue();
                    value.setAttrId(item.getKeyAsNumber().longValue());
                    value.setAttrValue(attrValue.getKeyAsString());
                    return value;
                }).collect(Collectors.toList());
                attr.setAttrValues(values);
                return attr;
            }).collect(Collectors.toList());
            result.setAttrs(attrs);
        }, threadPool);
        CompletableFuture<Void> totalHitsAsync = CompletableFuture.runAsync(() -> {
            TotalHits totalHits = searchResponse.getHits().getTotalHits();
            result.setTotalPages(totalHits.value/40l+1l);
            result.setTotal(totalHits.value);
            result.setPageNum(search.getPageNum()==null?1:search.getPageNum());
        }, threadPool);
        return result;
    }

    //异步组装el的查询语句
    private SearchResponse asynSearchResponse(SearchVO search) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("product");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        CompletableFuture<Void> keywordAsync = CompletableFuture.runAsync(() -> {
            //关键字查询
            if (StringUtils.isNotEmpty(search.getKeyword())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", search.getKeyword()));

            }
        }, threadPool);
        CompletableFuture<Void> catelogIdAsync = CompletableFuture.runAsync(() -> {
            //CatelogId查询
            if (search.getCatelogId() != null) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("catalogId", search.getCatelogId()));
            }
        }, threadPool);
        CompletableFuture<Void> brandIdAsync = CompletableFuture.runAsync(() -> {
            //品牌查询
            if (search.getBrandId() != null) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("brandId", search.getBrandId()));
            }
        }, threadPool);
        CompletableFuture<Void> attrsAsync = CompletableFuture.runAsync(() -> {
            //属性查询
            if(search.getAttrs()!=null&&search.getAttrs().size()!=0){

                for (String attr:search.getAttrs()) {
                    BoolQueryBuilder query = QueryBuilders.boolQuery();
                    String[] s = attr.split("_");
                    if (s.length==2){
                        String[] attrValues = s[1].split(":");//这个属性检索用的值
                        String[] attrVals = s[1].split(":");//这个属性检索用的值
                        query.must(QueryBuilders.termQuery("attrs.attrId",s[0]));
                        query.must(QueryBuilders.termsQuery("attrs.attrValue",attrVals));

                        NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs",query, ScoreMode.None);
                        boolQueryBuilder.filter(nestedQueryBuilder);
                    }

                }
            }
        }, threadPool);
        CompletableFuture<Void> priceAsync = CompletableFuture.runAsync(() -> {
            //价格筛选
            if (StringUtils.isNotEmpty(search.getSkuPrice())){
                RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
                String[] pricts = search.getSkuPrice().split("_");
                int i = search.getSkuPrice().indexOf("_");
                if (i == 0) {
                    BigDecimal max=new BigDecimal(search.getSkuPrice().substring(1));
                    rangeQuery.gt(0);
                    rangeQuery.lt(max);
                }else if (search.getSkuPrice().length()-i==1){
                    BigDecimal min=new BigDecimal(pricts[0]);
                    rangeQuery.gt(min);
                }
                else {
                    BigDecimal min=new BigDecimal(pricts[0]);
                    BigDecimal max=new BigDecimal(pricts[1]);
                    if (max.compareTo(min)==1){
                        rangeQuery.gt(min);
                        rangeQuery.lt(max);
                    }
                }
                boolQueryBuilder.filter(rangeQuery);
            }
        }, threadPool);
        CompletableFuture<Void> sortAsync = CompletableFuture.runAsync(() -> {
            //排序
            if (StringUtils.isNotEmpty(search.getSort())){
                String[] s = search.getSort().split("_");
                if (s.length==2){
                    SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
                    searchSourceBuilder.sort(s[0],order);

                }
            }
        }, threadPool);
        CompletableFuture<Void> brandAggAsync = CompletableFuture.runAsync(() -> {
            //品牌聚合分析
            searchSourceBuilder.query(boolQueryBuilder);
            TermsAggregationBuilder brandIdAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
            brandIdAgg.subAggregation(AggregationBuilders.terms("brandName_agg").field("brandName")).size(100);
            brandIdAgg.subAggregation(AggregationBuilders.terms("brandImg_agg").field("brandImg")).size(100);
            searchSourceBuilder.aggregation(brandIdAgg);

        }, threadPool);
        CompletableFuture<Void> catalogIdAggAsync = CompletableFuture.runAsync(() -> {
            //分类聚合信息
            TermsAggregationBuilder catalogIdAgg = AggregationBuilders.terms("catalogId_agg").field("catalogId").size(50);
            catalogIdAgg.subAggregation(AggregationBuilders.terms("catalogName_agg").field("catalogName")).size(100);
            searchSourceBuilder.aggregation(catalogIdAgg);
        }, threadPool);
        CompletableFuture<Void> attdIdAggAsync = CompletableFuture.runAsync(() -> {
            //属性聚合
            NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
            TermsAggregationBuilder attdIdAgg = AggregationBuilders.terms("attrId_agg").field("attrs.attrId").size(50);
            attdIdAgg.subAggregation(AggregationBuilders.terms("attrName_agg").field("attrs.attrName")).size(100);
            attdIdAgg.subAggregation(AggregationBuilders.terms("attrValue_agg").field("attrs.attrValue")).size(100);
            nested.subAggregation(attdIdAgg);
            searchSourceBuilder.aggregation(nested);

        }, threadPool);
        CompletableFuture<Void> pagingAsync = CompletableFuture.runAsync(() -> {
            searchSourceBuilder.size(40);
            if (search.getPageNum()!=null){
                Long pageNum=(search.getPageNum()-1)*40;
                searchSourceBuilder.from(Integer.valueOf(pageNum.toString()));

            }
        }, threadPool);
        CompletableFuture allOf = CompletableFuture.allOf(keywordAsync, catelogIdAsync, brandIdAsync,
                brandIdAsync, attrsAsync, priceAsync, sortAsync,
                brandAggAsync, catalogIdAggAsync, attdIdAggAsync, pagingAsync);
        searchRequest.source(searchSourceBuilder);
        allOf.join();
        System.err.println(searchRequest.source());
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, SearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponse;
    }
}
