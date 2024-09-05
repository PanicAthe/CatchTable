package panicathe.catchtable.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
public class ReservationDTO {
    private Long id;

    @NotBlank
    private LocalDateTime reservationTime;

    @NotNull
    private int storeId;

}