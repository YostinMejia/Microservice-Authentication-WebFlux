package co.com.bancolombia.api.helper;

import co.com.bancolombia.api.dto.CreateUserDto;
import co.com.bancolombia.model.user.exceptions.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public Mono<CreateUserDto> validator(CreateUserDto createUserDto) {

        return Mono.defer(() -> {
            final Set<ConstraintViolation<CreateUserDto>> errors = validator.validate(createUserDto);
            if (errors.isEmpty()) {
                return Mono.just(createUserDto);
            }

            final List<String> listErrors = errors.stream().map(er -> String.format("%s: %s", er.getPropertyPath(), er.getMessage())).toList();
            return Mono.error(new BusinessException(listErrors, "Create user validation failed", "B400-00"));

        });

    }

}
