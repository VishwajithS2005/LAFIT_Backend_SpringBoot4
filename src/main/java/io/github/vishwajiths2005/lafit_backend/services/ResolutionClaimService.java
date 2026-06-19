package io.github.vishwajiths2005.lafit_backend.services;

import io.github.vishwajiths2005.lafit_backend.dtos.ResolutionClaimRequest;
import io.github.vishwajiths2005.lafit_backend.dtos.ResolutionClaimResponse;
import io.github.vishwajiths2005.lafit_backend.enums.ActionType;
import io.github.vishwajiths2005.lafit_backend.enums.ItemStatus;
import io.github.vishwajiths2005.lafit_backend.enums.ItemType;
import io.github.vishwajiths2005.lafit_backend.enums.ResolutionStatus;
import io.github.vishwajiths2005.lafit_backend.events.ItemEditedEvent;
import io.github.vishwajiths2005.lafit_backend.mappers.ResolutionClaimMapper;
import io.github.vishwajiths2005.lafit_backend.models.*;
import io.github.vishwajiths2005.lafit_backend.repositories.ResolutionClaimRepository;
import jakarta.validation.Valid;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResolutionClaimService {

    private final ResolutionClaimRepository claimRepository;
    private final ResolutionClaimMapper claimMapper;
    private final ItemService itemService;
    private final UserService userService;

    public ResolutionClaimService(ResolutionClaimRepository claimRepository, ResolutionClaimMapper claimMapper, ItemService itemService, UserService userService) {
        this.claimRepository = claimRepository;
        this.claimMapper = claimMapper;
        this.itemService = itemService;
        this.userService = userService;
    }

    public ResolutionClaimResponse add(@Valid ResolutionClaimRequest request, UUID id) {
        ResolutionClaim claim = new ResolutionClaim();
        Item item = itemService.findById(request.getItemId());
        claim.setItem(item);
        if(item.getType() == ItemType.FOUND) {
            claim.setActionType(ActionType.CLAIMING_FOUND_ITEM);
        }
        else if(item.getType() == ItemType.LOST) {
            claim.setActionType(ActionType.RETURNING_LOST_ITEM);
        }
        Users user = userService.getById(id);
        if(user.equals(item.getReportedBy())) {
            throw new SameUserException("Same user 2 places.");
        }
        claim.setClaimant(user);
        claim.setStatus(request.getStatus());
        Optional<ResolutionClaim> rc = claimRepository.findByItemId(request.getItemId());
        if(rc.isPresent() && rc.get().getStatus() == ResolutionStatus.APPROVED) {
            throw new AlreadyExistsException("The Resolution Claim has already been approved.");
        }
        Optional<ResolutionClaim> rc1 = claimRepository.findByItemIdAndClaimantId(request.getItemId(), id);
        if(rc1.isPresent()) {
            throw new AlreadyExistsException("The Resolution Claim has already been applied by the same user.");
        }

        return claimMapper.resolutionClaimToResponse(claimRepository.save(claim));
    }

    public List<ResolutionClaimResponse> getAllClaims() {
        return claimRepository
                .findAll()
                .stream()
                .map(claimMapper::resolutionClaimToResponse)
                .collect(Collectors.toList());
    }

    public List<ResolutionClaimResponse> getClaimsByUserId(UUID userId) {
        return claimRepository
                .findByClaimantId(userId)
                .stream()
                .map(claimMapper::resolutionClaimToResponse)
                .collect(Collectors.toList());
    }

    public List<ResolutionClaimResponse> getClaimsByType(ActionType type) {
        return claimRepository
                .findByActionType(type)
                .stream()
                .map(claimMapper::resolutionClaimToResponse)
                .collect(Collectors.toList());
    }

    public List<ResolutionClaimResponse> getClaimsByTypeAndUserId(ActionType type, UUID id) {
        return claimRepository
                .findByActionTypeAndClaimantId(type, id)
                .stream()
                .map(claimMapper::resolutionClaimToResponse)
                .collect(Collectors.toList());
    }

    public List<ResolutionClaimResponse> getClaimsByStatus(ResolutionStatus status) {
        return claimRepository
                .findByStatus(status)
                .stream()
                .map(claimMapper::resolutionClaimToResponse)
                .collect(Collectors.toList());
    }

    public List<ResolutionClaimResponse> getClaimsByStatusAndUserId(ResolutionStatus status, UUID id) {
        return claimRepository
                .findByStatusAndClaimantId(status, id)
                .stream()
                .map(claimMapper::resolutionClaimToResponse)
                .collect(Collectors.toList());
    }


    public List<ResolutionClaimResponse> getClaimsByStatusAndType(ResolutionStatus status, ActionType type) {
        return claimRepository
                .findByStatusAndActionType(status, type)
                .stream()
                .map(claimMapper::resolutionClaimToResponse)
                .collect(Collectors.toList());
    }

    public List<ResolutionClaimResponse> getClaimsByStatusAndTypeAndUserId(ResolutionStatus status, ActionType type, UUID id) {
        return claimRepository
                .findByStatusAndActionTypeAndClaimantId(status, type, id)
                .stream()
                .map(claimMapper::resolutionClaimToResponse)
                .collect(Collectors.toList());
    }

    public ResolutionClaim findByClaimId(UUID claimId) {
        return claimRepository.findById(claimId).orElseThrow(() -> new NotFoundException("You don't exist."));
    }

    public ResolutionClaimResponse editClaim(UUID claimId, ResolutionClaimRequest claimRequest, UUID id) {
        Item item = itemService.findById(claimRequest.getItemId());
        if(item.getReportedBy().getId().equals(id)) {
            throw new SameUserException("If you cook the same piece of meat again, you'll overcook it.");
        }
        ResolutionClaim rc = findByClaimId(claimId);
        rc.setItem(item);
        if(item.getType() == ItemType.LOST) {
            rc.setActionType(ActionType.RETURNING_LOST_ITEM);
        } else if (item.getType() == ItemType.FOUND) {
            rc.setActionType(ActionType.CLAIMING_FOUND_ITEM);
        }
        if(claimRequest.getStatus() != null) {
            rc.setStatus(claimRequest.getStatus());
        }
        return claimMapper.resolutionClaimToResponse(claimRepository.save(rc));
    }

    @Transactional
    public ResolutionClaimResponse editStatus(UUID claimId, ResolutionStatus status) {
        ResolutionClaim rc = findByClaimId(claimId);
        rc.setStatus(status);
        if(status == ResolutionStatus.APPROVED) {
            rc.getItem().setStatus(ItemStatus.RESOLVED);
            claimRepository.rejectOtherClaimsForItem(rc.getItem().getId(), claimId, ResolutionStatus.REJECTED);
        }
        return claimMapper.resolutionClaimToResponse(claimRepository.save(rc));
    }

    @Transactional
    public void deleteClaim(UUID claimId) {
        ResolutionClaim claim = claimRepository.findById(claimId).orElseThrow(() -> new NotFoundException("Not found."));
        if(claim.getStatus() == ResolutionStatus.APPROVED) {
            UUID itemId = claim.getItem().getId();
            claimRepository.deleteById(claimId);
            claimRepository.revertRejectedClaimsToPending(
                    itemId,
                    ResolutionStatus.REJECTED,
                    ResolutionStatus.PENDING
            );
            itemService.editStatus(ItemStatus.APPROVED, itemId);
        }
        else {
            claimRepository.deleteById(claimId);
        }
    }

    @Transactional
    @EventListener
    public void handleItemEditedEvent(ItemEditedEvent event) {
        claimRepository.deleteAllClaimsByItemId(event.getItemId());
    }
}
