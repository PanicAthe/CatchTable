package panicathe.catchtable.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateOrUpdateReviewDTO {


    @NotBlank
    private String content;

    @NotNull
    @Min(1)
    @Max(5)
    private int rating;
}
