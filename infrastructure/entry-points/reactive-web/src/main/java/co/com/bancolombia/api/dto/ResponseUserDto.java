package co.com.bancolombia.api.dto;

import co.com.bancolombia.model.user.User;

public record ResponseUserDto(
        String message,
        String code,
        User data

) {}
