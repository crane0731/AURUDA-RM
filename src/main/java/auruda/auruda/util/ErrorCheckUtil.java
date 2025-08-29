package auruda.auruda.util;

import org.springframework.validation.BindingResult;

import java.util.Map;

/**
 * 필드에러가 존재하는지 확인하는 로직
 */
public class ErrorCheckUtil {

    public static boolean errorCheck(BindingResult bindingResult, Map<String, String> errorMessages){
        //유효성 검사에서 오류가 발생한 경우 모든 메시지를 Map에 추가
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.put(error.getField(), error.getDefaultMessage())
            );

            return true;
        }
        return false;
    }
}