package com.szs.assignment.model.user;

import com.szs.assignment.configure.security.CryptoConverter;
import com.szs.assignment.model.BaseEntity;
import javax.persistence.Convert;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table( indexes = {
    @Index(name = "idx__name", columnList = "name"),
    @Index(name = "idx__reg_no", columnList = "regNo", unique = true)
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SQLDelete(sql = "UPDATE can_join_user SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@EqualsAndHashCode(callSuper = true)
public class CanJoinUser extends BaseEntity {

    private String name;
    @Convert(converter = CryptoConverter.class)
    private String regNo;

    public CanJoinUser(Long seq, String name, String regNo){
        super(seq);
        this.name = name;
        this.regNo = regNo;

    }

}
