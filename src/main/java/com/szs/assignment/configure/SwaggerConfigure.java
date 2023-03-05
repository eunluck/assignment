package com.szs.assignment.configure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfigure implements WebMvcConfigurer {
    @Value("${apiPrefix}")
    private String apiPrefix;


    @Bean
    public GroupedOpenApi restApi() {
        return GroupedOpenApi.builder()
            .group(apiPrefix)
            .pathsToMatch("/**")
            .build();
    }

    @Bean
    public OpenAPI restOpenApi() {
        SpringDocUtils.getConfig()
            .addAnnotationsToIgnore(AuthenticationPrincipal.class);

        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("Authorization", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("jwt")
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization")))
            .addSecurityItem(new SecurityRequirement().addList("Authorization"))
            .info(
                new Info().title("szs-API")
                    .description("Szs 과제전형 API 문서"))
            .externalDocs(new ExternalDocumentation()
                .description("by 은행운")
                .url("skgoddns1@gmail.com"));

    }



}