package co.com.bancolombia.model.user.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorResponseDto errorResponse;

    public BusinessException(ErrorResponseDto errorResponse) {
        super(errorResponse.message());
        this.errorResponse = errorResponse;
    }

}
