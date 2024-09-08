package panicathe.catchtable.dto.store;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateOrUpdateStoreDTO {

    @NotBlank
    private String name;

    @NotNull
    private Double lon;

    @NotNull
    private Double lat;

    @NotBlank
    private String description;
}
