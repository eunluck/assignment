package com.szs.assignment.controller.refund.dto;

import com.szs.assignment.controller.BaseDto;
import com.szs.assignment.model.refund.ScrapHistory;
import com.szs.assignment.model.json.SzsJsonBody;
import com.szs.assignment.model.json.SzsJsonBody.SzsDeduction;
import com.szs.assignment.util.MessageUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


public class ScrapDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(callSuper = true)
    @Schema(name = "스크랩 결과 조회 성공")
    public static class Response extends BaseDto {

        @Schema(description = "공제 항목")
        private List<SzsDeduction> deductions;
        @Schema(description = "급여")
        private SzsJsonBody.SzsSalary salary;
        @Schema(description = "결정세액", format = "#,####")
        private String resultTaxAmount;

        public Response(ScrapHistory scrapHistory) {
            super(scrapHistory);
            this.deductions = scrapHistory.getDeductions().stream().map(SzsDeduction::new).map(SzsDeduction::formatting).collect(Collectors.toList());
            this.salary = new SzsJsonBody.SzsSalary(scrapHistory.getSalary());
            this.resultTaxAmount = Optional.ofNullable(scrapHistory.getResultTaxAmount()).map(bigDecimal -> MessageUtils.moneyFormatting(bigDecimal.toString())).orElse("0");
        }
    }


}
