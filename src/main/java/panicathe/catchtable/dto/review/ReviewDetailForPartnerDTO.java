package panicathe.catchtable.dto.review;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class ReviewDetailForPartnerDTO {

    private Long id;

    private String content;

    private int rating;

}