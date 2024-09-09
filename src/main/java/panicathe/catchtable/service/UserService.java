package panicathe.catchtable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panicathe.catchtable.dto.*;
import panicathe.catchtable.dto.reservation.CreateReservationDTO;
import panicathe.catchtable.dto.reservation.ReservationDetailDTO;
import panicathe.catchtable.dto.review.ReviewDetailForUserDTO;
import panicathe.catchtable.dto.review.CreateOrUpdateReviewDTO;
import panicathe.catchtable.dto.store.StoreByKeywordDTO;
import panicathe.catchtable.dto.store.StoreDetailDTO;
import panicathe.catchtable.exception.CustomException;
import panicathe.catchtable.exception.ErrorCode;
import panicathe.catchtable.model.Reservation;
import panicathe.catchtable.model.Review;
import panicathe.catchtable.model.Store;
import panicathe.catchtable.model.User;
import panicathe.catchtable.repository.ReservationRepository;
import panicathe.catchtable.repository.ReviewRepository;
import panicathe.catchtable.repository.StoreRepository;
import panicathe.catchtable.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    // 상점 목록 조회 (가나다순, 별점순, 거리순)
    public ResponseEntity<ResponseDTO> getStores(String sortBy, Double userLat, Double userLon) {
        List<Store> stores;

        switch (sortBy) {
            case "rating": // 별점순 정렬
                stores = storeRepository.findAllByOrderByAverageRatingDesc();
                break;
            case "distance": // 거리순 정렬
                stores = storeRepository.findAll();
                stores.sort((s1, s2) -> {
                    double distance1 = calculateDistance(userLat, userLon, s1.getLat(), s1.getLon());
                    double distance2 = calculateDistance(userLat, userLon, s2.getLat(), s2.getLon());
                    return Double.compare(distance1, distance2);
                });
                break;
            case "alphabet":
            default: // 가나다순 정렬
                stores = storeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
                break;
        }

        List<StoreDetailDTO> storeDtos = stores.stream()
                .map(store -> StoreDetailDTO.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .lat(store.getLat())
                        .lon(store.getLon())
                        .description(store.getDescription())
                        .averageRating(store.getAverageRating())
                        .reviewCount(store.getReviews().size())
                        .build())
                .collect(Collectors.toList());

        ResponseDTO response = new ResponseDTO("상점 목록 조회 성공", HttpStatus.OK, storeDtos);
        return ResponseEntity.ok(response);
    }

    // 상점 키워드로 조회
    public ResponseEntity<ResponseDTO> getStoreNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);

        // 이름에 키워드를 포함하는 상점 조회
        Page<Store> stores = storeRepository.findByNameContainingIgnoreCase(keyword, limit);

        List<StoreByKeywordDTO> storeDtos = stores.stream()
                .map(store -> StoreByKeywordDTO.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .build())
                .toList();

        ResponseDTO response = new ResponseDTO("상점 목록 조회 성공", HttpStatus.OK, storeDtos);
        return ResponseEntity.ok(response);
    }

    // 거리 계산 함수 (haversine formula를 이용한 계산)
    private double calculateDistance(Double userLat, Double userLon, Double storeLat, Double storeLon) {
        final int R = 6371; // 지구 반지름 (킬로미터)
        double latDistance = Math.toRadians(storeLat - userLat);
        double lonDistance = Math.toRadians(storeLon - userLon);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(storeLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 거리 반환 (킬로미터 단위)
    }

    // 상점 상세 정보
    public ResponseEntity<ResponseDTO> getStoreDetails(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // 리뷰 리스트를 DTO로 변환
        List<ReviewDetailForUserDTO> reviewDTOList = store.getReviews().stream()
                .map(review -> ReviewDetailForUserDTO.builder()
                        .reviewId(review.getId())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .build())
                .collect(Collectors.toList());

        StoreDetailDTO storeDetailDTO = StoreDetailDTO.builder()
                .id(store.getId())
                .name(store.getName())
                .lat(store.getLat())
                .lon(store.getLon())
                .description(store.getDescription())
                .averageRating(store.getAverageRating())
                .reviewCount(store.getReviews().size())
                .reviews(reviewDTOList)
                .build();

        ResponseDTO response = new ResponseDTO("상점 상세 정보 조회 성공", HttpStatus.OK, storeDetailDTO);
        return ResponseEntity.ok(response);
    }

    // 상점 예약
    @Transactional
    public ResponseEntity<ResponseDTO> makeReservation(CreateReservationDTO reservationDTO, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }
        Store store = storeRepository.findById(reservationDTO.getStoreId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        Reservation reservation = Reservation.builder()
                .reservationTime(reservationDTO.getReservationTime())
                .store(store)
                .user(user)
                .reservationConfirmed(false)
                .visitedConfirmed(false)
                .reviewed(false)
                .build();
        reservationRepository.save(reservation);

        ResponseDTO response = new ResponseDTO("예약이 완료되었습니다.", HttpStatus.OK, reservation.getId());
        return ResponseEntity.ok(response);
    }

    // 방문 확정(예약시간 10분전까지만 가능)
    @Transactional
    public ResponseEntity<ResponseDTO> confirmVisit(Long reservationId, String userEmail) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        if (!reservation.getUser().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        if(!reservation.isReservationConfirmed())
            throw new CustomException(ErrorCode.RESERVATION_NOT_ALLOWED);

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(reservation.getReservationTime().minusMinutes(9))) { // 10시 예약이라면 9시 51분 부터 방문 확정 불가
            throw new CustomException(ErrorCode.CANNOT_CONFIRM_VISIT);
        }
        reservation.setVisitedConfirmed(true);
        reservationRepository.save(reservation);

        ResponseDTO response = new ResponseDTO("방문이 확정되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(response);
    }

    // 리뷰 작성
    @Transactional
    public ResponseEntity<ResponseDTO> writeReview(CreateOrUpdateReviewDTO reviewDTO, String userEmail, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getEmail().equals(userEmail)) { // 이메일 불일치
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (!reservation.isVisitedConfirmed()) { // 방문하지 않음
            throw new CustomException(ErrorCode.CANNOT_WRITE_REVIEW);
        }
        if (reservation.isReviewed()) { // 이미 리뷰됨
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.builder()
                .content(reviewDTO.getContent())
                .rating(reviewDTO.getRating())
                .store(reservation.getStore())
                .user(reservation.getUser())
                .reservation(reservation)
                .build();

        reviewRepository.save(review);

        reservation.setReviewed(true);
        reservationRepository.save(reservation);
        review.getStore().updateAverageRating(); // 스토어 평균 평점 업데이트

        ResponseDTO response = new ResponseDTO("리뷰가 작성되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(response);
    }

    // 유저 본인의 예약 조회 (시간순)
    public ResponseEntity<ResponseDTO> getUserReservations(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }

        // 유저의 예약 목록을 예약 시간 순으로 정렬하여 가져옴
        List<Reservation> reservations = reservationRepository.findAllByUserOrderByReservationTimeAsc(user);

        List<ReservationDetailDTO> reservationDetailDTOs = reservations.stream()
                .map(reservation -> ReservationDetailDTO.builder()
                        .reservationId(reservation.getId())
                        .storeName(reservation.getStore().getName())
                        .reservationTime(String.valueOf(reservation.getReservationTime()))
                        .reservationConfirmed(reservation.isReservationConfirmed())
                        .visitedConfirmed(reservation.isVisitedConfirmed())
                        .build())
                .toList();

        ResponseDTO response = new ResponseDTO("예약 목록 조회 성공", HttpStatus.OK, reservationDetailDTOs);
        return ResponseEntity.ok(response);
    }

    // 유저 본인의 리뷰 확인
    public ResponseEntity<ResponseDTO> getUserReviews(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }

        // 유저가 작성한 리뷰 목록 조회
        List<Review> reviews = reviewRepository.findAllByUser(user);

        List<ReviewDetailForUserDTO> reviewDTOs = reviews.stream()
                .map(review -> ReviewDetailForUserDTO.builder()
                        .reviewId(review.getId())
                        .storeName(review.getStore().getName())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .reservationId(review.getReservation().getId())
                        .build())
                .toList();

        ResponseDTO response = new ResponseDTO("리뷰 목록 조회 성공", HttpStatus.OK, reviewDTOs);
        return ResponseEntity.ok(response);
    }

    // 리뷰 수정
    @Transactional
    public ResponseEntity<ResponseDTO> updateReview(CreateOrUpdateReviewDTO createOrUpdateReviewDTO, String userEmail, int reviewedId) {
        Review review = reviewRepository.findById(reviewedId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 사용자가 작성한 리뷰인지 확인
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        review.setContent(createOrUpdateReviewDTO.getContent());
        review.setRating(createOrUpdateReviewDTO.getRating());
        review.getStore().updateAverageRating(); // 스토어 평균 평점 업데이트

        reviewRepository.save(review);

        ResponseDTO response = new ResponseDTO("리뷰가 수정되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(response);
    }
    
}

