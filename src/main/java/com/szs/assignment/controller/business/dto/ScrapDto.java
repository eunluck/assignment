package com.szs.assignment.controller.business.dto;

import com.szs.assignment.controller.BaseDto;
import com.szs.assignment.model.entity.ScrapHistory;
import com.szs.assignment.model.json.SzsJsonBody;
import com.szs.assignment.model.json.SzsJsonBody.SzsDeduction;
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
    public static class Response extends BaseDto {

        private List<SzsDeduction> deductions;
        private SzsJsonBody.SzsSalary salary;
        private String resultTaxAmount;

        public Response(ScrapHistory scrapHistory) {
            super(scrapHistory);
            this.deductions = scrapHistory.getDeductions().stream().map(SzsDeduction::new).collect(Collectors.toList());
            this.salary = new SzsJsonBody.SzsSalary(scrapHistory.getSalary());
            this.resultTaxAmount = scrapHistory.getResultTaxAmount().toString();
        }
    }


}
