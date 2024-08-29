package panicathe.catchtable.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.UserDTO;
import panicathe.catchtable.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/user")
    public ResponseEntity<ResponseDTO> userSignUp(@RequestBody UserDTO user) {
        return authService.userSignUp(user);
    }
}
