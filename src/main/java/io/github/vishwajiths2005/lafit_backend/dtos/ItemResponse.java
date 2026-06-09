package io.github.vishwajiths2005.lafit_backend.dtos;

import io.github.vishwajiths2005.lafit_backend.enums.ItemStatus;
import io.github.vishwajiths2005.lafit_backend.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {

    private UUID id;
    private ItemType type;
    private ItemStatus status;
    private String itemName;
    private String itemDescription;
    private String imageLink;
    private String itemLocation;

    private String username;
    private String email;
}
