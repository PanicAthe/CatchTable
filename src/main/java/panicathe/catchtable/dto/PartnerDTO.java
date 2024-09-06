package panicathe.catchtable.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartnerDTO {

    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
