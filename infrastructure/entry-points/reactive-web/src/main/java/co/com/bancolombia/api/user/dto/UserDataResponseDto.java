package co.com.bancolombia.api.user.dto;

public record UserDataResponseDto (
        String name,
        String lastName,
        String email,
        String idRol,
        Long baseSalary,
        String birthDate,
        String phone,
        String document,
        String address

){
}
