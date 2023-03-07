package com.szs.assignment.model.refund;

import java.math.BigDecimal;
import java.util.function.Function;

public enum DeductionFormula {

    근로소득세액공제금액(earnedIncomeDeductionAmount()),
    특별세액공제금액((amount) -> amount),
    표준세액공제금액(standardTaxDeductionAmount()),
    퇴직연금세액공제금액(retirementPensionTaxDeductionAmount());

    public Function<BigDecimal, BigDecimal> calculate;

    DeductionFormula(Function<BigDecimal, BigDecimal> calculate) {
        this.calculate = calculate;
    }


    private static Function<BigDecimal, BigDecimal> earnedIncomeDeductionAmount() {
        return (amount) -> amount.multiply(BigDecimal.valueOf(0.55));
    }
    private static Function<BigDecimal, BigDecimal> standardTaxDeductionAmount() {
        return (amount) -> amount.compareTo(BigDecimal.valueOf(130000)) <= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(130000);
    }

    private static Function<BigDecimal, BigDecimal> retirementPensionTaxDeductionAmount() {
        return (amount) -> amount.multiply(BigDecimal.valueOf(0.15));
    }


    public BigDecimal getCalculatedValue(BigDecimal amount) {
        return calculate.apply(amount);
    }
}
