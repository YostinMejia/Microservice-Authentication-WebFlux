package co.com.bancolombia.api.user.dto;

import jakarta.validation.constraints.*;

public record CreateUserDto(

        @NotNull
        @NotBlank
        String name,

        @NotNull
        @NotBlank
        String lastName,
        @NotNull
        @Email
        String email,
        @NotNull
        @NotBlank
        String password,
        @NotBlank
        String rol,
        @NotNull
        @Min(value = 0, message = "The minimum value is 0")
        @Max(value = 15000000, message = "The maximum value is 15000000")
        Long baseSalary,
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"
                , message = "Birth Date should have the format yyyy-MM-dd and be a valid date")
        String birthDate,
        @Pattern(regexp = "^\\+\\d{2,3} \\d{10,18}$"
                , message = "The phone should have the format +xx xxxxxxxxxx and be a valid phone number")
        String phone,
        @NotNull
        @NotBlank
        String document,
        String address

) {
}
