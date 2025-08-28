package co.com.bancolombia.model.user.exceptions;

public record SingleErrorResponseDto(
        String message,
        String code) {
}
