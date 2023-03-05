package com.szs.assignment.service;

import com.szs.assignment.controller.business.dto.RefundDto;
import com.szs.assignment.error.SzsApiException;
import com.szs.assignment.model.entity.*;
import com.szs.assignment.model.json.SzsJsonBody;
import com.szs.assignment.model.json.SzsRequestBody;
import com.szs.assignment.repository.ScrapHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessService {

    private final ScrapHistoryRepository scrapHistoryRepository;
    private final WebClient webClient;

    private final String SZS_URL = "https://codetest.3o3.co.kr/v2/scrap";


    @Transactional
    public void scrap(UserInfo user) {

        postSzs(user.getName(), user.getRegNo())
            .subscribe(szsJsonBody ->
                scrapHistoryRepository.save(
                    mapToScrapHistory(
                            user,
                            szsJsonBody.getData().getJsonList())
                ), throwable -> {
                    throw new SzsApiException(throwable.getMessage());
                });
    }

    public ScrapHistory mapToScrapHistory(UserInfo user, SzsJsonBody.SzsJsonList szsResponse) {

        ScrapHistory scrapHistory = new ScrapHistory(
                user,
                szsResponse.get급여()
                        .stream()
                        .findFirst()
                        .map(Salary::newSalary)
                        .orElseGet(() ->
                                new Salary()
                        ),
                szsResponse.get산출세액()
        );

        scrapHistory.addAllDeduction(
                Deduction.listOf(scrapHistory, szsResponse.get소득공제())
        );

        return scrapHistory;
    }

    public Mono<SzsJsonBody> postSzs(String name, String regNo) {
        return webClient.post()
                .uri(SZS_URL)
                .header("Content-Type", "application/json")
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(new SzsRequestBody(name, regNo))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                    clientResponse.bodyToMono(String.class)
                        .flatMap(body ->
                                Mono.error(new SzsApiException(body))
                        ))
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                    clientResponse.bodyToMono(String.class)
                        .flatMap(body ->
                                Mono.error(new SzsApiException(body))
                        ))
                .bodyToMono(SzsJsonBody.class);
    }

    public RefundDto calculateRefund(BigDecimal totalAmount, List<Deduction> deductions) {

        BigDecimal 근로소득세액공제 = calculateIncomeTaxDeduction(totalAmount).setScale(0, RoundingMode.HALF_UP);
        BigDecimal 특별세액공제 = calculateSpecialTaxDeduction(totalAmount,deductions).setScale(0, RoundingMode.HALF_UP);
        BigDecimal 표준세액공제 = calculateStandardTaxDeduction(특별세액공제).setScale(0, RoundingMode.HALF_UP);
        if (표준세액공제.compareTo(BigDecimal.valueOf(130000)) == 0 ){
            특별세액공제 = BigDecimal.valueOf(0);
        }
        BigDecimal 퇴직연금세액공제 = calculateRetirementTaxDeduction(deductions).setScale(0, RoundingMode.HALF_UP);

        BigDecimal temp = totalAmount.subtract(근로소득세액공제)
                .subtract(특별세액공제)
                .subtract(표준세액공제)
                .subtract(퇴직연금세액공제).setScale(0, RoundingMode.HALF_UP);

        BigDecimal 결정세액 = temp.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : temp;


        return new RefundDto(근로소득세액공제,특별세액공제,표준세액공제,퇴직연금세액공제,결정세액);
    }


    public BigDecimal calculateIncomeTaxDeduction(BigDecimal totalAmount) {
        return DeductionFormula.근로소득세액공제금액.getCalculatedValue(totalAmount);
    }

    public BigDecimal calculateSpecialTaxDeduction(BigDecimal totalAmount,List<Deduction> deductions) {
        return deductions
                .stream()
                .filter(deduction ->
                        deduction.getType().isApplySpecialDeduction())
                .map(deduction ->
                        deduction.getType().getCalculatedValue(deduction.getAmount(), totalAmount))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateStandardTaxDeduction(BigDecimal specialTaxDeduction) {
        return DeductionFormula.표준세액공제금액.getCalculatedValue(specialTaxDeduction);
    }

    public BigDecimal calculateRetirementTaxDeduction(List<Deduction> deductions) {
        return deductions
                .stream()
                .filter(deduction ->
                        IncomeType.퇴직연금 == deduction.getType())
                .map(deduction ->
                        DeductionFormula.퇴직연금세액공제금액.getCalculatedValue(deduction.getTotalAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
