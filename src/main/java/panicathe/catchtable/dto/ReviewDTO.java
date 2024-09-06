package panicathe.catchtable.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class ReviewDTO {

    private Long id;

    private Long reservationId;

    private String storeName;

    @NotBlank
    private String content;


    @NotNull
    @Min(1)
    @Max(5)
    private int rating;

}