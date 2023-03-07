package com.szs.assignment.configure.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

class CryptoConverterTest {

    private CryptoConverter cryptoConverter;
    @BeforeEach
    void setUp() {

        cryptoConverter = new CryptoConverter("testkey3fa3fa3fw3fFAASqwdqsa1232");
    }
    @Test
    @DisplayName("민감한 정보는 암복호화 되어야 한다")
    void convertTest() {

        String encode = cryptoConverter.convertToDatabaseColumn("테스트123");

        String decode = cryptoConverter.convertToEntityAttribute(encode);

        Assertions.assertEquals("테스트123", decode);



    }

}