package com.atguigu.ware.vo;

import lombok.Data;

@Data
public class WareQueryVO {

    private String page;
    private String limit;
    private String name;
    private String address;
    private String areacode;
}
