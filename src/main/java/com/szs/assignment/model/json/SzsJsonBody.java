package com.szs.assignment.model.json;

import static com.szs.assignment.util.MessageUtils.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.szs.assignment.model.refund.Deduction;
import com.szs.assignment.model.refund.IncomeType;
import com.szs.assignment.model.refund.Salary;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SzsJsonBody {
    private String status;
    private SzsData data;
    private Map<String, Object> errors;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SzsData {
        private SzsJsonList jsonList;
        private String appVer;
        private String errMsg;

    }

    @Data
    public static class SzsJsonList {
        private List<SzsSalary> 급여;
        private String 산출세액;
        private List<SzsDeduction> 소득공제;

        public Optional<SzsSalary> get급여() {
            return 급여.stream().findFirst();
        }

        public BigDecimal get산출세액() {
            return new BigDecimal(removeThousandsSign(산출세액));
        }

    }

    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SzsSalary {
        @JsonIgnore
        private String 이름;
        @JsonIgnore
        private String 주민등록번호;
        private String 소득내역;
        private String 총지급액;
        private String 업무시작일;
        private String 기업명;
        private String 지급일;
        private String 업무종료일;
        private String 소득구분;
        private String 사업자등록번호;

        public BigDecimal get총지급액() {
            return new BigDecimal(removeThousandsSign(총지급액));
        }

        public LocalDate get업무시작일() {
            return 업무시작일 == null ? null : LocalDate.parse(업무시작일.replace(".", "-"));
        }

        public LocalDate get지급일() {

            return 지급일 == null ? null : LocalDate.parse(지급일.replace(".", "-"));
        }

        public LocalDate get업무종료일() {

            return 업무종료일 == null ? null : LocalDate.parse(업무종료일.replace(".", "-"));
        }

        public SzsSalary(Salary salary) {
            this.소득내역 = salary.getIncomeDetails();
            this.총지급액 = salary.getTotalAmount().toString();
            this.업무시작일 = salary.getWorkStartDate().toString();
            this.기업명 = salary.getCompany();
            this.지급일 = salary.getPaymentDate().toString();
            this.업무종료일 = salary.getWorkEndDate().toString();
            this.소득구분 = salary.getIncomeType();
            this.사업자등록번호 = salary.getCompanyNumber();
        }
    }

    @AllArgsConstructor
    @ToString
    @NoArgsConstructor
    public static class SzsDeduction {
        private String 금액;
        private String 소득구분;

        private String 총납임금액;

        public IncomeType get소득구분() {
            return IncomeType.valueOf(소득구분);
        }

        public BigDecimal get금액() {
            return new BigDecimal(
                    removeThousandsSign(
                            Optional.ofNullable(금액)
                                    .orElseGet(() -> "0"))).setScale(0);
        }

        public BigDecimal get총납임금액() {
            return new BigDecimal(
                    removeThousandsSign(
                            Optional.ofNullable(총납임금액)
                                    .orElseGet(() -> "0"))).setScale(3, RoundingMode.DOWN);
        }

        public SzsDeduction(Deduction deduction){
            this.금액 = deduction.getAmount().toString();
            this.소득구분 = deduction.getType().name();
            this.총납임금액 = deduction.getTotalAmount().toString();
        }

        public SzsDeduction formatting(){
            this.금액 = moneyFormatting(금액);
            this.소득구분 = moneyFormatting(소득구분);
            this.총납임금액 = moneyFormatting(총납임금액);
            return this;
        }
    }

}