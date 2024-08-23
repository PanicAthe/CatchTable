package panicathe.catchtable.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import panicathe.catchtable.dto.UserDTO;
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
    public ResponseEntity<String> userSignUp(UserDTO userDTO) {

        if (userRepository.existsByPhone(userDTO.getPhone())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 전화번호입니다.");
        }
        if (userRepository.existsByNickname(userDTO.getNickname())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 닉네임입니다.");
        }

        User newUser = UserMapper.toEntity(userDTO);

        userRepository.save(newUser);

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }


    // 1 파트너 등록

    
    // 파트너 정보 수정
    
    // 유저 정보 수정
    
    // 파트너 계정 삭제
    
    // 유저 계정 삭제

}
