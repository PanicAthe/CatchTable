package panicathe.catchtable.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import panicathe.catchtable.dto.*;
import panicathe.catchtable.dto.reservation.CreateReservationDTO;
import panicathe.catchtable.dto.reservation.ReservationDetailDTO;
import panicathe.catchtable.dto.review.ReviewDetailForUserDTO;
import panicathe.catchtable.dto.review.CreateOrUpdateReviewDTO;
import panicathe.catchtable.dto.store.StoreByKeywordDTO;
import panicathe.catchtable.dto.store.StoreDetailDTO;
import panicathe.catchtable.model.*;
import panicathe.catchtable.repository.ReservationRepository;
import panicathe.catchtable.repository.ReviewRepository;
import panicathe.catchtable.repository.StoreRepository;
import panicathe.catchtable.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStoresByRating() {
        // Arrange
        List<Store> stores = Arrays.asList(
                Store.builder().id(1L).name("Store A").lat(10.0).lon(20.0).averageRating(4.5).reviews(new ArrayList<>()).build(),
                Store.builder().id(2L).name("Store B").lat(15.0).lon(25.0).averageRating(4.7).reviews(new ArrayList<>()).build()
        );
        when(storeRepository.findAllByOrderByAverageRatingDesc()).thenReturn(stores.stream()
                .sorted(Comparator.comparingDouble(Store::getAverageRating).reversed())
                .collect(Collectors.toList()));

        // Act
        ResponseEntity<ResponseDTO> response = userService.getStores("rating", 0.0, 0.0);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<StoreDetailDTO> storeDtos = (List<StoreDetailDTO>) response.getBody().getData();
        assertEquals(2, storeDtos.size());
        assertEquals("Store B", storeDtos.getFirst().getName()); // 별점순 정렬로 인해 "Store B"가 먼저 와야 함
    }

    @Test
    void testGetStoreNamesByKeyword() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Store> stores = new PageImpl<>(Arrays.asList(
                Store.builder().id(1L).name("Store A").build(),
                Store.builder().id(2L).name("Store B").build()
        ));
        when(storeRepository.findByNameContainingIgnoreCase("Store", pageable)).thenReturn(stores);

        // Act
        ResponseEntity<ResponseDTO> response = userService.getStoreNamesByKeyword("Store");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<StoreByKeywordDTO> storeDtos = (List<StoreByKeywordDTO>) response.getBody().getData();
        assertEquals(2, storeDtos.size());
    }

    @Test
    void testGetStoreDetailsSuccess() {
        // Arrange
        Store store = Store.builder()
                .id(1L)
                .name("Store A")
                .lat(10.0)
                .lon(20.0)
                .description("A great store")
                .averageRating(4.5)
                .reviews(Arrays.asList(
                        Review.builder().id(1L).content("Excellent!").rating(5).build(),
                        Review.builder().id(2L).content("Not bad").rating(3).build()
                ))
                .build();
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        // Act
        ResponseEntity<ResponseDTO> response = userService.getStoreDetails(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        StoreDetailDTO storeDetailDTO = (StoreDetailDTO) response.getBody().getData();
        assertEquals("Store A", storeDetailDTO.getName());
        assertEquals(2, storeDetailDTO.getReviews().size());
    }

    @Test
    void testMakeReservationSuccess() {
        // Arrange
        CreateReservationDTO reservationDTO = CreateReservationDTO.builder()
                .storeId(1L)
                .reservationTime(LocalDateTime.now().plusDays(1))
                .build();
        User user = User.builder().email("user@example.com").build();
        Store store = Store.builder().id(1L).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        // Act
        ResponseEntity<ResponseDTO> response = userService.makeReservation(reservationDTO, "user@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("예약이 완료되었습니다.", response.getBody().getMessage());
    }

    @Test
    void testConfirmVisitSuccess() {
        // Arrange
        Reservation reservation = Reservation.builder()
                .id(1L)
                .reservationTime(LocalDateTime.now().plusMinutes(10))
                .reservationConfirmed(true)
                .visitedConfirmed(false)
                .user(User.builder().email("user@example.com").build())
                .build();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act
        ResponseEntity<ResponseDTO> response = userService.confirmVisit(1L, "user@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("방문이 확정되었습니다.", response.getBody().getMessage());
        assertTrue(reservation.isVisitedConfirmed());
    }

    @Test
    void testWriteReviewSuccess() {
        // Arrange
        CreateOrUpdateReviewDTO reviewDTO = CreateOrUpdateReviewDTO.builder()
                .content("Great experience!")
                .rating(5)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(User.builder().email("user@example.com").build())
                .store(Store.builder().id(1L).build())
                .visitedConfirmed(true)
                .reviewed(false)
                .build();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        // Act
        ResponseEntity<ResponseDTO> response = userService.writeReview(reviewDTO, "user@example.com", 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("리뷰가 작성되었습니다.", response.getBody().getMessage());
    }

    @Test
    void testGetUserReservationsSuccess() {
        // Arrange
        User user = User.builder().email("user@example.com").build();
        List<Reservation> reservations = Collections.singletonList(
                Reservation.builder()
                        .id(1L)
                        .store(Store.builder().name("Store A").build())
                        .reservationTime(LocalDateTime.now().plusDays(1))
                        .reservationConfirmed(true)
                        .visitedConfirmed(true)
                        .build()
        );
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(reservationRepository.findAllByUserOrderByReservationTimeAsc(user)).thenReturn(reservations);

        // Act
        ResponseEntity<ResponseDTO> response = userService.getUserReservations("user@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ReservationDetailDTO> reservationDetailDTOs = (List<ReservationDetailDTO>) response.getBody().getData();
        assertEquals(1, reservationDetailDTOs.size());
        assertEquals("Store A", reservationDetailDTOs.getFirst().getStoreName());
    }

    @Test
    void testGetUserReviewsSuccess() {
        // Arrange
        User user = User.builder().email("user@example.com").build();
        List<Review> reviews = Collections.singletonList(
                Review.builder()
                        .id(1L)
                        .store(Store.builder().name("Store A").build())
                        .content("Good!")
                        .rating(4)
                        .reservation(Reservation.builder().id(1L).build())
                        .build()
        );
        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(reviewRepository.findAllByUser(user)).thenReturn(reviews);

        // Act
        ResponseEntity<ResponseDTO> response = userService.getUserReviews("user@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ReviewDetailForUserDTO> reviewDTOs = (List<ReviewDetailForUserDTO>) response.getBody().getData();
        assertEquals(1, reviewDTOs.size());
        assertEquals("Store A", reviewDTOs.getFirst().getStoreName());
    }

    @Test
    void testUpdateReviewSuccess() {
        // Arrange
        CreateOrUpdateReviewDTO createOrUpdateReviewDTO = CreateOrUpdateReviewDTO.builder()
                .content("Updated content")
                .rating(4)
                .build();
        Review review = Review.builder()
                .id(1L)
                .user(User.builder().email("user@example.com").build())
                .content("Old content")
                .rating(3)
                .build();
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));

        // Act
        ResponseEntity<ResponseDTO> response = userService.updateReview(createOrUpdateReviewDTO, "user@example.com", 1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("리뷰가 수정되었습니다.", response.getBody().getMessage());
        assertEquals("Updated content", review.getContent());
        assertEquals(4, review.getRating());
    }
}
