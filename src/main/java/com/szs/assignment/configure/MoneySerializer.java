package com.szs.assignment.configure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MoneySerializer extends JsonSerializer<BigDecimal> {
    private static final DecimalFormat df = new DecimalFormat("#,###");

    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {

        jsonGenerator.writeString(moneyFormatting(value));
    }
    public static String moneyFormatting(BigDecimal value) {
        value.setScale(0, RoundingMode.HALF_UP);
        return df.format(value);
    }
}
