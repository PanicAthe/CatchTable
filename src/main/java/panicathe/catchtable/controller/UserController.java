package panicathe.catchtable.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.reservation.CreateReservationDTO;
import panicathe.catchtable.dto.review.CreateOrUpdateReviewDTO;
import panicathe.catchtable.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Controller", description = "유저 관련 API")
public class UserController {
    private final UserService userService;

    @Operation(summary = "상점 목록 조회", description = "정렬 기준을 sortBy 파라미터로 지정할 수 있습니다. (alphabet, distance, rating")
    @GetMapping("/stores")
    public ResponseEntity<ResponseDTO> getStores(
            @RequestParam(required = false, defaultValue = "alphabet") String sortBy,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        return userService.getStores(sortBy, lat, lon);
    }

    @Operation(summary = "상점 상세 정보 조회", description = "상점 ID에 해당하는 상세 정보를 조회합니다.")
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<ResponseDTO> getStoreDetails(@PathVariable Long storeId) {
        return userService.getStoreDetails(storeId);
    }

    @Operation(summary = "상점 키워드 조회", description = "주어진 키워드로 상점 목록을 조회합니다.")
    @GetMapping("/stores/search")
    public ResponseEntity<ResponseDTO> getStoreNamesByKeyword(@RequestParam String keyword) {
        return userService.getStoreNamesByKeyword(keyword);
    }

    @Operation(summary = "예약하기", description = "사용자가 상점을 예약합니다.")
    @PostMapping("/reservation")
    public ResponseEntity<ResponseDTO> makeReservation(
            @RequestBody @Valid CreateReservationDTO reservationDTO,
            @AuthenticationPrincipal String email) {
        return userService.makeReservation(reservationDTO, email);
    }

    @Operation(summary = "방문 확정", description = "예약 시간 10분 전까지만 방문을 확정합니다.")
    @PostMapping("/reservations/{reservationId}/confirm")
    public ResponseEntity<ResponseDTO> confirmVisit(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal String email) {
        return userService.confirmVisit(reservationId, email);
    }

    @Operation(summary = "유저 본인의 예약 조회", description = "사용자가 만든 예약을 조회합니다.")
    @GetMapping("/reservations")
    public ResponseEntity<ResponseDTO> getUserReservations(@AuthenticationPrincipal String email) {
        return userService.getUserReservations(email);
    }

    @Operation(summary = "리뷰 작성", description = "예약에 대한 리뷰를 작성합니다.")
    @PostMapping("/reviews/{reservationId}")
    public ResponseEntity<ResponseDTO> writeReview(
            @RequestBody @Valid CreateOrUpdateReviewDTO reviewDTO,
            @AuthenticationPrincipal String email, Long reservationId) {
        return userService.writeReview(reviewDTO, email, reservationId);
    }

    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다.")
    @PutMapping("/reviews/{reviewedId}")
    public ResponseEntity<ResponseDTO> updateReview(
            @RequestBody @Valid CreateOrUpdateReviewDTO createOrUpdateReviewDTO,
            @AuthenticationPrincipal String email,
            @PathVariable int reviewedId) {
        return userService.updateReview(createOrUpdateReviewDTO, email, reviewedId);
    }

    @Operation(summary = "유저 본인의 리뷰 확인", description = "사용자가 작성한 리뷰를 확인합니다.")
    @GetMapping("/reviews")
    public ResponseEntity<ResponseDTO> getUserReviews(@AuthenticationPrincipal String email) {
        return userService.getUserReviews(email);
    }
}
