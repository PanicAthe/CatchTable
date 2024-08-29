package panicathe.catchtable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import panicathe.catchtable.dto.ResponseDTO;
import panicathe.catchtable.dto.UserDTO;
import panicathe.catchtable.exception.CustomException;
import panicathe.catchtable.exception.ErrorCode;
import panicathe.catchtable.mapper.UserMapper;
import panicathe.catchtable.model.User;
import panicathe.catchtable.repository.PartnerRepository;
import panicathe.catchtable.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;

    // 1 유저 등록
    public ResponseEntity<ResponseDTO> userSignUp(UserDTO userDTO) {

        if (userRepository.existsByPhone(userDTO.getPhone())) {
            throw new CustomException(ErrorCode.USER_PHONE_ALREADY_REGISTERED);
        }
        if (userRepository.existsByNickname(userDTO.getNickname())) {
            throw new CustomException(ErrorCode.USER_NICKNAME_ALREADY_REGISTERED);
        }

        User newUser = UserMapper.toEntity(userDTO);

        userRepository.save(newUser);

        ResponseDTO responseDTO = new ResponseDTO("회원가입이 완료되었습니다.", HttpStatus.OK.value());
        return ResponseEntity.ok(responseDTO);
    }


    // 1 파트너 등록

    
    // 파트너 정보 수정
    
    // 유저 정보 수정
    
    // 파트너 계정 삭제
    
    // 유저 계정 삭제

}
