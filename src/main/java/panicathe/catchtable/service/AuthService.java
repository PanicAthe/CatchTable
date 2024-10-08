package panicathe.catchtable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.partner.PartnerDTO;
import panicathe.catchtable.dto.LoginOrUpdateDTO;
import panicathe.catchtable.dto.user.UserSignUpDTO;
import panicathe.catchtable.exception.CustomException;
import panicathe.catchtable.exception.ErrorCode;
import panicathe.catchtable.jwt.JwtProvider;
import panicathe.catchtable.model.Partner;
import panicathe.catchtable.model.User;
import panicathe.catchtable.repository.PartnerRepository;
import panicathe.catchtable.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 파트너 가입
    @Transactional
    public ResponseEntity<ResponseDTO> partnerSignUp(PartnerDTO partnerDTO) {

        if(partnerRepository.existsByEmail(partnerDTO.getEmail())){
            throw new CustomException(ErrorCode.PARTNER_EMAIL_ALREADY_REGISTERED);
        }
        if (userRepository.existsByEmail(partnerDTO.getEmail())) {
            throw new CustomException(ErrorCode.USER_EMAIL_ALREADY_REGISTERED);
        }

        partnerRepository.save(Partner.builder()
                .email(partnerDTO.getEmail()) 
                .password(passwordEncoder.encode(partnerDTO.getPassword())).build()); //비밀번호는 인코딩

        ResponseDTO responseDTO = new ResponseDTO("파트너 가입이 완료되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(responseDTO);
    }

    // 유저 회원가입
    @Transactional
    public ResponseEntity<ResponseDTO> userSignUp(UserSignUpDTO userDTO) {

        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new CustomException(ErrorCode.USER_PHONE_ALREADY_REGISTERED);
        }
        if(partnerRepository.existsByEmail(userDTO.getEmail())){
            throw new CustomException(ErrorCode.PARTNER_EMAIL_ALREADY_REGISTERED);
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new CustomException(ErrorCode.USER_EMAIL_ALREADY_REGISTERED);
        }

        userRepository.save(User.builder()
                .email(userDTO.getEmail())
                .phone(userDTO.getPhone())
                .password(passwordEncoder.encode(userDTO.getPassword())) // 비밀번호는 인코딩
                .build());

        ResponseDTO responseDTO = new ResponseDTO("회원가입이 완료되었습니다.", HttpStatus.OK, null);
        return ResponseEntity.ok(responseDTO);
    }

    // 파트너 로그인
    public ResponseEntity<ResponseDTO> partnerLogIn(LoginOrUpdateDTO dto) {

        String token = null;

        try{

            String email = dto.getEmail();
            Partner partner = partnerRepository.findByEmail(email);

            if(partner == null)
                throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);

            String password = dto.getPassword();
            String encodedPassword = partner.getPassword();
            boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            if(!isMatched)
                throw new CustomException(ErrorCode.PASSWORD_NOT_MATCHED);

            token = jwtProvider.create(email, "ROLE_PARTNER"); // role 포함 토큰 발급

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ResponseDTO responseDTO = new ResponseDTO("로그인 성공.", HttpStatus.OK, token);
        return ResponseEntity.ok(responseDTO);
    }

    // 유저 로그인
    public ResponseEntity<ResponseDTO> userLogIn(LoginOrUpdateDTO dto) {

        String token = null;

        try{

            String email = dto.getEmail();
            User user = userRepository.findByEmail(email);

            if(user == null)
                throw new CustomException(ErrorCode.USER_NOT_EXIST);

            String password = dto.getPassword();
            String encodedPassword = user.getPassword();
            boolean isMatched = passwordEncoder.matches(password, encodedPassword);
            if(!isMatched)
                throw new CustomException(ErrorCode.PASSWORD_NOT_MATCHED);

            token = jwtProvider.create(email, "ROLE_USER"); // role 포함 토큰 발급

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ResponseDTO responseDTO = new ResponseDTO("로그인 성공.", HttpStatus.OK, token);
        return ResponseEntity.ok(responseDTO);
    }

    // 파트너 정보 수정
    @Transactional
    public ResponseEntity<ResponseDTO> updatePartnerInfo(PartnerDTO partnerDTO, String email) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        partner.setEmail(partnerDTO.getEmail());
        partner.setPassword(passwordEncoder.encode(partnerDTO.getPassword())); // 비밀번호는 인코딩
        partnerRepository.save(partner);

        return ResponseEntity.ok(new ResponseDTO("파트너 정보 수정이 완료되었습니다.", HttpStatus.OK, null));
    }

    // 유저 정보 수정
    @Transactional
    public ResponseEntity<ResponseDTO> updateUserInfo(LoginOrUpdateDTO userDTO, String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }

        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // 비밀번호는 인코딩
        userRepository.save(user);

        return ResponseEntity.ok(new ResponseDTO("유저 정보 수정이 완료되었습니다.", HttpStatus.OK, null));
    }

    // 파트너 계정 삭제
    @Transactional
    public ResponseEntity<ResponseDTO> deletePartner(String email) {
        Partner partner = partnerRepository.findByEmail(email);
        if (partner == null) {
            throw new CustomException(ErrorCode.PARTNER_NOT_EXIST);
        }

        partnerRepository.delete(partner);
        return ResponseEntity.ok(new ResponseDTO("파트너 계정이 삭제되었습니다.", HttpStatus.OK, null));
    }

    // 유저 계정 삭제
    @Transactional
    public ResponseEntity<ResponseDTO> deleteUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_EXIST);
        }

        userRepository.delete(user);
        return ResponseEntity.ok(new ResponseDTO("유저 계정이 삭제되었습니다.", HttpStatus.OK, null));
    }


}