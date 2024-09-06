package panicathe.catchtable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panicathe.catchtable.model.Reservation;
import panicathe.catchtable.model.Store;
import panicathe.catchtable.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStoreInAndReservationTimeBetween(List<Store> stores, LocalDateTime startOfDay, LocalDateTime endOfDay);

    Reservation findByIdAndStore(Long reservationId, Store store);

    List<Reservation> findAllByUserOrderByReservationTimeAsc(User user);
}
