package co.com.bancolombia.api.user.dto;

import java.util.UUID;

public record UserDataResponseDto (
        String name,
        String lastName,
        String email,
        UUID idRol,
        Long baseSalary,
        String birthDate,
        String phone,
        String document,
        String address

){
}
