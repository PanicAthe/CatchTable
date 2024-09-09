package panicathe.catchtable.dto.reservation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationTimeTableDTO {

    private String storeName;
    private Long reservationId;
    private String reservationTime;
    private boolean reservationConfirmed;
    private boolean visitedConfirmed;
    private String userPhone;
}
