package io.github.vishwajiths2005.lafit_backend.dtos;

import io.github.vishwajiths2005.lafit_backend.enums.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeStatusRequest {

    @NonNull
    private ItemStatus status;

}
