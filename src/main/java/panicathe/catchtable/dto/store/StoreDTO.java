package panicathe.catchtable.dto.store;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreDTO {

    private Long id;

    private String name;

    private Double lon;

    private Double lat;

    private double averageRating;

    private int reviewCount;

}