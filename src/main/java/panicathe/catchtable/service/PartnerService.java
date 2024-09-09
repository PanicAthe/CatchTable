package panicathe.catchtable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panicathe.catchtable.dto.*;
import panicathe.catchtable.dto.reservation.ReservationTimeTableDTO;
import panicathe.catchtable.dto.review.ReviewDetailForPartnerDTO;
import panicathe.catchtable.dto.store.CreateOrUpdateStoreDTO;
import panicathe.catchtable.dto.store.StoreDTO;
import panicathe.catchtable.exception.CustomException;
import panicathe.catchtable.exception.ErrorCode;
import panicathe.catchtable.model.Partner;
import panicathe.catchtable.model.Reservation;
import panicathe.catchtable.model.Review;
import panicathe.catchtable.model.Store;
import panicathe.catchtable.repository.PartnerRepository;
import panicathe.catchtable.repository.ReservationRepository;
import panicathe.catchtable.repository.ReviewRepository;
import panicathe.catchtable.repository.StoreRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    // 상점 등록
    @Transactional
    public ResponseEntity<ResponseDTO> addStore(CreateOrUpdateStoreDTO storeDTO, String email) {

        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        if (storeRepository.findByName(storeDTO.getName()) != null) {
            throw new CustomException(ErrorCode.STORE_NAME_ALREADY_REGISTERED);
        }

        Store store = Store.builder()
                .partner(partner)
                .lat(storeDTO.getLat())
                .lon(storeDTO.getLon())
                .description(storeDTO.getDescription())
                .name(storeDTO.getName())
                .build();

        storeRepository.save(store);

        ResponseDTO responseDTO = new ResponseDTO("상점 등록이 완료되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(responseDTO);
    }

    // 내 상점 조회
    public ResponseEntity<ResponseDTO> getStores(String email) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        List<Store> stores = storeRepository.findAllByPartner(partner); // 파트너로 상점들 조회

        List<StoreDTO> storeDTOs = stores.stream()
                .map(store -> StoreDTO.builder()
                        .id(store.getId())
                        .name(store.getName())
                        .lat(store.getLat())
                        .lon(store.getLon())
                        .averageRating(store.getAverageRating())
                        .reviewCount(store.getReviews().size())
                        .build())
                .collect(Collectors.toList());

        ResponseDTO responseDTO = new ResponseDTO("파트너의 상점 조회 완료.", HttpStatus.OK, storeDTOs);

        return ResponseEntity.ok(responseDTO);
    }

    // 상점 정보 수정
    @Transactional
    public ResponseEntity<ResponseDTO> updateStore(String email, Long storeId, CreateOrUpdateStoreDTO storeDTO) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        Store store = storeRepository.findByIdAndPartner(storeId, partner);
        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        if ( !store.getName().equals(storeDTO.getName()) && storeRepository.findByName(storeDTO.getName()) != null) {
            throw new CustomException(ErrorCode.STORE_NAME_ALREADY_REGISTERED);
        }

        store.setName(storeDTO.getName());
        store.setLat(storeDTO.getLat());
        store.setLon(storeDTO.getLon());
        store.setDescription(storeDTO.getDescription());

        storeRepository.save(store);
        
        ResponseDTO responseDTO = new ResponseDTO("상점 정보 수정 완료", HttpStatus.OK, null);
        return ResponseEntity.ok(responseDTO);
    }

    // 상점 정보 삭제
    @Transactional
    public ResponseEntity<ResponseDTO> deleteStore(String email, Long storeId) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        Store store = storeRepository.findByIdAndPartner(storeId, partner);
        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        storeRepository.delete(store);
        
        ResponseDTO responseDTO = new ResponseDTO("상점이 삭제되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(responseDTO);
    }

    // 상점 리뷰 조회
    public ResponseEntity<ResponseDTO> getStoreReviews(String email, Long storeId) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        Store store = storeRepository.findByIdAndPartner(storeId, partner);
        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        List<ReviewDetailForPartnerDTO> reviewDTOs = store.getReviews().stream()
                .map(review -> ReviewDetailForPartnerDTO.builder()
                        .id(review.getId())
                        .content(review.getContent())
                        .rating(review.getRating())
                        .build())
                .collect(Collectors.toList());

        ResponseDTO responseDTO = new ResponseDTO("상점 리뷰 조회 완료", HttpStatus.OK, reviewDTOs);
        return ResponseEntity.ok(responseDTO);
    }

    // 상점 특정 리뷰 삭제
    @Transactional
    public ResponseEntity<ResponseDTO> deleteReview(String email, Long storeId, Long reviewId) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        Store store = storeRepository.findByIdAndPartner(storeId, partner);
        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        Review review = reviewRepository.findByIdAndStore(reviewId, store);
        if (review == null) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }

        reviewRepository.delete(review);
        store.updateAverageRating(); // 스토어 평균 평점 업데이트
        
        ResponseDTO responseDTO = new ResponseDTO("리뷰 삭제 완료", HttpStatus.OK, null);
        return ResponseEntity.ok(responseDTO);
    }

    // 파트너의 상점 예약 정보 조회 (해당하는 날짜만)
    public ResponseEntity<ResponseDTO> getStoreReservations(String email, LocalDate date) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        List<Store> stores = storeRepository.findAllByPartner(partner);
        if (stores.isEmpty()) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Reservation> reservations = reservationRepository.findByStoreInAndReservationTimeBetween(stores, startOfDay, endOfDay);

        Map<LocalDate, List<ReservationTimeTableDTO>> reservationTimeTables = reservations.stream()
                .collect(Collectors.groupingBy(
                        reservation -> reservation.getReservationTime().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Reservation::getReservationTime))
                                        .map(reservation -> ReservationTimeTableDTO.builder()
                                                .reservationId(reservation.getId())
                                                .storeName(reservation.getStore().getName())
                                                .reservationTime(reservation.getReservationTime().toString())
                                                .reservationConfirmed(reservation.isReservationConfirmed())
                                                .visitedConfirmed(reservation.isVisitedConfirmed())
                                                .userPhone(reservation.getUser().getPhone())
                                                .build())
                                        .collect(Collectors.toList())
                        )
                ));

        ResponseDTO responseDTO = new ResponseDTO("예약 정보 조회 성공", HttpStatus.OK, reservationTimeTables);
        return ResponseEntity.ok(responseDTO);
    }

    // 예약 승인
    @Transactional
    public ResponseEntity<ResponseDTO> confirmReservation(String email, Long storeId, Long reservationId) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        Store store = storeRepository.findByIdAndPartner(storeId, partner);
        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        Reservation reservation = reservationRepository.findByIdAndStore(reservationId, store);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        reservation.setReservationConfirmed(true);
        reservationRepository.save(reservation);

        ResponseDTO responseDTO = new ResponseDTO("예약이 승인되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(responseDTO);
    }

    // 예약 취소
    @Transactional
    public ResponseEntity<ResponseDTO> cancelReservation(String email, Long storeId, Long reservationId) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        Store store = storeRepository.findByIdAndPartner(storeId, partner);
        if (store == null) {
            throw new CustomException(ErrorCode.STORE_NOT_FOUND);
        }

        Reservation reservation = reservationRepository.findByIdAndStore(reservationId, store);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        reservation.setReservationConfirmed(false);
        reservationRepository.save(reservation);

        ResponseDTO responseDTO = new ResponseDTO("예약이 취소되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(responseDTO);
    }


}

