package panicathe.catchtable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import panicathe.catchtable.dto.*;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    // 1 상점 목록 조회 (가나다순, 별점순, 거리순)
    public ResponseEntity<ResponseDTO> getStores(String sortBy, Double userLat, Double userLon) {
        List<Store> stores;

        switch (sortBy) {
            case "rating": // 별점순 정렬, 리뷰 평균을 기준으로 정렬
                stores = storeRepository.findAll();
                stores.sort((s1, s2) -> Double.compare(calculateAverageRating(s2), calculateAverageRating(s1)));
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
                        .averageRating(calculateAverageRating(store))
                        .reviewCount(store.getReviews().size()) // 리뷰 개수
                        .build())
                .toList();

        ResponseDTO response = new ResponseDTO("상점 목록 조회 성공", HttpStatus.OK, storeDtos);
        return ResponseEntity.ok(response);
    }

    // 별점 평균 계산 함수
    private double calculateAverageRating(Store store) {
        List<Review> reviews = store.getReviews();
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
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


    // 2 상점 상세 정보
    public ResponseEntity<ResponseDTO> getStoreDetails(String name) {
        // 상점 이름으로 검색
        Store store = storeRepository.findByName(name);

        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        // 상점의 평균 평점과 리뷰 개수 계산
        double averageRating = calculateAverageRating(store);
        int reviewCount = store.getReviews().size();

        // StoreDetailDTO로 매핑
        StoreDetailDTO storeDetailDTO = StoreDetailDTO.builder()
                .id(store.getId())
                .name(store.getName())
                .lat(store.getLat())
                .lon(store.getLon())
                .description(store.getDescription())
                .averageRating(averageRating)
                .reviewCount(reviewCount)
                .build();

        // 응답 생성
        ResponseDTO response = new ResponseDTO("상점 상세 정보 조회 성공", HttpStatus.OK, storeDetailDTO);
        return ResponseEntity.ok(response);
    }


    // 3 상점 예약
    public ResponseEntity<ResponseDTO> makeReservation(ReservationDTO reservationDTO, String userEmail) {
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

    // 4 방문 확정(예약시간 10분전까지 가능)
    public ResponseEntity<ResponseDTO> confirmVisit(Long reservationId, String userEmail) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        if (!reservation.getUser().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(reservation.getReservationTime().minusMinutes(10))) {
            throw new CustomException(ErrorCode.CANNOT_CONFIRM_VISIT);
        }
        reservation.setVisitedConfirmed(true);
        reservationRepository.save(reservation);

        ResponseDTO response = new ResponseDTO("방문이 확정되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(response);
    }

    // 5 리뷰 작성
    public ResponseEntity<ResponseDTO> writeReview(ReviewDTO reviewDTO, String userEmail) {
        Reservation reservation = reservationRepository.findById(reviewDTO.getReservationId())
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

        ResponseDTO response = new ResponseDTO("리뷰가 작성되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(response);
    }

    // 6. 유저 본인의 예약 조회 (시간순)
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
                        .reservationTime(reservation.getReservationTime())
                        .reservationConfirmed(reservation.isReservationConfirmed())
                        .visitedConfirmed(reservation.isVisitedConfirmed())
                        .build())
                .toList();

        ResponseDTO response = new ResponseDTO("예약 목록 조회 성공", HttpStatus.OK, reservationDetailDTOs);
        return ResponseEntity.ok(response);
    }

    // 7. 유저 본인의 리뷰 확인
    public ResponseEntity<ResponseDTO> getUserReviews(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }

        // 유저가 작성한 리뷰 목록 조회
        List<Review> reviews = reviewRepository.findAllByUser(user);

        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> ReviewDTO.builder()
                        .id(review.getId())
                        .storeName(review.getStore().getName())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .reservationId(review.getReservation().getId())
                        .build())
                .toList();

        ResponseDTO response = new ResponseDTO("리뷰 목록 조회 성공", HttpStatus.OK, reviewDTOs);
        return ResponseEntity.ok(response);
    }


}

