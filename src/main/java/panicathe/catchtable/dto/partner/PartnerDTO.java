package panicathe.catchtable.dto.partner;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartnerDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
