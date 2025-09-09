package co.com.bancolombia.model.utils;

import lombok.Getter;

@Getter
public enum BusinessErrorCode implements ResponseMessage{
    VALIDATION_FAILED("B400-00", "Dto validation failed"),
    USER_NOT_FOUND("B404-00", "User does not exist"),
    USER_ALREADY_REGISTERED("B400-01", "User registered already"),
    ROL_NOT_EXIST("B400-02", "Rol does not exist"),
    INTERNAL_SERVER_ERROR("I500-00", "Internal Server Error"),
    INVALID_CREDENTIALS("B401-00", "Invalid credentials"),
    IS_NOT_SAME_EMAIL("B400-10", "Is not the same email as token"),
    JWT_INVALID("B401-02","JWT invalid" );

    private final String businessCode;
    private final String message;

    BusinessErrorCode(String businessCode, String message) {
        this.businessCode = businessCode;
        this.message = message;
    }
}