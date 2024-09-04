package panicathe.catchtable.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreDTO {

    @NotBlank
    private String name;

    @NotNull
    private Double lon;

    @NotNull
    private Double lat;

    @NotBlank
    private String description;

}