package co.com.bancolombia.model.exceptions;

import java.util.List;

public record MultipleErrorsResponseDto(
        List<String> errors,
        String message,
        String code
){ }
