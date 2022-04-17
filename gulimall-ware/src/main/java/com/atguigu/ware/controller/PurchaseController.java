package com.atguigu.ware.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.ware.vo.MergeVO;
import com.atguigu.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.ware.entity.PurchaseEntity;
import com.atguigu.ware.service.PurchaseService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;


/**
 * 采购信息
 *
 * @author zwq
 * @email s1196652915@qq.com
 * @date 2022-01-12 15:22:50
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;


    /**
     * 完成采购单功能
     */
    @RequestMapping("/done")
    public R done(@RequestBody PurchaseDoneVO purchaseDoneVO) {
        purchaseService.done(purchaseDoneVO);
        return R.ok();
    }
    private static List<Long> ids=new ArrayList<>();
    static {
        ids.add(1505057192359694338l);
        ids.add(1505057192883982338l);
        ids.add(1505057193269858305l);
        ids.add(1505057193664122882l);
        ids.add(1505057194121302017l);
        ids.add(1505057194775613441l);
        ids.add(1505057195157295105l);
        ids.add(1505057195551559682l);
        ids.add(1505057196012933122l);
        ids.add(1505057196403003393l);
        ids.add(1505057196730159105l);
        ids.add(1505057197128617985l);
        ids.add(1505057197585797122l);
        ids.add(1505057198110085122l);
        ids.add(1505057198504349698l);
        ids.add(1505057199158661122l);
        ids.add(1505057199548731394l);
        ids.add(1505057200085602305l);
        ids.add(1505057200479866882l);
        ids.add(1505057201138372609l);
        ids.add(1505057201650077698l);
        ids.add(1505057202178560002l);
        ids.add(1505057202572824577l);
        ids.add(1505057203046780930l);
        ids.add(1505057203688509441l);
        ids.add(1505057204216991746l);
        ids.add(1505057204607062018l);
        ids.add(1505057204997132290l);
        ids.add(1505057205320093697l);
        ids.add(1505057205794050049l);
        ids.add(1505057206108622849l);
        ids.add(1505057206502887426l);
        ids.add(1505057207031369730l);
        ids.add(1505057207421440002l);
        ids.add(1505057207807315970l);
        ids.add(1505057208142860289l);
        ids.add(1505057208524541953l);
        ids.add(1505057208923000833l);
        ids.add(1505057209317265409l);
        ids.add(1505057209648615426l);
        ids.add(1505057210047074306l);
        ids.add(1505057210638471170l);
        ids.add(1505057211242450945l);
        ids.add(1505057211636715521l);
        ids.add(1505057212572045314l);
        ids.add(1505057213566095362l);
        ids.add(1505057214220406786l);
        ids.add(1505057214610477058l);
        ids.add(1505057215008935937l);
        ids.add(1505057215394811905l);
        ids.add(1505057215784882177l);
        ids.add(1505057216242061314l);
        ids.add(1505057216632131585l);
        ids.add(1505057217101893633l);
        ids.add(1505057217424855041l);
        ids.add(1505057217814925314l);
        ids.add(1505057218142081025l);
        ids.add(1505057218540539906l);
        ids.add(1505057218867695617l);
        ids.add(1505057219261960193l);
        ids.add(1505057219664613378l);
        ids.add(1505057220054683650l);
        ids.add(1505057220440559617l);
        ids.add(1505057220893544449l);
        ids.add(1505057221359112194l);
        ids.add(1505057221690462210l);
        ids.add(1505057222474797058l);
        ids.add(1505057223057805314l);
        ids.add(1505057223519178753l);
        ids.add(1505057223842140161l);
        ids.add(1505057224232210434l);
        ids.add(1505057224626475009l);
    }
    /**
     * 批量完成采购单功能
     */
    @RequestMapping("/dones")
    public R dones() {
        PurchaseDoneVO purchaseDoneVO=new PurchaseDoneVO();
        purchaseDoneVO.setId(1503347819019227138l);
        purchaseDoneVO.setItems(ids);
        purchaseService.done(purchaseDoneVO);
        return R.ok();
    }

    /**
     * 合并整单
     */
    @RequestMapping("/merge")
    public R merge(@RequestBody MergeVO mergeVO) {
        purchaseService.updateMerge(mergeVO);
        return R.ok();
    }


    /**
     * 查询没有采购需求的采购单
     */
    @RequestMapping("/unreceive/list")
    public R unreceiveList() {
        List<PurchaseEntity> purchaseEntities = purchaseService.getUnreceiveList();

        return R.ok().put("page", purchaseEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateStatus(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
