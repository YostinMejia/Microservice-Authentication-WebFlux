package co.com.bancolombia.api.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExistsByDocumentAndEmailDto(
        @NotNull
        @NotBlank
        String document,
        @NotNull
        @Email
        String email
) {
}
