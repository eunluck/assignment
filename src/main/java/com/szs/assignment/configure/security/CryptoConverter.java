package com.szs.assignment.configure.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Slf4j
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private final String ALGORITHM;
    private final Key key;

    public CryptoConverter(@Value("${secretKey}") String secretKey) {
        this.ALGORITHM = "AES/ECB/PKCS5Padding";
        this.key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),"AES");
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            return new String(Base64.getEncoder().encode(c.doFinal(attribute.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            log.warn("암호화 오류:{}",attribute);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            return new String(c.doFinal(Base64.getDecoder().decode(dbData.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            log.warn("복호화 오류:{}",dbData);
            throw new RuntimeException(e);
        }
    }

}
