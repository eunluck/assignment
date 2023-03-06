package com.szs.assignment.configure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.szs.assignment.util.MessageUtils;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.text.DecimalFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Configuration
public class ServiceConfigure {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().clientConnector((method, uri, requestCallback) -> {
            HttpClient httpClient = HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .responseTimeout(Duration.ofSeconds(30))
                    .doOnConnected(conn ->
                            conn.addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                                    .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS)));

            ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

            return connector.connect(method, uri, requestCallback);
        }).build();
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        MessageUtils.setMessageSourceAccessor(messageSourceAccessor);
        return messageSourceAccessor;
    }
    @Bean
    public Jackson2ObjectMapperBuilder configureObjectMapper() {
        JavaTimeModule jtm = new JavaTimeModule();
        jtm.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder() {
            @Override
            public void configure(ObjectMapper objectMapper) {
                super.configure(objectMapper);
                objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            }
        };
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.modulesToInstall(jtm);
        return builder;
    }
}
