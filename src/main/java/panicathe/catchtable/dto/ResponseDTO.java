package panicathe.catchtable.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ResponseDTO {
    private String message;
    private HttpStatus statusCode;
    private Object data; // JSON 데이터를 담기 위한 필드 추가
}
