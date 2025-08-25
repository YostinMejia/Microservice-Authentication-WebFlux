package co.com.bancolombia.r2dbc.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table("users")
public class UserEntity {
    @Id
    @Column("user_id")
    private String id;

    private String name;
    private String lastName;
    private String email;
    private Integer baseSalary;

    private String phone;
    private LocalDate birthDate;
    private String address;

}