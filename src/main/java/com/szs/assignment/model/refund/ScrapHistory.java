package com.szs.assignment.model.refund;

import com.google.common.collect.Lists;
import com.szs.assignment.model.BaseEntity;
import com.szs.assignment.model.user.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE scrap_history SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@EqualsAndHashCode(callSuper = true)
public class ScrapHistory extends BaseEntity {

    @OneToMany(mappedBy = "scrapHistory", cascade = CascadeType.PERSIST)
    private List<Deduction> deductions = Lists.newArrayList();

    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo user;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "totalAmount", column = @Column(name = "total_amount", columnDefinition = "numeric(19,0) default '0'")),
            @AttributeOverride(name = "workStartDate", column = @Column(name = "work_start_date")),
            @AttributeOverride(name = "workEndDate", column = @Column(name = "work_end_date")),
            @AttributeOverride(name = "paymentDate", column = @Column(name = "payment_date")),
            @AttributeOverride(name = "company", column = @Column(name = "company")),
            @AttributeOverride(name = "companyNumber", column = @Column(name = "company_Number"))
    })
    private Salary salary;
    private BigDecimal resultTaxAmount;


    public void addAllDeduction(List<Deduction> deduction) {
        this.deductions.addAll(deduction);
    }

    @Builder
    public ScrapHistory(UserInfo user, Salary salary, BigDecimal resultTaxAmount) {
        this.user = user;
        this.salary = salary;
        this.resultTaxAmount = resultTaxAmount;
    }

}
