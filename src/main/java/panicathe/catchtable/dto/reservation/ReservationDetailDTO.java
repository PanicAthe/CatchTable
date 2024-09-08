package panicathe.catchtable.dto.reservation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationDetailDTO {
    private Long reservationId;
    private String storeName;
    private String reservationTime;
    private boolean reservationConfirmed;
    private boolean visitedConfirmed;
}
