package co.com.bancolombia.r2dbc.rol;

import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.r2dbc.rol.entity.RolEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, String>, ReactiveQueryByExampleExecutor<RolEntity> {
    Mono<Rol> findByName(String name);
}
