package panicathe.catchtable.dto.store;

import lombok.Builder;
import lombok.Data;
import panicathe.catchtable.dto.review.ReviewDetailForUserDTO;

import java.util.List;

@Data
@Builder
public class StoreDetailDTO {

    private Long id;

    private String name;

    private Double lon;

    private Double lat;

    private String description;

    private double averageRating;

    private int reviewCount;

    private List<ReviewDetailForUserDTO> reviews;
}
