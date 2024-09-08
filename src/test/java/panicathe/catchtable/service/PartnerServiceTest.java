package panicathe.catchtable.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import panicathe.catchtable.dto.*;
import panicathe.catchtable.dto.reservation.ReservationTimeTableDTO;
import panicathe.catchtable.dto.review.ReviewDetailForPartnerDTO;
import panicathe.catchtable.dto.store.CreateOrUpdateStoreDTO;
import panicathe.catchtable.dto.store.StoreDTO;
import panicathe.catchtable.model.*;
import panicathe.catchtable.repository.PartnerRepository;
import panicathe.catchtable.repository.ReservationRepository;
import panicathe.catchtable.repository.ReviewRepository;
import panicathe.catchtable.repository.StoreRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PartnerServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private PartnerService partnerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddStoreSuccess() {
        // Arrange
        String email = "partner@example.com";
        Partner partner = Partner.builder()
                .email(email)
                .build();
        CreateOrUpdateStoreDTO storeDTO = CreateOrUpdateStoreDTO.builder()
                .name("New Store")
                .lat(10.0)
                .lon(20.0)
                .description("A new store")
                .build();

        when(partnerRepository.findByEmail(email)).thenReturn(partner);
        when(storeRepository.findByName(storeDTO.getName())).thenReturn(null);

        // Act
        ResponseEntity<ResponseDTO> response = partnerService.addStore(storeDTO, email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("상점 등록이 완료되었습니다.", response.getBody().getMessage());
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    void testGetStoresSuccess() {
        // Arrange
        String email = "partner@example.com";
        Partner partner = Partner.builder()
                .email(email)
                .build();
        Store store = Store.builder()
                .id(1L)
                .name("Existing Store")
                .lat(10.0)
                .lon(20.0)
                .averageRating(4.5)
                .reviews(Collections.emptyList())
                .build();
        when(partnerRepository.findByEmail(email)).thenReturn(partner);
        when(storeRepository.findAllByPartner(partner)).thenReturn(Collections.singletonList(store));

        // Act
        ResponseEntity<ResponseDTO> response = partnerService.getStores(email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<StoreDTO> storeDTOs = (List<StoreDTO>) response.getBody().getData();
        assertEquals(1, storeDTOs.size());
        assertEquals("Existing Store", storeDTOs.getFirst().getName());
    }

    @Test
    void testGetStoreReviewsSuccess() {
        // Arrange
        String email = "partner@example.com";
        Long storeId = 1L;
        Partner partner = Partner.builder()
                .email(email)
                .build();
        Store store = Store.builder()
                .id(storeId)
                .reviews(Collections.singletonList(
                        Review.builder()
                                .id(1L)
                                .content("Great store!")
                                .rating(5)
                                .build()
                ))
                .build();
        when(partnerRepository.findByEmail(email)).thenReturn(partner);
        when(storeRepository.findByIdAndPartner(storeId, partner)).thenReturn(store);

        // Act
        ResponseEntity<ResponseDTO> response = partnerService.getStoreReviews(email, storeId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ReviewDetailForPartnerDTO> reviewDTOs = (List<ReviewDetailForPartnerDTO>) response.getBody().getData();
        assertEquals(1, reviewDTOs.size());
        assertEquals("Great store!", reviewDTOs.getFirst().getContent());
    }

    @Test
    void testDeleteReviewSuccess() {
        // Arrange
        String email = "partner@example.com";
        Long storeId = 1L;
        Long reviewId = 1L;
        Partner partner = Partner.builder()
                .email(email)
                .build();
        Store store = Store.builder()
                .id(storeId)
                .partner(partner)
                .build();
        Review review = Review.builder()
                .id(reviewId)
                .store(store)
                .build();
        when(partnerRepository.findByEmail(email)).thenReturn(partner);
        when(storeRepository.findByIdAndPartner(storeId, partner)).thenReturn(store);
        when(reviewRepository.findByIdAndStore(reviewId, store)).thenReturn(review);

        // Act
        ResponseEntity<ResponseDTO> response = partnerService.deleteReview(email, storeId, reviewId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("리뷰 삭제 완료", response.getBody().getMessage());
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void testGetStoreReservationsSuccess() {
        // Arrange
        String email = "partner@example.com";
        LocalDate date = LocalDate.now();
        Partner partner = Partner.builder()
                .email(email)
                .build();
        Store store = Store.builder()
                .id(1L)
                .partner(partner)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .reservationTime(LocalDateTime.of(date, LocalTime.of(10, 0)))
                .reservationConfirmed(true)
                .visitedConfirmed(true)
                .user(User.builder().phone("123-456-7890").build())
                .store(store)
                .build();
        when(partnerRepository.findByEmail(email)).thenReturn(partner);
        when(storeRepository.findAllByPartner(partner)).thenReturn(Collections.singletonList(store));
        when(reservationRepository.findByStoreInAndReservationTimeBetween(anyList(), any(), any())).thenReturn(Collections.singletonList(reservation));

        // Act
        ResponseEntity<ResponseDTO> response = partnerService.getStoreReservations(email, date);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<LocalDate, List<ReservationTimeTableDTO>> reservationTimeTables = (Map<LocalDate, List<ReservationTimeTableDTO>>) response.getBody().getData();
        assertEquals(1, reservationTimeTables.size());
        List<ReservationTimeTableDTO> reservationDTOs = reservationTimeTables.get(date);
        assertEquals(1, reservationDTOs.size());
        assertEquals("123-456-7890", reservationDTOs.getFirst().getUserPhone());
    }

    @Test
    void testConfirmReservationSuccess() {
        // Arrange
        String email = "partner@example.com";
        Long storeId = 1L;
        Long reservationId = 1L;
        Partner partner = Partner.builder()
                .email(email)
                .build();
        Store store = Store.builder()
                .id(storeId)
                .partner(partner)
                .build();
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .reservationTime(LocalDateTime.now())
                .reservationConfirmed(false)
                .visitedConfirmed(false)
                .store(store)
                .build();
        when(partnerRepository.findByEmail(email)).thenReturn(partner);
        when(storeRepository.findByIdAndPartner(storeId, partner)).thenReturn(store);
        when(reservationRepository.findByIdAndStore(reservationId, store)).thenReturn(reservation);

        // Act
        ResponseEntity<ResponseDTO> response = partnerService.confirmReservation(email, storeId, reservationId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("예약이 승인되었습니다.", response.getBody().getMessage());
        assertTrue(reservation.isReservationConfirmed());
    }

    @Test
    void testCancelReservationSuccess() {
        // Arrange
        String email = "partner@example.com";
        Long storeId = 1L;
        Long reservationId = 1L;
        Partner partner = Partner.builder()
                .email(email)
                .build();
        Store store = Store.builder()
                .id(storeId)
                .partner(partner)
                .build();
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .reservationTime(LocalDateTime.now())
                .reservationConfirmed(true)
                .visitedConfirmed(false)
                .store(store)
                .build();
        when(partnerRepository.findByEmail(email)).thenReturn(partner);
        when(storeRepository.findByIdAndPartner(storeId, partner)).thenReturn(store);
        when(reservationRepository.findByIdAndStore(reservationId, store)).thenReturn(reservation);

        // Act
        ResponseEntity<ResponseDTO> response = partnerService.cancelReservation(email, storeId, reservationId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("예약이 취소되었습니다.", response.getBody().getMessage());
        assertFalse(reservation.isReservationConfirmed());
    }

    @Test
    void testUpdateStoreSuccess() {
        // Arrange
        String email = "partner@example.com";
        Long storeId = 1L;
        CreateOrUpdateStoreDTO storeDTO = CreateOrUpdateStoreDTO.builder()
                .name("Updated Store")
                .lat(11.0)
                .lon(21.0)
                .description("Updated description")
                .build();
        Partner partner = Partner.builder()
                .email(email)
                .build();
        Store store = Store.builder()
                .id(storeId)
                .name("Old Store")
                .lat(10.0)
                .lon(20.0)
                .description("Old description")
                .partner(partner)
                .build();
        when(partnerRepository.findByEmail(email)).thenReturn(partner);
        when(storeRepository.findByIdAndPartner(storeId, partner)).thenReturn(store);
        when(storeRepository.findByName(storeDTO.getName())).thenReturn(null);

        // Act
        ResponseEntity<ResponseDTO> response = partnerService.updateStore(email, storeId, storeDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("상점 정보 수정 완료", response.getBody().getMessage());
        assertEquals("Updated Store", store.getName());
        assertEquals(11.0, store.getLat());
        assertEquals(21.0, store.getLon());
        assertEquals("Updated description", store.getDescription());
    }

    @Test
    void testDeleteStoreSuccess() {
        // Arrange
        String email = "partner@example.com";
        Long storeId = 1L;
        Partner partner = Partner.builder()
                .email(email)
                .build();
        Store store = Store.builder()
                .id(storeId)
                .partner(partner)
                .build();
        when(partnerRepository.findByEmail(email)).thenReturn(partner);
        when(storeRepository.findByIdAndPartner(storeId, partner)).thenReturn(store);

        // Act
        ResponseEntity<ResponseDTO> response = partnerService.deleteStore(email, storeId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("상점이 삭제되었습니다.", response.getBody().getMessage());
        verify(storeRepository, times(1)).delete(store);
    }
}
