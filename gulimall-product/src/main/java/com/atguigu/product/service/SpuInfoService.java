package com.atguigu.product.service;

import com.atguigu.product.vo.AddSpuVO;
import com.atguigu.product.vo.SpuItemVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.SpuInfoEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * spu信息
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:08
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addSpu(AddSpuVO addSpuVO);

    void upSku(Long spuId);

    SpuItemVO getSpuItem(Long skuId);

    void asynAddSpu(AddSpuVO addSpuVO);

    void asynUpSku(Long spuId);

    SpuItemVO asynSpuItem(Long skuId);
}

