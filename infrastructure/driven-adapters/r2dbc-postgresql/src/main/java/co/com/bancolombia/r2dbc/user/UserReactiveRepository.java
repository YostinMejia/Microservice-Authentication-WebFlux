package co.com.bancolombia.r2dbc.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.r2dbc.user.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, String>, ReactiveQueryByExampleExecutor<UserEntity> {
    Mono<User> findByEmail(String email);
    Mono<Boolean> existsByEmailOrDocument(String email, String document);
    Mono<Boolean> existsByDocumentAndEmail(String document, String email);

}
