package com.atguigu.ware.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.atguigu.ware.feign.ProductFeign;
import com.atguigu.ware.vo.SkuQueryVO;
import com.atguigu.ware.vo.WarePurchaseitemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.ware.entity.PurchaseDetailEntity;
import com.atguigu.ware.service.PurchaseDetailService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:22:49
 */
@RestController
@RequestMapping("ware/purchasedetail")
public class PurchaseDetailController {
    @Autowired
    private PurchaseDetailService purchaseDetailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(WarePurchaseitemVO warePurchaseitemVO) {
        PageUtils page = purchaseDetailService.queryPage(warePurchaseitemVO);

        return R.ok().put("page",page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseDetailEntity purchaseDetail = purchaseDetailService.getById(id);
        return R.ok().put("purchaseDetail", purchaseDetail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseDetailEntity purchaseDetail) {
        purchaseDetailService.savePurchaseDetail(purchaseDetail);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseDetailEntity purchaseDetail) {
        purchaseDetailService.updateById(purchaseDetail);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseDetailService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }


    public static List<Long> skuids = new ArrayList<>();

    static {
        skuids.add(1505054653392326658l);
        skuids.add(1505054669628477442l);
        skuids.add(1505054677861896194l);
        skuids.add(1505054686456025090l);
        skuids.add(1505054694584586242l);
        skuids.add(1505054701333221378l);
        skuids.add(1505054781318598658l);
        skuids.add(1505054788180480002l);
        skuids.add(1505054806476034049l);
        skuids.add(1505054812482277378l);
        skuids.add(1505054819163803649l);
        skuids.add(1505054825413316609l);
        skuids.add(1505054893465899010l);
        skuids.add(1505054901665763330l);
        skuids.add(1505054908062076930l);
        skuids.add(1505054914345144322l);
        skuids.add(1505054921563541505l);
        skuids.add(1505054927813054466l);
        skuids.add(1505054970024529922l);
        skuids.add(1505054977515556865l);
        skuids.add(1505054985660895233l);
        skuids.add(1505054993839788034l);
        skuids.add(1505055003713179650l);
        skuids.add(1505055011401338881l);
        skuids.add(1505055055923875842l);
        skuids.add(1505055062899003393l);
        skuids.add(1505055069689581570l);
        skuids.add(1505055077335797761l);
        skuids.add(1505055084378034177l);
        skuids.add(1505055090921148417l);
        skuids.add(1505055186572251137l);
        skuids.add(1505055192964370433l);
        skuids.add(1505055199029334018l);
        skuids.add(1505055207531188226l);
        skuids.add(1505055213625511937l);
        skuids.add(1505055220499976194l);
        skuids.add(1505055263990714369l);
        skuids.add(1505055271334940674l);
        skuids.add(1505055277513150465l);
        skuids.add(1505055285020954626l);
        skuids.add(1505055293015298049l);
        skuids.add(1505055301584261122l);
        skuids.add(1505055347260231682l);
        skuids.add(1505055354210193409l);
        skuids.add(1505055361239846914l);
        skuids.add(1505055368659570689l);
        skuids.add(1505055376188346369l);
        skuids.add(1505055382324613122l);
        skuids.add(1505055430370365442l);
        skuids.add(1505055437320327170l);
        skuids.add(1505055444081545218l);
        skuids.add(1505055449181818881l);
        skuids.add(1505055454143680513l);
        skuids.add(1505055460347056130l);
        skuids.add(1505055508006932482l);
        skuids.add(1505055515149832193l);
        skuids.add(1505055521256738818l);
        skuids.add(1505055528613548034l);
        skuids.add(1505055535089553410l);
        skuids.add(1505055540609257473l);
        skuids.add(1505055602508795906l);
        skuids.add(1505055609794301953l);
        skuids.add(1505055617780256770l);
        skuids.add(1505055624793133057l);
        skuids.add(1505055632330297345l);
        skuids.add(1505055639917793281l);
        skuids.add(1505055695135805442l);
        skuids.add(1505055700768755713l);
        skuids.add(1505055704963059713l);
        skuids.add(1505055712672190465l);
        skuids.add(1505055722361032706l);
        skuids.add(1505055730464428034l);
    }
    @RequestMapping("/saves")
    public R saves() {
        List<PurchaseDetailEntity> list =skuids.stream().map(item->{
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setStatus(0);
            purchaseDetailEntity.setSkuId(item);
            purchaseDetailEntity.setSkuNum(1000);
            purchaseDetailEntity.setWareId(2l);
            purchaseDetailService.savePurchaseDetail(purchaseDetailEntity);
            return purchaseDetailEntity;

        }).collect(Collectors.toList());
//        purchaseDetailService.saveBatch(list);
        return null;
    }
}
