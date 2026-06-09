package io.github.vishwajiths2005.lafit_backend.controllers;

import io.github.vishwajiths2005.lafit_backend.dtos.ChangeStatusRequest;
import io.github.vishwajiths2005.lafit_backend.dtos.ItemResponse;
import io.github.vishwajiths2005.lafit_backend.enums.ItemStatus;
import io.github.vishwajiths2005.lafit_backend.enums.ItemType;
import io.github.vishwajiths2005.lafit_backend.models.AccessDeniedException;
import io.github.vishwajiths2005.lafit_backend.models.Item;
import io.github.vishwajiths2005.lafit_backend.models.MyUserDetails;
import io.github.vishwajiths2005.lafit_backend.services.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ItemResponse addItem(@RequestBody Item item, @AuthenticationPrincipal MyUserDetails userDetails) {
        item.setStatus(ItemStatus.PENDING);
        UUID userId = userDetails.getId();
        return itemService.add(item, userId);
    }

    @GetMapping("")
    public List<ItemResponse> getItems(@AuthenticationPrincipal MyUserDetails userDetails) {
        String role = userDetails.getRole().toString();
        if(role.equals("USER")) {
            return itemService.getByUserId(userDetails.getId());
        } else if (role.equals("ADMIN")) {
            return itemService.getAllItems();
        }
        else {
            return null;
        }
    }

    @GetMapping("/status/{status}")
    public List<ItemResponse> getItemsByStatus(
            @PathVariable ItemStatus status,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        if(status == ItemStatus.APPROVED || status == ItemStatus.RESOLVED) {
            return itemService.getByStatus(status);
        }
        String role = userDetails.getRole().toString();
        if(role.equals("USER")) {
            return itemService.getByStatusAndUserId(status, userDetails.getId());
        } else if (role.equals("ADMIN")) {
            return itemService.getByStatus(status);
        }
        else {
            return null;
        }
    }

    @GetMapping("/type/{type}")
    public List<ItemResponse> getItemsByType(
            @PathVariable ItemType type,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        String role = userDetails.getRole().toString();
        if(role.equals("USER")) {
            return itemService.getByTypeAndUserId(type, userDetails.getId());
        } else if (role.equals("ADMIN")) {
            return itemService.getByType(type);
        }
        else {
            return null;
        }
    }

    @GetMapping("/both")
    public List<ItemResponse> getItemsByBoth(
            @RequestParam(name = "status") ItemStatus status,
            @RequestParam(name = "type") ItemType type,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        if(status == ItemStatus.APPROVED || status == ItemStatus.RESOLVED) {
            return itemService.getByStatusAndType(status, type);
        }
        String role = userDetails.getRole().toString();
        if(role.equals("USER")) {
            return itemService.getByStatusAndTypeAndUserId(status, type, userDetails.getId());
        } else if (role.equals("ADMIN")) {
            return itemService.getByStatusAndType(status, type);
        }
        else {
            return null;
        }
    }

    @PutMapping("/{id}")
    public ItemResponse editItem(
            @RequestBody Item item,
            @PathVariable UUID id,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        String role = userDetails.getRole().toString();
        if(role.equals("USER")) {
            ItemResponse item1 = itemService.getById(id);
            if(!item1.getUsername().equals(userDetails.getUsername())) {
                throw new AccessDeniedException("Wrong user to access.");
            }
            item.setStatus(ItemStatus.PENDING);
        }
        return itemService.edit(item, id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ItemResponse editStatus(
            @Valid @RequestBody ChangeStatusRequest changeStatusRequest,
            @PathVariable UUID id
    ) {
        if(changeStatusRequest.getStatus() == ItemStatus.RESOLVED) {
            throw new AccessDeniedException("This status isn't yours to conquer.");
        }
        return itemService.editStatus(changeStatusRequest.getStatus(), id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(
            @PathVariable UUID id,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) throws AccessDeniedException {
        String role = userDetails.getRole().toString();
        if(role.equals("USER")) {
            ItemResponse item = itemService.getById(id);
            if(userDetails.getUsername().equals(item.getUsername())) {
                itemService.delete(id);
            }
            else {
                throw new AccessDeniedException("This item isn't yours to conquer.");
            }
        } else if (role.equals("ADMIN")) {
            itemService.delete(id);
        }
    }

}
