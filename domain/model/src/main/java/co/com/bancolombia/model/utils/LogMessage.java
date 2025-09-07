package co.com.bancolombia.model.utils;

import lombok.Getter;

@Getter
public enum LogMessage {
    SAVE_USER_CALLED("Save user called"),
    EXIST_BY_DOCUMENT_AND_EMAIL_CALLED("Exist by Document and Email called"),
    FIND_ROLE_NAME_BY_EMAIL_CALLED("Find role name by Email called"),
    GET_ALL_CALLED("Get all called"),
    LOGIN_CALLED("Login called"),
    IS_SAME_EMAIL_AS_TOKEN_CALLED("Is same email as token called");

    private final String message;

    LogMessage(String message) {
        this.message = message;
    }
}
