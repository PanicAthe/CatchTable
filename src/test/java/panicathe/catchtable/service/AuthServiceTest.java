package panicathe.catchtable.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.partner.PartnerDTO;
import panicathe.catchtable.dto.user.UserDTO;
import panicathe.catchtable.dto.user.UserSignUpDTO;
import panicathe.catchtable.jwt.JwtProvider;
import panicathe.catchtable.model.Partner;
import panicathe.catchtable.model.User;
import panicathe.catchtable.repository.PartnerRepository;
import panicathe.catchtable.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void userSignUp_Success() {
        UserSignUpDTO userSignUpDTO = UserSignUpDTO.builder()
                .email("test@example.com")
                .phone("1234567890")
                .password("password")
                .build();

        when(userRepository.existsByPhone(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(partnerRepository.existsByEmail(anyString())).thenReturn(false);

        ResponseEntity<ResponseDTO> response = authService.userSignUp(userSignUpDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("회원가입이 완료되었습니다.", response.getBody().getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void partnerSignUp_Success() {
        PartnerDTO partnerDTO = PartnerDTO.builder()
                .email("test@example.com")
                .password("password")
                .build();

        when(partnerRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        ResponseEntity<ResponseDTO> response = authService.partnerSignUp(partnerDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("파트너 가입이 완료되었습니다.", response.getBody().getMessage());
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }


    @Test
    void updatePartnerInfo_Success() {
        PartnerDTO partnerDTO = PartnerDTO.builder()
                .email("new@example.com")
                .password("newPassword")
                .build();
        Partner partner = Partner.builder()
                .email("old@example.com")
                .password("oldPassword")
                .build();

        when(partnerRepository.findByEmail(anyString())).thenReturn(partner);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");

        ResponseEntity<ResponseDTO> response = authService.updatePartnerInfo(partnerDTO, "old@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("파트너 정보 수정이 완료되었습니다.", response.getBody().getMessage());
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    void updateUserInfo_Success() {
        UserDTO userDTO = UserDTO.builder()
                .password("newPassword")
                .build();
        User user = User.builder()
                .email("test@example.com")
                .password("oldPassword")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");

        ResponseEntity<ResponseDTO> response = authService.updateUserInfo(userDTO, "test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("유저 정보 수정이 완료되었습니다.", response.getBody().getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deletePartner_Success() {
        Partner partner = Partner.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        when(partnerRepository.findByEmail(anyString())).thenReturn(partner);

        ResponseEntity<ResponseDTO> response = authService.deletePartner("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("파트너 계정이 삭제되었습니다.", response.getBody().getMessage());
        verify(partnerRepository, times(1)).delete(any(Partner.class));
    }

    @Test
    void deleteUser_Success() {
        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(user);

        ResponseEntity<ResponseDTO> response = authService.deleteUser("test@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("유저 계정이 삭제되었습니다.", response.getBody().getMessage());
        verify(userRepository, times(1)).delete(any(User.class));
    }
}
