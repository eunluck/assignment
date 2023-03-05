package com.szs.assignment.model.entity;

import com.szs.assignment.model.json.SzsJsonBody;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Salary {
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private LocalDate workStartDate;
    private LocalDate workEndDate;
    private LocalDate paymentDate;
    private String company;
    private String incomeDetails;
    private String incomeType;
    private String companyNumber;

    public static Salary newSalary(SzsJsonBody.SzsSalary salary){
        return new Salary(salary.get총지급액(),
                salary.get업무시작일(),
                salary.get업무종료일(),
                salary.get지급일(),
                salary.get기업명(),
                salary.get소득내역(),
                salary.get소득구분(),
                salary.get사업자등록번호());
    }
}
