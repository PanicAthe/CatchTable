package panicathe.catchtable.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.StoreDTO;
import panicathe.catchtable.service.PartnerService;

import java.time.LocalDate;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class PartnerController {

    private final PartnerService partnerService;

    // 파트너 정보 확인 (샘플 메소드)
    @PostMapping
    public ResponseEntity<String> getPartner(
            @Parameter(name = "Authorization", description = "Bearer [JWT 토큰]", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
            @RequestHeader("Authorization") String authorizationHeader,
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(email + " details");
    }

    // 상점 추가
    @PostMapping("/store/add")
    public ResponseEntity<ResponseDTO> addStore(@RequestBody StoreDTO storeDTO, @AuthenticationPrincipal String email) {
        return partnerService.addStore(storeDTO, email);
    }

    // 파트너의 모든 상점 조회
    @GetMapping("/store")
    public ResponseEntity<ResponseDTO> getStores(@AuthenticationPrincipal String email) {
        return partnerService.getStores(email);
    }

    // 파트너의 상점 리뷰 조회
    @GetMapping("/store/{storeId}/reviews")
    public ResponseEntity<ResponseDTO> getStoreReviews(@AuthenticationPrincipal String email, @PathVariable Long storeId) {
        return partnerService.getStoreReviews(email, storeId);
    }

    // 상점 리뷰 삭제
    @DeleteMapping("/store/{storeId}/reviews/{reviewId}")
    public ResponseEntity<ResponseDTO> deleteReview(@AuthenticationPrincipal String email, @PathVariable Long storeId, @PathVariable Long reviewId) {
        return partnerService.deleteReview(email, storeId, reviewId);
    }

    // 상점 예약 정보 조회 (특정 날짜)
    @GetMapping("/store/reservations")
    public ResponseEntity<ResponseDTO> getStoreReservations(
            @AuthenticationPrincipal String email,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return partnerService.getStoreReservations(email, date);
    }

    // 상점 예약 승인
    @PostMapping("/store/{storeId}/reservation/{reservationId}/confirm")
    public ResponseEntity<ResponseDTO> confirmReservation(@AuthenticationPrincipal String email, @PathVariable Long storeId, @PathVariable Long reservationId) {
        return partnerService.confirmReservation(email, storeId, reservationId);
    }

    // 상점 예약 취소
    @PostMapping("/store/{storeId}/reservation/{reservationId}/cancel")
    public ResponseEntity<ResponseDTO> cancelReservation(@AuthenticationPrincipal String email, @PathVariable Long storeId, @PathVariable Long reservationId) {
        return partnerService.cancelReservation(email, storeId, reservationId);
    }

    // 상점 정보 수정
    @PutMapping("/store/{storeId}")
    public ResponseEntity<ResponseDTO> updateStore(@AuthenticationPrincipal String email, @PathVariable Long storeId, @RequestBody StoreDTO storeDTO) {
        return partnerService.updateStore(email, storeId, storeDTO);
    }

    // 상점 삭제
    @DeleteMapping("/store/{storeId}")
    public ResponseEntity<ResponseDTO> deleteStore(@AuthenticationPrincipal String email, @PathVariable Long storeId) {
        return partnerService.deleteStore(email, storeId);
    }
}
