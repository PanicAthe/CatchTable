package panicathe.catchtable.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    private Long id;

    @NotBlank
    private LocalDateTime reservationTime;

    @NotNull
    private Long storeId;

    @NotNull
    private Long userId;

    @NotNull
    private boolean reservationConfirmed;

    @NotNull
    private boolean visitedConfirmed;

    @NotNull
    private boolean reviewed;
}