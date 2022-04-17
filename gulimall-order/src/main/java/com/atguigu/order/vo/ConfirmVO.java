package com.atguigu.order.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ConfirmVO {

    private List<OrderProduct> orderProducts;

    private List<Address> address;

    private BigDecimal totalPrice;

    private Integer productSum;

    private String token;

    private Long memberId;
}
