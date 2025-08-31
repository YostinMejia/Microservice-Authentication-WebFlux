package co.com.bancolombia.model.exceptions;

public record SingleErrorResponseDto(
        String message,
        String code) {
}
