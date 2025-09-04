package digital.shraddha.mapper;

import digital.shraddha.model.dto.RefreshTokenDto;
import digital.shraddha.model.entity.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper (componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RefreshTokenMapper {

	@Mapping(target = "expiresAt", ignore = true)
	RefreshToken toEntity(RefreshTokenDto dto);

	RefreshTokenDto toDto(RefreshToken entity);
}
