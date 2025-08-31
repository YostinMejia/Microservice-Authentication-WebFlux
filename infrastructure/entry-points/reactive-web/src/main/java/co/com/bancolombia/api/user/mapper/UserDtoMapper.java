package co.com.bancolombia.api.user.mapper;

import co.com.bancolombia.api.user.dto.CreateUserDto;
import co.com.bancolombia.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    @Mapping(source ="birthDate", target = "birthDate",dateFormat = "yyyy-MM-dd")
    @Mapping(target = "idRol",ignore = true)
    User toUser(CreateUserDto createUserDto);

}
