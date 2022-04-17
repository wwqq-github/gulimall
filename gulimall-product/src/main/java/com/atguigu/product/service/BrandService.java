package com.atguigu.product.service;

import com.atguigu.product.vo.BrandQueryVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:21:09
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(BrandQueryVO brandQueryVO);
}

