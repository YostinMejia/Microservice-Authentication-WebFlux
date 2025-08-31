package co.com.bancolombia.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String name;
    private String document;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phone;
    private String email;
    private String baseSalary;
    private UUID idRol;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return  Objects.equals(document, user.document) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(document, email);
    }
}
