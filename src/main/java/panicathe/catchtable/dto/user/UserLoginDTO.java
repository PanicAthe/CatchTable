package panicathe.catchtable.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
