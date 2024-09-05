package panicathe.catchtable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.ReservationDTO;
import panicathe.catchtable.dto.ReviewDTO;
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
            case "rating":
                stores = storeRepository.findAll(Sort.by(Sort.Direction.DESC, "reviews.rating"));
                break;
            case "distance":
                stores = storeRepository.findAll(); // 임시
                stores.sort((s1, s2) -> {
                    double distance1 = calculateDistance(userLat, userLon, s1.getLat(), s1.getLon());
                    double distance2 = calculateDistance(userLat, userLon, s2.getLat(), s2.getLon());
                    return Double.compare(distance1, distance2);
                });
                break;
            case "alphabet":
            default:
                stores = storeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
                break;
        }
        ResponseDTO response = new ResponseDTO("상점 목록 조회 성공", HttpStatus.OK, stores);
        return ResponseEntity.ok(response);
    }

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // 지구 반경 km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // 2 상점 상세 정보
    public ResponseEntity<ResponseDTO> getStoreDetails(String name) {
        Store store = storeRepository.findByName(name);
        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }
        ResponseDTO response = new ResponseDTO("상점 상세 정보 조회 성공", HttpStatus.OK, store);
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

        ResponseDTO response = new ResponseDTO("예약이 완료되었습니다.", HttpStatus.OK, reservation);
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

        ResponseDTO response = new ResponseDTO("방문이 확정되었습니다.", HttpStatus.OK, reservation);
        return ResponseEntity.ok(response);
    }

    // 5 리뷰 작성
    public ResponseEntity<ResponseDTO> writeReview(ReviewDTO reviewDTO, String userEmail) {
        Reservation reservation = reservationRepository.findById(reviewDTO.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        if (!reservation.getUser().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        if (!reservation.isVisitedConfirmed()) {
            throw new CustomException(ErrorCode.CANNOT_WRITE_REVIEW);
        }
        if (reservation.isReviewed()) {
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

        ResponseDTO response = new ResponseDTO("리뷰가 작성되었습니다.", HttpStatus.OK, review);
        return ResponseEntity.ok(response);
    }
}

