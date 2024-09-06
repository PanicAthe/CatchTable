package panicathe.catchtable.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
public class ReservationDTO {
    private Long id;

    @NotNull
    private LocalDateTime reservationTime;

    @NotNull
    private int storeId;

}