package panicathe.catchtable.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotBlank
    private String password;

}
