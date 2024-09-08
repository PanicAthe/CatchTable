package panicathe.catchtable.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class ReservationTimeTableDTO {

    private Long reservationId;
    private String reservationTime;  // 예약된 시간
    private boolean reservationConfirmed;  // 예약 확정 여부
    private boolean visitedConfirmed;  // 방문 여부
    private String userPhone;  // 예약자의 이메일 (혹은 다른 정보)
}
