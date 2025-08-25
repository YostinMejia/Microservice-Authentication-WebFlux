package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.CreateUserDto;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    @Mapping(source ="birthDate", target = "birthDate",dateFormat = "yyyy-MM-dd")
    User toUser(CreateUserDto createUserDto);

}
