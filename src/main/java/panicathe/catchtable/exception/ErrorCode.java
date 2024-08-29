package panicathe.catchtable.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    USER_PHONE_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록된 전화번호입니다."),
    USER_NICKNAME_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록된 닉네임입니다."),
    USER_LOGIN_FAILED(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 확인해주세요.");

    private final HttpStatus status;
    private final String message;
}
