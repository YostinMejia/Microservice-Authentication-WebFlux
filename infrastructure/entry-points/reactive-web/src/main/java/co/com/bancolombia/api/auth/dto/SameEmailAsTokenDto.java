package co.com.bancolombia.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SameEmailAsTokenDto(
        @NotNull
        @Email
        String email
) {
}
