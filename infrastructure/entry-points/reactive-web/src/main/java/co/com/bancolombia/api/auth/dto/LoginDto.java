package co.com.bancolombia.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginDto(
        @NotNull
        @Email
        String email,
        @NotNull
        @NotBlank
        String password
        ) {
}
