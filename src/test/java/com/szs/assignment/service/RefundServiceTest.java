package com.szs.assignment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szs.assignment.controller.refund.dto.RefundDto;
import com.szs.assignment.model.json.SzsJsonBody;
import com.szs.assignment.model.json.SzsRequestBody;
import com.szs.assignment.model.refund.Deduction;
import com.szs.assignment.model.refund.IncomeType;
import com.szs.assignment.model.refund.Salary;
import com.szs.assignment.model.refund.ScrapHistory;
import com.szs.assignment.model.user.UserInfo;
import com.szs.assignment.repository.ScrapHistoryRepository;
import io.swagger.v3.core.util.ObjectMapperFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@ExtendWith(value = MockitoExtension.class)
class RefundServiceTest {


    @Mock
    private ScrapHistoryRepository scrapHistoryRepository;
    private RefundService refundService;

    private UserInfo user;
    @Mock
    private WebClient webClient;
    private WebTestClient webTestClient;
    private ObjectMapper objectMapper;
    private SzsJsonBody szsExampleJsonBody;
    @Mock
    private ExchangeFunction exchangeFunction;

    @BeforeEach
    void setUp() {
        user = new UserInfo(1L,"eunluck","123","김둘리","921108-1582816");

        webTestClient = WebTestClient.bindToServer().baseUrl("https://codetest.3o3.co.kr/v2/scrap").responseTimeout(Duration.ofSeconds(30)).build();
        webClient = WebClient.builder()
            .exchangeFunction(exchangeFunction)
            .build();

        objectMapper = ObjectMapperFactory.buildStrictGenericObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/김둘리.json")) {
            String jsonString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            szsExampleJsonBody = objectMapper.readValue(jsonString, SzsJsonBody.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        refundService = new RefundService(scrapHistoryRepository,webClient);
    }

    @Test
    @DisplayName("szsApi를 호출해 세무 정보를 가져온다.")
    void postSzs() {

        webTestClient.post().uri("https://codetest.3o3.co.kr/v2/scrap")
            .body(Mono.just(new SzsRequestBody(user.getName(), user.getRegNo())), SzsRequestBody.class)
            .exchange()
            .expectStatus().isOk()
            .expectBody(SzsJsonBody.class)
            .value(szsJsonBody -> {
                assertAll(
                    () -> assertNotNull(szsJsonBody),
                    () -> assertDoesNotThrow(() ->objectMapper.writeValueAsString(szsJsonBody)),
                    () -> assertEquals("success",szsJsonBody.getStatus()),
                    () -> assertThat(szsJsonBody.getData().getJsonList().get급여()).isNotNull(),
                    () -> assertThat(szsJsonBody.getData().getJsonList().get산출세액()).isNotNull(),
                    () -> assertThat(szsJsonBody.getData().getJsonList().get소득공제()).isNotNull()
                );
            });

    }

    @Test
    @DisplayName("SzsJsonBody로 받은 데이터를 엔티티로 변환한다")
    void mapToScrapHistory() {
        ScrapHistory scrapHistory = refundService.mapToScrapHistory(
            user,
            szsExampleJsonBody.getData().getJsonList());

        assertAll(
            () -> assertThat(scrapHistory).isNotNull(),
            () -> assertEquals(scrapHistory.getUser().getName(), "김둘리")
        );
    }


    @Test
    @DisplayName("근로소득세액공제금액을 계산한다.")
    void calculateIncomeTaxDeduction() {
        //given
        BigDecimal totalAmount = BigDecimal.valueOf(1000000);

        //when
        BigDecimal result = refundService.calculateIncomeTaxDeduction(totalAmount);

        //then
        BigDecimal expected = BigDecimal.valueOf(550000);

        assertEquals(expected, result.setScale(0, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("특별세액공제 합을 계산한다.")
    void calculateSpecialTaxDeduction() {
        //given
        BigDecimal 산출세액 = BigDecimal.valueOf(1000000); // 백만
        BigDecimal 총급여 = BigDecimal.valueOf(30000000); // 3천만
        Deduction 보험료 = new Deduction(null, BigDecimal.valueOf(100000), null, IncomeType.보험료); // 10만
        Deduction 의료비 = new Deduction(null,BigDecimal.valueOf(100000), null, IncomeType.의료비);
        Deduction 교육비 = new Deduction(null,BigDecimal.valueOf(100000), null, IncomeType.교육비);
        Deduction 기부금 = new Deduction(null,BigDecimal.valueOf(100000), null, IncomeType.기부금);

        //when
        BigDecimal result = refundService.calculateSpecialTaxDeduction(총급여,List.of(보험료, 의료비, 교육비, 기부금));

        /*
        * 의료비 계산
        * 30000000 × 0.03 = 900000
        * 100000 - 900000 = -800000
        *  -800000 * 15 = -12000000
        * 의료비공제금액 = 0;
        * */

        //then
        BigDecimal 보험료Expected = BigDecimal.valueOf(12000);
        BigDecimal 의료비Expected = BigDecimal.valueOf(0);
        BigDecimal 교육비Expected = BigDecimal.valueOf(15000);
        BigDecimal 기부금Expected = BigDecimal.valueOf(15000);
        
        BigDecimal expected = BigDecimal.valueOf(42000);

        assertEquals(expected.setScale(0, RoundingMode.HALF_UP), result.setScale(0, RoundingMode.HALF_UP));

    }

    @Test
    @DisplayName("표준세액공제금액을 계산한다.")
    void calculateStandardTaxDeduction() {
        assertAll(
            () -> assertEquals(BigDecimal.valueOf(0), refundService.calculateStandardTaxDeduction(BigDecimal.valueOf(120000))),
            () -> assertEquals(BigDecimal.valueOf(0), refundService.calculateStandardTaxDeduction(BigDecimal.valueOf(130000))),
            () -> assertEquals(BigDecimal.valueOf(130000),refundService.calculateStandardTaxDeduction(BigDecimal.valueOf(150000)))
        );
    }

    @Test
    @DisplayName("퇴직연금세액공제금액을 계산한다.")
    void calculateRetirementTaxDeduction() {
        //given
        Deduction 퇴직연금 = new Deduction(null,null, BigDecimal.valueOf(100000), IncomeType.퇴직연금);

        //when
        BigDecimal result = refundService.calculateRetirementTaxDeduction(List.of(퇴직연금));

        //then
        BigDecimal expected = BigDecimal.valueOf(15000);

        assertEquals(expected, result.setScale(0, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("세액공제금액을 최종 계산한다.")
    void calculateRefund() {
        //given
        BigDecimal 산출세액 = BigDecimal.valueOf(1000000); // 백만
        BigDecimal 총급여 = BigDecimal.valueOf(30000000); // 3천만

        RefundDto refundDto = refundService.calculateRefund(
            산출세액,
            총급여,
            List.of(
                new Deduction(null, BigDecimal.valueOf(100000), null, IncomeType.보험료),
                new Deduction(null, BigDecimal.valueOf(100000), null, IncomeType.의료비),
                new Deduction(null, BigDecimal.valueOf(100000), null, IncomeType.교육비),
                new Deduction(null, BigDecimal.valueOf(100000), null, IncomeType.기부금),
                new Deduction(null, null, BigDecimal.valueOf(100000), IncomeType.퇴직연금)
            ));

    }
}