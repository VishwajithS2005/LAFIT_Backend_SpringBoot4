package io.github.vishwajiths2005.lafit_backend.controllers;

import io.github.vishwajiths2005.lafit_backend.dtos.ResolutionClaimRequest;
import io.github.vishwajiths2005.lafit_backend.dtos.ResolutionClaimResponse;
import io.github.vishwajiths2005.lafit_backend.enums.ActionType;
import io.github.vishwajiths2005.lafit_backend.enums.ResolutionStatus;
import io.github.vishwajiths2005.lafit_backend.models.AccessDeniedException;
import io.github.vishwajiths2005.lafit_backend.models.MyUserDetails;
import io.github.vishwajiths2005.lafit_backend.models.ResolutionClaim;
import io.github.vishwajiths2005.lafit_backend.services.ResolutionClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resolution")
public class ResolutionClaimController {

    private final ResolutionClaimService claimService;

    public ResolutionClaimController(ResolutionClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('USER')")
    public ResolutionClaimResponse createClaim(
            @Valid @RequestBody ResolutionClaimRequest request,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        request.setStatus(ResolutionStatus.PENDING);
        return claimService.add(request, userDetails.getId());
    }

    @GetMapping("")
    public List<ResolutionClaimResponse> getAllClaims(@AuthenticationPrincipal MyUserDetails userDetails) {
        String role = userDetails.getRole().toString();
        if(role.equals("ADMIN")) {
            return claimService.getAllClaims();
        } else if (role.equals("USER")) {
            return claimService.getClaimsByUserId(userDetails.getId());
        }
        return null;
    }

    @GetMapping("/type/{type}")
    public List<ResolutionClaimResponse> getClaimsByType(
            @PathVariable ActionType type,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        String role = userDetails.getRole().toString();
        if(role.equals("ADMIN")) {
            return claimService.getClaimsByType(type);
        } else if (role.equals("USER")) {
            return claimService.getClaimsByTypeAndUserId(type, userDetails.getId());
        }
        return null;
    }

    @GetMapping("/status/{status}")
    public List<ResolutionClaimResponse> getClaimsByStatus(
            @PathVariable ResolutionStatus status,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        if(status == ResolutionStatus.APPROVED) {
            return claimService.getClaimsByStatus(status);
        }
        String role = userDetails.getRole().toString();
        if(role.equals("ADMIN")) {
            return claimService.getClaimsByStatus(status);
        } else if (role.equals("USER")) {
            return claimService.getClaimsByStatusAndUserId(status, userDetails.getId());
        }
        return null;
    }

    @GetMapping("/both")
    public List<ResolutionClaimResponse> getClaimsByBoth(
            @RequestParam(name = "status") ResolutionStatus status,
            @RequestParam(name = "type") ActionType type,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        if(status == ResolutionStatus.APPROVED) {
            return claimService.getClaimsByStatusAndType(status, type);
        }
        String role = userDetails.getRole().toString();
        if(role.equals("ADMIN")) {
            return claimService.getClaimsByStatusAndType(status, type);
        } else if (role.equals("USER")) {
            return claimService.getClaimsByStatusAndTypeAndUserId(status, type, userDetails.getId());
        }
        return null;
    }

    @PutMapping("/{claimId}")
    public ResolutionClaimResponse editClaim(
            @PathVariable UUID claimId,
            @RequestBody ResolutionClaimRequest claimRequest,
            @AuthenticationPrincipal MyUserDetails userDetails
    ) {
        String role = userDetails.getRole().toString();
        if(role.equals("USER")) {
            ResolutionClaim rc = claimService.findByClaimId(claimId);
            if(!rc.getClaimant().getUsername().equals(userDetails.getUsername())) {
                throw new AccessDeniedException("This claim is not yours to conquer.");
            }
            claimRequest.setStatus(ResolutionStatus.PENDING);
        }
        return claimService.editClaim(claimId, claimRequest, userDetails.getId());
    }

    @PatchMapping("/{claimId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResolutionClaimResponse changeStatus(
            @PathVariable UUID claimId,
            @RequestParam(name = "status") ResolutionStatus status
    ) {
        return claimService.editStatus(claimId, status);
    }

    @DeleteMapping("/{claimId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable UUID claimId, @AuthenticationPrincipal MyUserDetails userDetails) {
        ResolutionClaim rc = claimService.findByClaimId(claimId);
        String role = userDetails.getRole().toString();
        if(role.equals("ADMIN")) {
            claimService.deleteClaim(claimId);
        } else if (role.equals("USER")) {
            if(rc.getClaimant().getUsername().equals(userDetails.getUsername())) {
                claimService.deleteClaim(claimId);
            }
            else {
                throw new AccessDeniedException("This claim isn't yours to conquer.");
            }
        }
    }

}
