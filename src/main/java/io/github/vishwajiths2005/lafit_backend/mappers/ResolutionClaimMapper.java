package io.github.vishwajiths2005.lafit_backend.mappers;

import io.github.vishwajiths2005.lafit_backend.dtos.ResolutionClaimResponse;
import io.github.vishwajiths2005.lafit_backend.models.ResolutionClaim;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResolutionClaimMapper {

    @Mapping(source = "item.type", target = "itemType")
    @Mapping(source = "item.itemName", target = "itemName")
    @Mapping(source = "item.itemDescription", target = "itemDescription")
    @Mapping(source = "item.imageLink", target = "imageLink")
    @Mapping(source = "item.itemLocation", target = "itemLocation")
    @Mapping(source = "item.reportedBy.username", target = "reportedByUsername")
    @Mapping(source = "item.reportedBy.email", target = "reportedByEmail")
    @Mapping(source = "claimant.username", target = "claimantUsername")
    @Mapping(source = "claimant.email", target = "claimantEmail")
    ResolutionClaimResponse resolutionClaimToResponse(ResolutionClaim resolutionClaim);

}
