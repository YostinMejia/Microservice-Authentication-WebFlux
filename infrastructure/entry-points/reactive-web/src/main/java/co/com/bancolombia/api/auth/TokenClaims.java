package co.com.bancolombia.api.auth;

import lombok.Getter;

@Getter
public enum TokenClaims {
    EMAIL("email"),
    ROL("rol"),
    DOCUMENT("document");

    private final String value;

    TokenClaims(String value) {
        this.value = value;
    }
}
