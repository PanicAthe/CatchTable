package panicathe.catchtable.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ReviewDTO {

    private Long id;

    @NotBlank
    private String content;

    @NotNull
    @Min(1)
    @Max(5)
    private int rating;

    @NotNull
    private Long storeId;

    @NotNull
    private Long userId;
}