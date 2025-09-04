package co.com.bancolombia.r2dbc.user.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter()
@Builder
@Table("users")
public class UserEntity {
    @Id
    @Column("user_id")
    private UUID id;

    private String name;
    private String lastName;
    private String email;
    private Integer baseSalary;

    private String document;
    private String phone;
    private LocalDate birthDate;
    private String address;
    private String password;
    private UUID idRol;
}