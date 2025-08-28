package co.com.bancolombia.model.user.exceptions;

import java.util.List;

public record MultipleErrorsResponseDto(
        List<String> errors,
        String message,
        String code
){ }
