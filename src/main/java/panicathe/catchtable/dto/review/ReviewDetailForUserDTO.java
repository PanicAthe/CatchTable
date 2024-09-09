package panicathe.catchtable.dto.review;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class ReviewDetailForUserDTO {

    private Long reviewId;

    private Long reservationId;

    private String storeName;

    private String content;

    private int rating;

}