package com.atguigu.ware.service;

import com.atguigu.ware.vo.WareQueryVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:22:49
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(WareQueryVO wareQueryVO);
}

