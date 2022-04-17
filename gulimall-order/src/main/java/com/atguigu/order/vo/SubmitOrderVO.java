package com.atguigu.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubmitOrderVO {

    private Long addrId;
    private BigDecimal payPrice;
    private String orderToken;
    private  Long memberId;
    private String note;
}
