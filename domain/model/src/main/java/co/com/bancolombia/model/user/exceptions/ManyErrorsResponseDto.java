package co.com.bancolombia.model.user.exceptions;

import java.util.List;

public record ManyErrorsResponseDto(
        List<String> errors,
        String message,
        String code
){ }
