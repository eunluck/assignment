package com.szs.assignment.controller.refund;

import static org.junit.jupiter.api.Assertions.*;

import com.szs.assignment.service.RefundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(controllers = RefundController.class)
class RefundControllerTest {

    @MockBean
    private RefundService refundService;


    @Test
    void viewRefundAmount() {
    }

    @Test
    void scrapFromSzs() {
    }

    @Test
    void scrapResult() {
    }
}