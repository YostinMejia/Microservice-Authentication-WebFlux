package co.com.bancolombia.model.user.gateways;

import co.com.bancolombia.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> save(User user);

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByDocumentAndEmail(String document, String email);

    Mono<Boolean> existsByEmailOrDocument(String email, String document);

    Flux<User> findAll();

}
