package co.com.bancolombia.r2dbc.rol.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table("rol")
public class RolEntity {
    @Id
    @Column("id_rol")
    private String id;
    @Column("nombre")
    private String name;
    @Column("descripcion")
    private String description;
}