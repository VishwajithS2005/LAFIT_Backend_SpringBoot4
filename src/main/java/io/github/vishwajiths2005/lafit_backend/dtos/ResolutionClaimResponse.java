package io.github.vishwajiths2005.lafit_backend.dtos;

import io.github.vishwajiths2005.lafit_backend.enums.ActionType;
import io.github.vishwajiths2005.lafit_backend.enums.ItemType;
import io.github.vishwajiths2005.lafit_backend.enums.ResolutionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResolutionClaimResponse {

    private UUID id;
    private ActionType actionType;
    private ResolutionStatus status;

    private ItemType itemType;
    private String itemName;
    private String itemDescription;
    private String imageLink;
    private String itemLocation;

    private String reportedByUsername;
    private String reportedByEmail;

    private String claimantUsername;
    private String claimantEmail;

}
