package panicathe.catchtable.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.partner.PartnerDTO;
import panicathe.catchtable.dto.user.UserDTO;
import panicathe.catchtable.dto.LoginDTO;
import panicathe.catchtable.dto.user.UserSignUpDTO;
import panicathe.catchtable.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Controller", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/user")
    @Operation(summary = "유저 회원가입", description = "새로운 유저를 회원가입 시킵니다.")
    public ResponseEntity<ResponseDTO> userSignUp(@RequestBody @Valid UserSignUpDTO user) {
        return authService.userSignUp(user);
    }

    @PostMapping("/signup/partner")
    @Operation(summary = "파트너 회원가입", description = "새로운 파트너를 회원가입 시킵니다.")
    public ResponseEntity<ResponseDTO> partnerSignUp(@RequestBody @Valid PartnerDTO partner) {
        return authService.partnerSignUp(partner);
    }

    @PostMapping("/login/partner")
    @Operation(summary = "파트너 로그인", description = "파트너 로그인을 처리하고 JWT 토큰을 발급합니다.")
    public ResponseEntity<ResponseDTO> partnerLogin(@RequestBody @Valid LoginDTO partner) {
        return authService.partnerLogIn(partner);
    }

    @PostMapping("/login/user")
    @Operation(summary = "유저 로그인", description = "유저 로그인을 처리하고 JWT 토큰을 발급합니다.")
    public ResponseEntity<ResponseDTO> userLogin(@RequestBody @Valid LoginDTO user) {
        return authService.userLogIn(user);
    }

    @PutMapping("/partner")
    @Operation(summary = "파트너 정보 수정", description = "파트너의 정보를 수정합니다.")
    public ResponseEntity<ResponseDTO> updatePartnerInfo(@RequestBody @Valid PartnerDTO partnerDTO, @AuthenticationPrincipal String email) {
        return authService.updatePartnerInfo(partnerDTO, email);
    }

    @PutMapping("/user")
    @Operation(summary = "유저 정보 수정", description = "유저의 정보를 수정합니다.")
    public ResponseEntity<ResponseDTO> updateUserInfo(@RequestBody @Valid UserDTO userDTO, @AuthenticationPrincipal String email) {
        return authService.updateUserInfo(userDTO, email);
    }

    @DeleteMapping("/partner")
    @Operation(summary = "파트너 계정 삭제", description = "파트너 계정을 삭제합니다.")
    public ResponseEntity<ResponseDTO> deletePartner(@AuthenticationPrincipal String email) {
        return authService.deletePartner(email);
    }

    @DeleteMapping("/user")
    @Operation(summary = "유저 계정 삭제", description = "유저 계정을 삭제합니다.")
    public ResponseEntity<ResponseDTO> deleteUser(@AuthenticationPrincipal String email) {
        return authService.deleteUser(email);
    }
}
