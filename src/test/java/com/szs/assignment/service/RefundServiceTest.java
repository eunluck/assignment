package com.szs.assignment.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest
class RefundServiceTest {

    @MockBean
    private WebClient webClient;
    @Autowired
    private RefundService refundService;

    WebTestClient testClient = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:8080")
            .build();

    @Test
    void postSzs() {
    }


}