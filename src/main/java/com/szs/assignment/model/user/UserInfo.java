package com.szs.assignment.model.user;

import com.szs.assignment.configure.security.CryptoConverter;
import com.szs.assignment.model.BaseEntity;
import com.szs.assignment.model.refund.ScrapHistory;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table( indexes = {
    @Index(name = "idx__user_id", columnList = "userId", unique = true)
})
@SQLDelete(sql = "UPDATE user_info SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@EqualsAndHashCode(callSuper = true)
public class UserInfo extends BaseEntity {
    private String userId;
    private String password;
    private String name;
    @Convert(converter = CryptoConverter.class)
    private String regNo;

    @OneToMany(mappedBy = "user")
    @OrderBy("seq DESC")
    private List<ScrapHistory> scrapHistories;


    public void login(PasswordEncoder passwordEncoder, String credentials) {
        if (!passwordEncoder.matches(credentials, password))
            throw new BadCredentialsException("패스워드를 확인해주세요.");
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
    public String newApiToken(Jwt jwt, String[] roles) {
        Jwt.Claims claims = Jwt.Claims.of(getSeq(), userId,name ,roles);
        return jwt.newToken(claims);
    }

    public void addScrapHistory(ScrapHistory scrapHistory) {
        this.scrapHistories.add(scrapHistory);
    }
    public static UserInfo join(String userId, String password, String name, String regNo) {
        return new UserInfo(userId,password, name, regNo);

    }
    public UserInfo(String userId,String password, String name, String regNo){
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.regNo = regNo;
    }
    public UserInfo(Long seq, String userId,String password, String name, String regNo){
        super(seq);
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.regNo = regNo;
    }
}
