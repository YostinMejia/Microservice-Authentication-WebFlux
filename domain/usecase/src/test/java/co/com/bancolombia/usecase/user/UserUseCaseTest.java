package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.exceptions.BusinessException;
import co.com.bancolombia.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @InjectMocks
    private UserUseCase userUseCase;

    @Mock
    private UserRepository userRepository;


    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Alice")
                .lastName("Smith")
                .birthDate(LocalDate.of(1995, 5, 20))
                .address("Calle 123")
                .phone("3001234567")
                .email("alice@gmail.com")
                .baseSalary("2000")
                .document("984123412")
                .build();
    }

    @Test
    void findUserByEmail_whenTheUserExist_thenShouldReturnIt() {

        given(userRepository.findByEmail(user.getEmail())).willReturn(Mono.just(user));

        StepVerifier.create(userUseCase.findByEmail(user.getEmail()))
                .expectNext(user)
                .verifyComplete();
    }


    @Test
    void save_whenUserAlreadyExists_thenShouldThrowBusinessException() {
        given(userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument()))
                .willReturn(Mono.just(true));

        StepVerifier.create(userUseCase.save(user, "client"))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void save_whenUserDoesNotExists_thenShouldCreteIt() {
        given(userRepository.existsByEmailOrDocument(user.getEmail(), user.getDocument()))
                .willReturn(Mono.just(false));

        given(userRepository.save(user)).willReturn(Mono.just(user));

        StepVerifier.create(userUseCase.save(user, "client"))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void findAll_whenUsersExists_thenShouldReturnTextEventStream() {
        given(userRepository.findAll()).willReturn(Flux.just(user, user));

        StepVerifier.create(userUseCase.findAll())
                .expectNext(user).
                expectNext(user)
                .verifyComplete();
    }
}