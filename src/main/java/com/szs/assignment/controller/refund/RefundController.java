package com.szs.assignment.controller.refund;

import static com.szs.assignment.controller.ApiResult.OK;

import com.szs.assignment.controller.ApiResult;
import com.szs.assignment.controller.refund.dto.RefundDto;
import com.szs.assignment.controller.refund.dto.ScrapDto;
import com.szs.assignment.controller.user.dto.JwtAuthentication;
import com.szs.assignment.error.NotFoundException;
import com.szs.assignment.model.refund.ScrapHistory;
import com.szs.assignment.model.user.UserInfo;
import com.szs.assignment.service.RefundService;
import com.szs.assignment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${apiPrefix}")
public class RefundController {

    private final RefundService refundService;
    private final UserService userService;

    @Tag(name = "환급 API")
    @GetMapping(path = "refund")
    @Operation(summary = "환급액 확인", description = "스크랩된 정보로 환급액을 조회한다.")
    public ApiResult<RefundDto.Response> viewRefundAmount(
        @AuthenticationPrincipal JwtAuthentication auth
    ) {

        ScrapHistory scrapHistory =
            userService.findBySeq(auth.getSeq())
                .orElseThrow(() ->
                    new NotFoundException(UserInfo.class, auth.getSeq()))
                .getScrapHistories()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                    new IllegalStateException("스크랩된 정보가 없습니다."));

        return OK(
            new RefundDto.Response(
                auth.getName(),
                refundService.calculateRefund(
                    scrapHistory.getResultTaxAmount(),
                    scrapHistory.getSalary().getTotalAmount(),
                    scrapHistory.getDeductions()))
        );
    }

    @Tag(name = "환급 API")
    @PutMapping(path = "scrap")
    @Operation(summary = "스크랩", description = "외부에서 자산 정보 스크랩.")
    public ApiResult<Void> scrapFromSzs(
        @AuthenticationPrincipal JwtAuthentication auth
    ) {

        refundService.scrap(
            userService.findBySeq(auth.getSeq()).orElseThrow(() ->
                new NotFoundException(UserInfo.class, auth.getSeq()))
        );

        return OK(null);
    }

    @Tag(name = "환급 API")
    @GetMapping(path = "scrap")
    @Operation(summary = "스크랩 결과 확인", description = "외부에서 스크랩된 정보 확인")
    public ApiResult<ScrapDto.Response> scrapResult(
        @AuthenticationPrincipal JwtAuthentication auth
    ) {
        return OK(
            userService.findBySeq(auth.getSeq())
                .orElseThrow(() ->
                    new NotFoundException(UserInfo.class, auth.getSeq()))
                .getScrapHistories()
                .stream()
                .findFirst()
                .map(ScrapDto.Response::new)
                .orElse(null)
        );
    }
}
