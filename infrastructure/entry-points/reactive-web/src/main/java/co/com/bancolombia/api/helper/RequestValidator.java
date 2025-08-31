package co.com.bancolombia.api.helper;

import co.com.bancolombia.model.exceptions.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RequestValidator<T> {

    private final Validator validator;

    public Mono<T> validator(T dto) {

        return Mono.defer(() -> {
            final Set<ConstraintViolation<T>> errors = validator.validate(dto);
            if (errors.isEmpty()) {
                return Mono.just(dto);
            }

            final List<String> listErrors = errors.stream().map(er -> String.format("%s: %s", er.getPropertyPath(), er.getMessage())).toList();
            return Mono.error(new BusinessException(listErrors, "Create user validation failed", "B400-00"));

        });

    }

}
