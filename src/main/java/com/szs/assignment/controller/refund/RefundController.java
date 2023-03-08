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
    @Operation(summary = "환급액 확인", description = "스크랩된 정보로 환급액을 계산해 조회합니다. ")
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
    @Operation(summary = "스크랩 요청", description = "외부에서 자산 정보를 스크랩해 DB에 저장하는 요청을 보냅니다. "
        + "외부 호출은 최장 20초가 소요되기에 비동기 호출로 조회되기 때문에 해당 API의 결과는 항상 true입니다. "
        + "스크랩 결과는 GET scrap API를 통해 조회할 수 있습니다.")
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
    @Operation(summary = "스크랩 결과 확인", description = "가장 최근 스크랩 된 결과 한건을 조회합니다. "
        + "null일 시 아직 스크랩이 완료 되지 않은 상태입니다. ")
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
