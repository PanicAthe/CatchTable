package panicathe.catchtable.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationDetailDTO {
    private Long reservationId;
    private String storeName;
    private LocalDateTime reservationTime;
    private boolean reservationConfirmed;
    private boolean visitedConfirmed;
}
