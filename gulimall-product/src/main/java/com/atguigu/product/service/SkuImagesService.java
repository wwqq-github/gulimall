package com.atguigu.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.SkuImagesEntity;

import java.util.Map;

/**
 * sku图片
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:08
 */
public interface SkuImagesService extends IService<SkuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

