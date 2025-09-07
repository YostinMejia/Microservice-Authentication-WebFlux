package co.com.bancolombia.model.utils;

import lombok.Getter;

@Getter
public enum ResponseCode  implements ResponseMessage{
    USER_CREATED("201-00", "User created successfully"),
    USER_EXISTS("200-00", "User exists"),
    TOKEN_GENERATED("200-00", "Token generated"),
    IS_SAME_EMAIL("200-00", "Is same email as token"),
    USER_ROLE_FOUND("200-00", "User role found");

    private final String businessCode;
    private final String message;

    ResponseCode(String businessCode, String message) {
        this.businessCode = businessCode;
        this.message = message;
    }
}