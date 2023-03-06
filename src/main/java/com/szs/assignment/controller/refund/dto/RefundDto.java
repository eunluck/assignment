package com.szs.assignment.controller.refund.dto;

import static com.szs.assignment.util.MessageUtils.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@ToString
public class RefundDto {

    BigDecimal 근로소득세액공제;
    BigDecimal 특별세액공제;
    BigDecimal 표준세액공제;
    BigDecimal 퇴직연금세액공제;
    BigDecimal 결정세액;

    public static class Response {
        @Schema(description = "이름")
        String 은행운;
        @Schema(description = "근로소득세액공제",format = "#,####")
        String 근로소득세액공제;
        @Schema(description = "특별세액공제",format = "#,####")
        String 특별세액공제;
        @Schema(description = "표준세액공제",format = "#,####")
        String 표준세액공제;
        @Schema(description = "퇴직연금세액공제",format = "#,####")
        String 퇴직연금세액공제;
        @Schema(description = "결정세액",format = "#,####")
        String 결정세액;

        public Response(String 은행운,RefundDto refund) {
            this.은행운 = 은행운;
            this.근로소득세액공제 = moneyFormatting(refund.get근로소득세액공제());
            this.특별세액공제 = moneyFormatting(refund.get특별세액공제());
            this.표준세액공제 = moneyFormatting(refund.get표준세액공제());
            this.퇴직연금세액공제 = moneyFormatting(refund.get퇴직연금세액공제());
            this.결정세액 = moneyFormatting(refund.get결정세액());
        }
    }




}
