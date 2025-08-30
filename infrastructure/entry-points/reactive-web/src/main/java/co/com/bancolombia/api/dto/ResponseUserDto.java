package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.user.User;

public record ResponseUserDto<T>(
        String message,
        String code,
        T data
) {}
