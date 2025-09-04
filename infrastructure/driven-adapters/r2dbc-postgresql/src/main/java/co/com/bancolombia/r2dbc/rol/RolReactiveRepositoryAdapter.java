package co.com.bancolombia.r2dbc.rol;

import co.com.bancolombia.model.rol.Rol;
import co.com.bancolombia.model.rol.gateways.RolRepository;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.rol.entity.RolEntity;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolEntity,
        UUID,
        RolReactiveRepository
        > implements RolRepository {
    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }

    @Override
    public Mono<Rol> findByName(String name) {
        return repository.findByName(name);
    }


}
