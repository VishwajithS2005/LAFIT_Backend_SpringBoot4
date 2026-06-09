package io.github.vishwajiths2005.lafit_backend.dtos;

import io.github.vishwajiths2005.lafit_backend.enums.ActionType;
import io.github.vishwajiths2005.lafit_backend.enums.ResolutionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResolutionClaimRequest {

    @NonNull
    private UUID itemId;
    private ActionType actionType;
    private ResolutionStatus status;

}
