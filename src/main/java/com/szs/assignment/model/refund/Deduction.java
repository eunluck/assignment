package com.szs.assignment.model.refund;

import com.szs.assignment.model.BaseEntity;
import com.szs.assignment.model.json.SzsJsonBody;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE deduction SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@EqualsAndHashCode(callSuper = true)
public class Deduction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_id")
    private ScrapHistory scrapHistory;

    @Column(columnDefinition = "numeric(19,0) default '0'")
    private BigDecimal amount;


    @Column(columnDefinition = "numeric(19,3) default '0'")
    private BigDecimal totalAmount;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private IncomeType type;

    public static List<Deduction> listOf(ScrapHistory scrapHistory,
        List<SzsJsonBody.SzsDeduction> szsDeduction) {
        return szsDeduction
            .stream()
            .map(deduction ->
                new Deduction(
                    scrapHistory,
                    deduction.get금액(),
                    deduction.get총납임금액(),
                    deduction.get소득구분()))
            .collect(Collectors.toList());
    }

}
