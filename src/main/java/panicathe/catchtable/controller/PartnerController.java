package panicathe.catchtable.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.store.CreateOrUpdateStoreDTO;
import panicathe.catchtable.service.PartnerService;

import java.time.LocalDate;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Partner Controller", description = "파트너 관련 API")
public class PartnerController {

    private final PartnerService partnerService;

    @PostMapping("/store/add")
    @Operation(summary = "상점 추가", description = "새로운 상점을 추가합니다.")
    public ResponseEntity<ResponseDTO> addStore(@RequestBody @Valid CreateOrUpdateStoreDTO storeDTO, @AuthenticationPrincipal String email) {
        return partnerService.addStore(storeDTO, email);
    }

    @GetMapping("/store")
    @Operation(summary = "파트너의 상점 조회", description = "파트너의 모든 상점을 조회합니다.")
    public ResponseEntity<ResponseDTO> getStores(@AuthenticationPrincipal String email) {
        return partnerService.getStores(email);
    }

    @GetMapping("/store/{storeId}/reviews")
    @Operation(summary = "상점 리뷰 조회", description = "특정 상점의 리뷰를 조회합니다.")
    public ResponseEntity<ResponseDTO> getStoreReviews(@AuthenticationPrincipal String email, @PathVariable Long storeId) {
        return partnerService.getStoreReviews(email, storeId);
    }

    @DeleteMapping("/store/{storeId}/reviews/{reviewId}")
    @Operation(summary = "상점 리뷰 삭제", description = "특정 상점의 리뷰를 삭제합니다.")
    public ResponseEntity<ResponseDTO> deleteReview(@AuthenticationPrincipal String email, @PathVariable Long storeId, @PathVariable Long reviewId) {
        return partnerService.deleteReview(email, storeId, reviewId);
    }

    @GetMapping("/store/reservations")
    @Operation(summary = "상점 예약 정보 조회", description = "특정 날짜의 상점 예약 정보를 조회합니다.")
    public ResponseEntity<ResponseDTO> getStoreReservations(
            @AuthenticationPrincipal String email,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return partnerService.getStoreReservations(email, date);
    }

    @PostMapping("/store/{storeId}/reservation/{reservationId}/confirm")
    @Operation(summary = "상점 예약 승인", description = "특정 예약을 승인합니다.")
    public ResponseEntity<ResponseDTO> confirmReservation(@AuthenticationPrincipal String email, @PathVariable Long storeId, @PathVariable Long reservationId) {
        return partnerService.confirmReservation(email, storeId, reservationId);
    }

    @PostMapping("/store/{storeId}/reservation/{reservationId}/cancel")
    @Operation(summary = "상점 예약 취소", description = "특정 예약을 취소합니다.")
    public ResponseEntity<ResponseDTO> cancelReservation(@AuthenticationPrincipal String email, @PathVariable Long storeId, @PathVariable Long reservationId) {
        return partnerService.cancelReservation(email, storeId, reservationId);
    }

    @PutMapping("/store/{storeId}")
    @Operation(summary = "상점 정보 수정", description = "특정 상점의 정보를 수정합니다.")
    public ResponseEntity<ResponseDTO> updateStore(@AuthenticationPrincipal String email, @PathVariable Long storeId, @RequestBody @Valid CreateOrUpdateStoreDTO storeDTO) {
        return partnerService.updateStore(email, storeId, storeDTO);
    }

    @DeleteMapping("/store/{storeId}")
    @Operation(summary = "상점 삭제", description = "특정 상점을 삭제합니다.")
    public ResponseEntity<ResponseDTO> deleteStore(@AuthenticationPrincipal String email, @PathVariable Long storeId) {
        return partnerService.deleteStore(email, storeId);
    }
}
