package panicathe.catchtable.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StoreReservationDTO {

    @NotNull
    private Long storeId;

    @NotNull
    private LocalDateTime reservationTime;

    @NotNull
    private Long userId;

    @NotNull
    private boolean reservationConfirmed;
}
