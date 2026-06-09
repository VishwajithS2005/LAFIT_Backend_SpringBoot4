package io.github.vishwajiths2005.lafit_backend.mappers;

import io.github.vishwajiths2005.lafit_backend.dtos.ItemResponse;
import io.github.vishwajiths2005.lafit_backend.models.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(source = "reportedBy.username", target = "username")
    @Mapping(source = "reportedBy.email", target = "email")
    ItemResponse itemToItemResponse(Item item);

}
