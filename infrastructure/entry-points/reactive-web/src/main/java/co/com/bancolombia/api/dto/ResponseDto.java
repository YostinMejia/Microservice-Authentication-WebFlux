package co.com.bancolombia.api.dto;

public record ResponseDto<T>(
        String message,
        String code,
        T data
) {}
