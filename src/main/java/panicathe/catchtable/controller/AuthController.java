package panicathe.catchtable.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import panicathe.catchtable.dto.PartnerDTO;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.user.UserDTO;
import panicathe.catchtable.dto.user.UserLoginDTO;
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

    @PostMapping("/signup/partner")
    public ResponseEntity<ResponseDTO> partnerSignUp(@RequestBody PartnerDTO partner) {
        return authService.partnerSignUp(partner);
    }

    @PostMapping("/login/partner")
    public ResponseEntity<ResponseDTO> partnerLogin(@RequestBody PartnerDTO partner) {
        return authService.partnerLogIn(partner);
    }

    @PostMapping("/login/user")
    public ResponseEntity<ResponseDTO> userLogin(@RequestBody UserLoginDTO user) {
        return authService.userLogIn(user);
    }

}
