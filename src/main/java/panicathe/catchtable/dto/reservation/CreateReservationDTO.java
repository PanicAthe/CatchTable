package panicathe.catchtable.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
public class CreateReservationDTO {

    @NotNull
    private Long storeId;

    @NotNull
    private LocalDateTime reservationTime;

}