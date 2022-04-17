package com.atguigu.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.product.dto.SkuFullReductionDO;
import com.atguigu.product.dto.SkuLadderReductionDO;
import com.atguigu.product.dto.SkuUpDto;
import com.atguigu.product.entity.*;
import com.atguigu.product.feign.CouponFeign;
import com.atguigu.product.feign.SearchFeign;
import com.atguigu.product.feign.WareFeign;
import com.atguigu.product.service.*;
import com.atguigu.product.vo.*;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.product.dao.SpuInfoDao;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeign wareFeign;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private ThreadPoolExecutor threadPool;
    @Autowired
    private CouponFeign couponFeign;

    @Autowired
    private SearchFeign searchFeign;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void addSpu(AddSpuVO addSpuVO) {
        //添加商品基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setBrandId(addSpuVO.getBrandId());
        spuInfoEntity.setCatalogId(addSpuVO.getCatalogId());
        spuInfoEntity.setPublishStatus(addSpuVO.getPublishStatus());
        spuInfoEntity.setSpuName(addSpuVO.getSpuName().trim());
        spuInfoEntity.setWeight(addSpuVO.getWeight());
        spuInfoEntity.setSpuDescription(addSpuVO.getSpuDescription().trim());
        spuInfoEntity.setCatalogId(addSpuVO.getCatalogId());
        this.save(spuInfoEntity);
        //添加spu销售信息
        List<ProductAttrValueEntity> productAttrValueEntities = addSpuVO.getBaseAttrs().stream().map(baseAttr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(baseAttr.getAttrId());
            productAttrValueEntity.setAttrName(baseAttr.getAttrName());
            productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
            productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(productAttrValueEntities);
        //添加spu的描述图片
        List<SpuInfoDescEntity> spuInfoDescEntities = addSpuVO.getDecript().stream().map(decript -> {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
            spuInfoDescEntity.setDecript(decript);
            return spuInfoDescEntity;
        }).collect(Collectors.toList());
        spuInfoDescService.saveBatch(spuInfoDescEntities);
        //添加spu主图
        List<SpuImagesEntity> spuImageCollect = addSpuVO.getImages().stream().map(image -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            spuImagesEntity.setImgUrl(image);
            spuImagesEntity.setSpuId(spuInfoEntity.getId());
            return spuImagesEntity;
        }).collect(Collectors.toList());
        spuImagesService.saveBatch(spuImageCollect);
        //添加sku信息
        List<SkuInfoEntity> skuInfoEntities = addSpuVO.getSkus().stream().map(item -> {
            //添加sku信息
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(item, skuInfoEntity);
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setCatalogId(addSpuVO.getCatalogId());
            skuInfoEntity.setBrandId(addSpuVO.getBrandId());
            for (SkuImage skuImage : item.getImages()) {
                if (skuImage.getDefaultImg() == 1) {
                    skuInfoEntity.setSkuDefaultImg(skuImage.getImgUrl());
                }
            }
            skuInfoService.save(skuInfoEntity);
            //添加销售属性
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntitys = item.getAttr().stream().map(attr -> {
                attr.setSkuId(skuInfoEntity.getSkuId());
                return attr;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntitys);
            //添加sku商品照片
            //1497819857020665857
            //1497819857020665857
            List<SkuImagesEntity> collect1 = item.getImages().stream().map(image -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setImgUrl(image.getImgUrl());
                skuImagesEntity.setDefaultImg(image.getDefaultImg());
                skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                return skuImagesEntity;
            }).filter(image -> !StringUtils.isEmpty(image.getImgUrl())).collect(Collectors.toList());
            skuImagesService.saveBatch(collect1);

            //添加sku优惠信息
            SkuFullReductionDO skuFullReductionDO = new SkuFullReductionDO();
            skuFullReductionDO.setSkuId(skuInfoEntity.getSkuId());
            skuFullReductionDO.setFullPrice(item.getFullPrice());
            skuFullReductionDO.setReducePrice(item.getReducePrice());
            skuFullReductionDO.setAddOther(item.getCountStatus());
            SkuLadderReductionDO aDo = new SkuLadderReductionDO();
            aDo.setSkuId(skuInfoEntity.getSkuId());
            aDo.setFullCount(item.getFullCount());
            aDo.setDiscount(item.getDiscount());
            aDo.setAddOther(item.getPriceStatus());
            addCoupon(skuFullReductionDO, aDo);
            return skuInfoEntity;
        }).collect(Collectors.toList());
    }

    /**
     * 上架商品
     *
     * @param spuId
     */
    @Override
    public void upSku(Long spuId) {
        //上架商品 先封装SkuEsModule
        //1. 先查询spk包含的所有sku
        List<SkuInfoEntity> skus = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        //查询sku的可检索属性
        QueryWrapper<AttrEntity> attrWrapper = new QueryWrapper<>();
        attrWrapper.eq("catelog_id",skus.get(0).getCatalogId());
        attrWrapper.eq("search_type",1);
        List<AttrEntity> attrEntities = attrService.getBaseMapper().selectList(attrWrapper);
        List<Long> longList = attrEntities.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //查询spu包含的基本信息
        List<ProductAttrValueEntity> attrValues = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).in("attr_id",longList));
        List<ProductAttrValueEntity> collect = attrValues.stream().map(item -> {
            return item;
        }).collect(Collectors.toList());
        //收集所有的skuid
        List<Long> skuIds = skus.stream().map(item -> {
            return item.getSkuId();
        }).collect(Collectors.toList());
        //远程查库存
        R r = wareFeign.stockAndAddr(skuIds);
        List<SkuUpDto> data = (List<SkuUpDto>) r.getData(new TypeReference<List<SkuUpDto>>() {
        });
        Map<Long, SkuUpDto> upDtoMap = data.stream().collect(Collectors.toMap(SkuUpDto::getSkuId, skuUpDto -> {
            return skuUpDto;
        }));
        //封装数据
        List<SkuEsModule> skuEsModules = skus.stream().map(item -> {
            SkuEsModule skuEsModule = new SkuEsModule();
            BrandEntity brandEntity = brandService.getById(item.getBrandId());
            BeanUtils.copyProperties(item, skuEsModule);
            skuEsModule.setSkuPrice(item.getSkuPrice());
            skuEsModule.setHasStock(upDtoMap.get(item.getSkuId()).isHasStock());
            skuEsModule.setWareId(upDtoMap.get(item.getSkuId()).getWareId());
            skuEsModule.setWareAddress(upDtoMap.get(item.getSkuId()).getWareAddress());
            skuEsModule.setBrandName(brandEntity.getName());
            skuEsModule.setCatalogName(categoryService.getById(item.getCatalogId()).getName());
            skuEsModule.setAttrs(attrValues);
            skuEsModule.setBrandImg(brandEntity.getLogo());
            return skuEsModule;
        }).collect(Collectors.toList());
        searchFeign.saveProduct(skuEsModules);
        SpuInfoEntity spuInfoEntity=new SpuInfoEntity();
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setPublishStatus(1);
        this.updateById(spuInfoEntity);
    }

    /**
     * 获取spk的基本信息 和属性信息 和销售信息
     * @param skuId
     * @return
     */
    @Override
    public SpuItemVO getSpuItem(Long skuId) {
        SpuItemVO spuItemVO = new SpuItemVO();
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        spuItemVO.setSkuInfo(skuInfoEntity);
        //获取sku对应的所有属性信息
        List<ProductAttrValueEntity> productAttrValues = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", skuInfoEntity.getSpuId()));
        Map<Long,ProductAttrValueEntity> map=new HashMap<>();
        //收集属性信息的所有id
        List<Long> attrIds = productAttrValues.stream().map(item -> {
            map.put(item.getAttrId(),item);
            return item.getAttrId();
        }).collect(Collectors.toList());
        //获取属性信息
        List<AttrEntity> attrs = attrService.list(new QueryWrapper<AttrEntity>().in("attr_id", attrIds));
        List<Long> attrGroupIds = attrs.stream().map(item -> {
            return item.getAttrGroupId();
        }).distinct().collect(Collectors.toList());
        //获取属性涉及的所有分组
        List<AttrGroupEntity> attrGroups = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().in("attr_group_id", attrGroupIds).eq("catelog_id",skuInfoEntity.getCatalogId()));
        //封装数据
        List<AttrAndGroupVO> collect = attrGroups.stream().map(item -> {
            AttrAndGroupVO attrAndGroupVO = new AttrAndGroupVO();
            attrAndGroupVO.setAttrGroupName(item.getAttrGroupName());
            List<Long> list = attrs.stream().filter(a->a.getAttrGroupId().equals(item.getAttrGroupId())).map(a->{
                return a.getAttrId();
            }).collect(Collectors.toList());
            List<ProductAttrValueEntity> pavs = list.stream().map(attrId->{return map.get(attrId);}).collect(Collectors.toList());
            attrAndGroupVO.setAttrs(pavs);
            return attrAndGroupVO;
        }).collect(Collectors.toList());

        spuItemVO.setAttrAndGroupVOS(collect);
        //获取spu的详细信息图片
        List<SpuInfoDescEntity> descs = spuInfoDescService.list(new QueryWrapper<SpuInfoDescEntity>().eq("spu_id", skuInfoEntity.getSpuId()));
        List<String> descImages = descs.stream().map(item -> {
            return item.getDecript();
        }).collect(Collectors.toList());
        spuItemVO.setSpuDecriptImage(descImages);

