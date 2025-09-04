package digital.shraddha.mapper;

import digital.shraddha.model.dto.RegisterUserRequest;
import digital.shraddha.model.dto.UserDto;
import digital.shraddha.model.entity.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper (componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

	UserDto toDto(RegisterUserRequest request);

	@Mapping (target = "userId", source = "id")
	@Mapping (target = "password", ignore = true)
	@Mapping (target = "email", expression = "java(dto.email().toLowerCase())")
	@Mapping (target = "username", expression = "java(dto.username().toLowerCase())")
	AuthUser toAuthUser(UserDto dto);
}
