package com.szs.assignment.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MessageUtilsTest {

    @Test
    @DisplayName("BigDecimal 변환을 위해 천단위 콤마를 제거한다")
    void removeThousandsSign() {

            //given
            String test_1 = "100,000,000";
            String test_2 = "1,000";
            String test_3 = "100";

            //when
            String formatted_1 = MessageUtils.removeThousandsSign(test_1);
            String formatted_2 = MessageUtils.removeThousandsSign(test_2);
            String formatted_3 = MessageUtils.removeThousandsSign(test_3);

            //then
            assertEquals("100000000", formatted_1);
            assertEquals("1000", formatted_2);
            assertEquals("100", formatted_3);

    }

    @Test
    @DisplayName("API 반환을 위해 천단위 콤마를 추가한다")
    void moneyFormatting() {

        //given
        BigDecimal test_1 = BigDecimal.valueOf(100000000);
        BigDecimal test_2 = BigDecimal.valueOf(1000);
        BigDecimal test_3 = BigDecimal.valueOf(100);

        //when
        String formatted_1 = MessageUtils.moneyFormatting(test_1);
        String formatted_2 = MessageUtils.moneyFormatting(test_2);
        String formatted_3 = MessageUtils.moneyFormatting(test_3);

        //then
        assertEquals("100,000,000", formatted_1);
        assertEquals("1,000", formatted_2);
        assertEquals("100", formatted_3);


    }
}