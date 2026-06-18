package io.github.vishwajiths2005.lafit_backend.repositories;

import io.github.vishwajiths2005.lafit_backend.enums.ActionType;
import io.github.vishwajiths2005.lafit_backend.enums.ResolutionStatus;
import io.github.vishwajiths2005.lafit_backend.models.ResolutionClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResolutionClaimRepository extends JpaRepository<ResolutionClaim, UUID> {

    Optional<ResolutionClaim> findByItemId(UUID id);
    Optional<ResolutionClaim> findByItemIdAndClaimantId(UUID item_id, UUID claimant_id);

    List<ResolutionClaim> findByClaimantId(UUID claimant_id);

    List<ResolutionClaim> findByActionType(ActionType actionType);
    List<ResolutionClaim> findByActionTypeAndClaimantId(ActionType actionType, UUID claimant_id);

    List<ResolutionClaim> findByStatus(ResolutionStatus status);
    List<ResolutionClaim> findByStatusAndClaimantId(ResolutionStatus status, UUID claimant_id);

    List<ResolutionClaim> findByStatusAndActionType(ResolutionStatus status, ActionType actionType);
    List<ResolutionClaim> findByStatusAndActionTypeAndClaimantId(ResolutionStatus status, ActionType actionType, UUID claimant_id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ResolutionClaim rc SET rc.status = :newStatus WHERE rc.item.id = :itemId AND rc.id != :approvedClaimId")
    void rejectOtherClaimsForItem(
            @Param("itemId") UUID itemId,
            @Param("approvedClaimId") UUID approvedClaimId,
            @Param("newStatus") ResolutionStatus newStatus
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ResolutionClaim rc SET rc.status = :newStatus WHERE rc.item.id = :itemId AND rc.status = :oldStatus")
    void revertRejectedClaimsToPending(
            @Param("itemId") UUID itemId,
            @Param("oldStatus") ResolutionStatus oldStatus,
            @Param("newStatus") ResolutionStatus newStatus
    );

}