//        获取sku的所有的照片
        List<SkuImagesEntity> skuImages = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id",skuId));
        List<String> images = skuImages.stream().map(item -> {
            return item.getImgUrl();
        }).collect(Collectors.toList());
        spuItemVO.setSkuImage(images);

        //获取sku的所有销售属性
        //获取所有的sku
        List<SkuInfoEntity> skus = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", skuInfoEntity.getSpuId()));
        //收集skuId
        List<Long> skuIds = skus.stream().map(item -> {
            return item.getSkuId();
        }).collect(Collectors.toList());
        //获取sku涉及的所有销售属性
        List<SkuSaleAttrValueEntity> attrValues = skuSaleAttrValueService.list(new QueryWrapper<SkuSaleAttrValueEntity>().in("sku_id", skuIds));
        List<SkuSaleAttrValueEntity> collect1 = attrValues.stream().map(item -> {
            return item;
        }).collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SkuSaleAttrValueEntity::getAttrName))), ArrayList::new));

        List<SellAttr> sellAttrs = collect1.stream().map(item -> {
            List<String> values = attrValues.stream().filter(a -> item.getAttrName().equals(a.getAttrName())).map(a -> {
                return a.getAttrValue();
            }).distinct().collect(Collectors.toList());
            SellAttr sellAttr= new SellAttr();
            sellAttr.setAttrId(item.getAttrId());
            sellAttr.setAttrName(item.getAttrName());
            List<SellAndSkuVO> sellAndSkuVOS = values.stream().map(v -> {
                SellAndSkuVO sellAndSkuVO = new SellAndSkuVO();
                sellAndSkuVO.setAttrValue(v);
                List<String> ids = attrValues.stream().filter(a -> a.getAttrValue().equals(v)).map(a -> {
                    return a.getSkuId()+"";
                }).distinct().collect(Collectors.toList());
                sellAndSkuVO.setSkuIds(addValue(ids));
                return sellAndSkuVO;
            }).collect(Collectors.toList());
            sellAttr.setValues(sellAndSkuVOS);
            return sellAttr;

        }).collect(Collectors.toList());
        spuItemVO.setSellAttrs(sellAttrs);
        return spuItemVO;
    }


    public String addValue(List<String> attrValue){
        if (attrValue==null||attrValue.size()==0){
            return null;
        }else if (attrValue.size()==1){
            return attrValue.get(0);
        }else {
            StringBuffer value=new StringBuffer();
            for (int i=0;i<attrValue.size();i++){
                if (i==attrValue.size()-1) {
                    value.append(attrValue.get(i));
                }else {
                    value.append(attrValue.get(i)+",");
                }

            }
            return value.toString();
        }
    }


    public void addCoupon(SkuFullReductionDO full, SkuLadderReductionDO ladder) {
        if (full.getFullPrice().compareTo(new BigDecimal("0")) == 1 && full.getReducePrice().compareTo(new BigDecimal("0")) == 1) {
            couponFeign.saveFull(full);
        }
        if (ladder.getFullCount() > 0 && ladder.getDiscount().compareTo(new BigDecimal("0")) == 1) {
            couponFeign.saveLadder(ladder);
        }

    }


    //异步添加spu
    @Override
    public void asynAddSpu(AddSpuVO addSpuVO) {
        //添加商品基本信息
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setBrandId(addSpuVO.getBrandId());
        spuInfoEntity.setCatalogId(addSpuVO.getCatalogId());
        spuInfoEntity.setPublishStatus(addSpuVO.getPublishStatus());
        spuInfoEntity.setSpuName(addSpuVO.getSpuName().trim());
        spuInfoEntity.setWeight(addSpuVO.getWeight());
        spuInfoEntity.setSpuDescription(addSpuVO.getSpuDescription().trim());
        spuInfoEntity.setCatalogId(addSpuVO.getCatalogId());
        this.save(spuInfoEntity);
        CompletableFuture<Void> attrValue = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                //添加spu销售信息
                List<ProductAttrValueEntity> productAttrValueEntities = addSpuVO.getBaseAttrs().stream().map(baseAttr -> {
                    ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                    productAttrValueEntity.setAttrId(baseAttr.getAttrId());
                    productAttrValueEntity.setAttrName(baseAttr.getAttrName());
                    productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
                    productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
                    productAttrValueEntity.setSpuId(spuInfoEntity.getId());
                    return productAttrValueEntity;
                }).collect(Collectors.toList());
                productAttrValueService.saveBatch(productAttrValueEntities);
            }
        }, threadPool);
        CompletableFuture<Void> spuInfoDesc = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                //添加spu的描述图片
                List<SpuInfoDescEntity> spuInfoDescEntities = addSpuVO.getDecript().stream().map(decript -> {
                    SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
                    spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
                    spuInfoDescEntity.setDecript(decript);
                    return spuInfoDescEntity;
                }).collect(Collectors.toList());
                spuInfoDescService.saveBatch(spuInfoDescEntities);
            }
        }, threadPool);
        CompletableFuture<Void> spuImages = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                //添加spu主图
                List<SpuImagesEntity> spuImageCollect = addSpuVO.getImages().stream().map(image -> {
                    SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                    spuImagesEntity.setImgUrl(image);
                    spuImagesEntity.setSpuId(spuInfoEntity.getId());
                    return spuImagesEntity;
                }).collect(Collectors.toList());
                spuImagesService.saveBatch(spuImageCollect);
            }
        }, threadPool);
        CompletableFuture<Void> skuInfo = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {

                //添加sku信息
                List<SkuInfoEntity> skuInfoEntities = addSpuVO.getSkus().stream().map(item -> {
                    //添加sku信息
                    SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                    BeanUtils.copyProperties(item, skuInfoEntity);
                    skuInfoEntity.setSpuId(spuInfoEntity.getId());
                    skuInfoEntity.setCatalogId(addSpuVO.getCatalogId());
                    skuInfoEntity.setBrandId(addSpuVO.getBrandId());
                    for (SkuImage skuImage : item.getImages()) {
                        if (skuImage.getDefaultImg() == 1) {
                            skuInfoEntity.setSkuDefaultImg(skuImage.getImgUrl());
                        }
                    }
                    skuInfoService.save(skuInfoEntity);
                    //添加销售属性
                    CompletableFuture<Void> saleAttrValue=CompletableFuture.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            //添加销售属性
                            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntitys = item.getAttr().stream().map(attr -> {
                                attr.setSkuId(skuInfoEntity.getSkuId());
                                return attr;
                            }).collect(Collectors.toList());
                            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntitys);

                        }
                    },threadPool);
                    CompletableFuture<Void> skuImages=CompletableFuture.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            //添加sku商品照片
                            //1497819857020665857
                            //1497819857020665857
                            List<SkuImagesEntity> collect1 = item.getImages().stream().map(image -> {
                                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                                skuImagesEntity.setImgUrl(image.getImgUrl());
                                skuImagesEntity.setDefaultImg(image.getDefaultImg());
                                skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                                return skuImagesEntity;
                            }).filter(image -> !StringUtils.isEmpty(image.getImgUrl())).collect(Collectors.toList());
                            skuImagesService.saveBatch(collect1);
                        }
                    },threadPool);
                    CompletableFuture<Void> skuFullReduction=CompletableFuture.runAsync(new Runnable() {
                        @Override
                        public void run() {
                            //添加sku优惠信息
                            SkuFullReductionDO skuFullReductionDO = new SkuFullReductionDO();
                            skuFullReductionDO.setSkuId(skuInfoEntity.getSkuId());
                            skuFullReductionDO.setFullPrice(item.getFullPrice());
                            skuFullReductionDO.setReducePrice(item.getReducePrice());
                            skuFullReductionDO.setAddOther(item.getCountStatus());
                            SkuLadderReductionDO aDo = new SkuLadderReductionDO();
                            aDo.setSkuId(skuInfoEntity.getSkuId());
                            aDo.setFullCount(item.getFullCount());
                            aDo.setDiscount(item.getDiscount());
                            aDo.setAddOther(item.getPriceStatus());
                            addCoupon(skuFullReductionDO, aDo);
                        }
                    },threadPool);
                    return skuInfoEntity;
                }).collect(Collectors.toList());

            }
        }, threadPool);
    }

    /**
     * 异步上架商品
     *1649673202688
     *1649673199325
     *0000000003363
     * 1649673288994
     * 1649673288583
     * 0000000000411
     * @param spuId
     */
    @Override
    public void asynUpSku(Long spuId){
        //上架商品 先封装SkuEsModule
        //1. 先查询spu包含的所有sku
        List<SkuInfoEntity> skus = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        CompletableFuture<List<ProductAttrValueEntity>> attrValues = CompletableFuture.supplyAsync(() -> {
            //查询sku的基本属性
            QueryWrapper<AttrEntity> attrWrapper = new QueryWrapper<>();
            attrWrapper.eq("catelog_id", skus.get(0).getCatalogId()).eq("search_type", 1);
            List<AttrEntity> attrEntities = attrService.getBaseMapper().selectList(attrWrapper);
            List<Long> longList = attrEntities.stream().map(item -> {
                return item.getAttrId();
            }).collect(Collectors.toList());

            //查询spu包含的基本信息的值是多少
            List<ProductAttrValueEntity> values = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId).in("attr_id", longList));
            List<ProductAttrValueEntity> collect = values.stream().map(item -> {
                return item;
            }).collect(Collectors.toList());
            return values;
        }, threadPool);
        CompletableFuture<Map<Long, SkuUpDto>> skuStock = CompletableFuture.supplyAsync(() -> {
            //收集所有的skuId
            List<Long> skuIds = skus.stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());
            //远程查库存
            R r = wareFeign.stockAndAddr(skuIds);
            List<SkuUpDto> data = (List<SkuUpDto>) r.getData(new TypeReference<List<SkuUpDto>>() {
            });
            Map<Long, SkuUpDto> upDtoMap = data.stream().collect(Collectors.toMap(SkuUpDto::getSkuId, skuUpDto -> {
                return skuUpDto;
            }));
            return upDtoMap;
        }, threadPool);

        CompletableFuture<List<SkuEsModule>> skuEsModules = attrValues.thenCombineAsync(skuStock, (t, u) -> {
            //封装数据
            List<SkuEsModule> skuEs = skus.stream().map(item -> {
                SkuEsModule skuEsModule = new SkuEsModule();
                BrandEntity brandEntity = brandService.getById(item.getBrandId());
                BeanUtils.copyProperties(item, skuEsModule);
                skuEsModule.setSkuPrice(item.getSkuPrice());
                skuEsModule.setHasStock(u.get(item.getSkuId()).isHasStock());
                skuEsModule.setWareId(u.get(item.getSkuId()).getWareId());
                skuEsModule.setWareAddress(u.get(item.getSkuId()).getWareAddress());
                skuEsModule.setBrandName(brandEntity.getName());
                skuEsModule.setCatalogName(categoryService.getById(item.getCatalogId()).getName());
                skuEsModule.setAttrs(t);
                skuEsModule.setBrandImg(brandEntity.getLogo());
                return skuEsModule;
            }).collect(Collectors.toList());
            return skuEs;
        }, threadPool);
        try {
            searchFeign.saveProduct(skuEsModules.get());
            SpuInfoEntity spuInfoEntity=new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setPublishStatus(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public SpuItemVO asynSpuItem(Long skuId) {
        SpuItemVO spuItemVO = new SpuItemVO();
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        spuItemVO.setSkuInfo(skuInfoEntity);
        CompletableFuture<Void> attrAndGroup = CompletableFuture.runAsync(() -> {
            //获取sku对应的所有属性信息
            List<ProductAttrValueEntity> productAttrValues = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", skuInfoEntity.getSpuId()));
            Map<Long, ProductAttrValueEntity> map = new HashMap<>();
            //收集属性信息的所有id
            List<Long> attrIds = productAttrValues.stream().map(item -> {
                map.put(item.getAttrId(), item);
                return item.getAttrId();
            }).collect(Collectors.toList());
            //获取属性信息
            List<AttrEntity> attrs = attrService.list(new QueryWrapper<AttrEntity>().in("attr_id", attrIds));
            List<Long> attrGroupIds = attrs.stream().map(item -> {
                return item.getAttrGroupId();
            }).distinct().collect(Collectors.toList());
            //获取属性涉及的所有分组
            List<AttrGroupEntity> attrGroups = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().in("attr_group_id", attrGroupIds).eq("catelog_id", skuInfoEntity.getCatalogId()));
            //封装数据
            List<AttrAndGroupVO> collect = attrGroups.stream().map(item -> {
                AttrAndGroupVO attrAndGroupVO = new AttrAndGroupVO();
                attrAndGroupVO.setAttrGroupName(item.getAttrGroupName());
                List<Long> list = attrs.stream().filter(a -> a.getAttrGroupId().equals(item.getAttrGroupId())).map(a -> {
                    return a.getAttrId();
                }).collect(Collectors.toList());
                List<ProductAttrValueEntity> pavs = list.stream().map(attrId -> {
                    return map.get(attrId);
                }).collect(Collectors.toList());
                attrAndGroupVO.setAttrs(pavs);
                return attrAndGroupVO;
            }).collect(Collectors.toList());
            spuItemVO.setAttrAndGroupVOS(collect);
        }, threadPool);

        CompletableFuture<Void> spuImages = CompletableFuture.runAsync(() -> {
            //获取spu的详细信息图片
            List<SpuInfoDescEntity> descs = spuInfoDescService.list(new QueryWrapper<SpuInfoDescEntity>().eq("spu_id", skuInfoEntity.getSpuId()));
            List<String> descImages = descs.stream().map(item -> {
                return item.getDecript();
            }).collect(Collectors.toList());
            spuItemVO.setSpuDecriptImage(descImages);

        }, threadPool);
        CompletableFuture<Void> skuImages = CompletableFuture.runAsync(() -> {
            //获取sku的所有的照片
            List<SkuImagesEntity> skuImage = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id",skuId));
            List<String> images = skuImage.stream().map(item -> {
                return item.getImgUrl();
            }).collect(Collectors.toList());
            spuItemVO.setSkuImage(images);
        }, threadPool);


        CompletableFuture<Void> skuSellAttrs = CompletableFuture.runAsync(() -> {
            //获取sku的所有销售属性
            //获取所有的sku
            List<SkuInfoEntity> skus = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", skuInfoEntity.getSpuId()));
            //收集skuId
            List<Long> skuIds = skus.stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());
            //获取sku涉及的所有销售属性
            List<SkuSaleAttrValueEntity> attrValues = skuSaleAttrValueService.list(new QueryWrapper<SkuSaleAttrValueEntity>().in("sku_id", skuIds));
            List<SkuSaleAttrValueEntity> collect1 = attrValues.stream().map(item -> {
                return item;
            }).collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SkuSaleAttrValueEntity::getAttrName))), ArrayList::new));

            List<SellAttr> sellAttrs = collect1.stream().map(item -> {
                List<String> values = attrValues.stream().filter(a -> item.getAttrName().equals(a.getAttrName())).map(a -> {
                    return a.getAttrValue();
                }).distinct().collect(Collectors.toList());
                SellAttr sellAttr= new SellAttr();
                sellAttr.setAttrId(item.getAttrId());
                sellAttr.setAttrName(item.getAttrName());
                List<SellAndSkuVO> sellAndSkuVOS = values.stream().map(v -> {
                    SellAndSkuVO sellAndSkuVO = new SellAndSkuVO();
                    sellAndSkuVO.setAttrValue(v);
                    List<String> ids = attrValues.stream().filter(a -> a.getAttrValue().equals(v)).map(a -> {
                        return a.getSkuId()+"";
                    }).distinct().collect(Collectors.toList());
                    sellAndSkuVO.setSkuIds(addValue(ids));
                    return sellAndSkuVO;
                }).collect(Collectors.toList());
                sellAttr.setValues(sellAndSkuVOS);
                return sellAttr;

            }).collect(Collectors.toList());
            spuItemVO.setSellAttrs(sellAttrs);

        }, threadPool);
        return spuItemVO;
    }

}