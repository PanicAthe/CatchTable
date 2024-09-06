package panicathe.catchtable.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    USER_PHONE_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록된 전화번호입니다."),
    USER_EMAIL_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록된 이메일입니다."),
    USER_LOGIN_FAILED(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호를 확인해주세요."),
    PARTNER_EMAIL_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록된 파트너 이메일입니다."),
    PARTNER_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 이메일 파트너가 존재하지 않습니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 이메일 유저가 존재하지 않습니다."),
    STORE_NAME_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "해당 이름의 상점이 이미 있습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "상점을 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
    CANNOT_CONFIRM_VISIT(HttpStatus.FORBIDDEN, "방문을 확인할 수 없습니다."),
    CANNOT_WRITE_REVIEW(HttpStatus.FORBIDDEN, "리뷰를 작성할 수 없습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 리뷰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
