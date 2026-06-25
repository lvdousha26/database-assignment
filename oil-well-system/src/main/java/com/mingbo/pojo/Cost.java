package com.mingbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cost {
    private String code;
    private String preunit;
    private String wellcode;
    private BigDecimal premoney;
    private String person;
    private LocalDate predate;
    private LocalDate startdate;
    private LocalDate finish;
    private String settleunit;
    private String content;

    private String mat1Code;
    private Integer mat1Num;
    private BigDecimal mat1Price;
    private BigDecimal mat1Sub;
    private String mat2Code;
    private Integer mat2Num;
    private BigDecimal mat2Price;
    private BigDecimal mat2Sub;
    private String mat3Code;
    private Integer mat3Num;
    private BigDecimal mat3Price;
    private BigDecimal mat3Sub;
    private String mat4Code;
    private Integer mat4Num;
    private BigDecimal mat4Price;
    private BigDecimal mat4Sub;

    private BigDecimal matcost;
    private BigDecimal humancost;
    private BigDecimal equipcost;
    private BigDecimal othercost;
    private BigDecimal settlecost;
    private String settleperson;
    private LocalDate settledate;
    private BigDecimal finalcost;
    private String finalperson;
    private LocalDate finaldate;
}
