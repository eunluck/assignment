package com.szs.assignment.controller.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
public class RefundDto {

    BigDecimal 근로소득세액공제;
    BigDecimal 특별세액공제;
    BigDecimal 표준세액공제;
    BigDecimal 퇴직연금세액공제;
    BigDecimal 결정세액;

}
