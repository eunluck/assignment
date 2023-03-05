package com.szs.assignment.model.entity;

import java.math.BigDecimal;
import java.util.function.Function;
public enum IncomeType {
    보험료((amount) ->  amount.multiply(BigDecimal.valueOf(0.12)),true),
    교육비((amount) ->  amount.multiply(BigDecimal.valueOf(0.15)),true),
    기부금((amount) ->  amount.multiply(BigDecimal.valueOf(0.15)),true),
    의료비((amount) ->  amount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : amount.multiply(BigDecimal.valueOf(0.15)),true),
    퇴직연금((amount) ->  amount.multiply(BigDecimal.valueOf(0.15)),false);

    private final Function<BigDecimal,BigDecimal> specialDeductionCalculator;
    private final boolean applySpecialDeduction;

    IncomeType(Function<BigDecimal, BigDecimal> specialDeductionCalculator, boolean applySpecialDeduction) {
        this.specialDeductionCalculator = specialDeductionCalculator;
        this.applySpecialDeduction = applySpecialDeduction;
    }



    public BigDecimal getCalculatedValue(BigDecimal amount, BigDecimal totalAmount) {
        if(IncomeType.의료비.equals(this)){
            amount = amount.subtract(totalAmount.multiply(BigDecimal.valueOf(0.03)));
        }
        return getCalculatedValue(amount);
    }

    public BigDecimal getCalculatedValue(BigDecimal amount) {

        return specialDeductionCalculator.apply(amount);
    }


    public boolean isApplySpecialDeduction() {
        return applySpecialDeduction;
    }
}
