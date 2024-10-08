package panicathe.catchtable.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginOrUpdateDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
