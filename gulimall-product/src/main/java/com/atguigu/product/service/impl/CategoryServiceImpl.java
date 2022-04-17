package com.atguigu.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.product.vo.CategoryTreeVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.product.dao.CategoryDao;
import com.atguigu.product.entity.CategoryEntity;
import com.atguigu.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private ThreadPoolExecutor threadPool;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取分类数据 并封装成节点格式
     *
     * @return
     */
    @Override
    public List<CategoryTreeVO> getListTree() {
        List<CategoryTreeVO> list = getList();
        return list;
    }

    /**
     * 获取一级分类
     *
     * @return
     */
    @Override
    public List<CategoryEntity> getOneLevel() {
        //加锁 防止频繁查询数据库
        synchronized (this) {
            //先判断缓存有咩有
            String onelevel = stringRedisTemplate.opsForValue().get("onelevel");
            //缓存有 直接返回
            if (StringUtils.isNotEmpty(onelevel)) {
                List<CategoryEntity> categoryEntities = JSON.parseObject(onelevel, List.class);
                return categoryEntities;
            } else {
                //缓存没有 查询数据量
                List<CategoryEntity> categoryEntities = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
                System.err.println("查询数据库");
                stringRedisTemplate.opsForValue().set("onelevel", JSON.toJSONString(categoryEntities), 3, TimeUnit.HOURS);
                return categoryEntities;
            }
        }

    }

    /**
     * 获取2及分类和三级分类
     *
     * @return
     */
    @Override
    public Map<String, List<CategoryTreeVO>> getCategoryJson() {
        List<CategoryTreeVO> list = list = getList();
        Map<String, List<CategoryTreeVO>> categorys = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            categorys.put(list.get(i).getCatId().toString(), list.get(i).getChildren());
        }
        return categorys;
    }

    //查询数据库或者缓存
    public List<CategoryTreeVO> getList() {
        synchronized (this){
            //判断缓存是否有数据
            String categoryTree = stringRedisTemplate.opsForValue().get("categoryTree");
            //如果缓存中没有 查数据库
            if (StringUtils.isEmpty(categoryTree)) {
//            list = this.baseMapper.selectListTree();
                List<CategoryEntity> categorys = this.list(null);
                List<CategoryTreeVO> list = categorys.stream().filter(item->item.getParentCid().compareTo(0l)==0).map(item->{
                    CategoryTreeVO categoryTrees= new CategoryTreeVO();
                    BeanUtils.copyProperties(item,categoryTrees);
                    categoryTrees.setChildren(getListTree(categorys, item.getCatId()));
                    return categoryTrees;
                }).collect(Collectors.toList());
                stringRedisTemplate.opsForValue().set("categoryTree", JSON.toJSONString(list), 3, TimeUnit.HOURS);
                return list;
            }
            //如果缓存有 直接返回
            List<CategoryTreeVO> list = JSON.parseObject(categoryTree, List.class);
            return list;
        }
    }

    public List<CategoryTreeVO> getListTree(List<CategoryEntity> categorys,Long parentCid){
        if (parentCid!=null){
           List<CategoryTreeVO> collect = categorys.stream().filter(item -> item.getParentCid().compareTo(parentCid)==0).map(i -> {
               CategoryTreeVO categoryTreeVO = new CategoryTreeVO();
               BeanUtils.copyProperties(i,categoryTreeVO);
               List<CategoryTreeVO> listTree = getListTree(categorys, i.getCatId());
               categoryTreeVO.setChildren(listTree);
               return categoryTreeVO;
           }).collect(Collectors.toList());
           return collect;
       }else {
           return null;
       }

    }
}