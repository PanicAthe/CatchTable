package panicathe.catchtable.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignUpDTO {
    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String password;
}
