package co.com.bancolombia.model.rol.gateways;

import co.com.bancolombia.model.rol.Rol;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RolRepository {
    Mono<Rol> findByName(String name);
    Mono<Rol> findById(UUID id);
}
