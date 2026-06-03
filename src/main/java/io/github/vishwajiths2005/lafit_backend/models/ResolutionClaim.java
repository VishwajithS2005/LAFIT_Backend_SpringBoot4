package io.github.vishwajiths2005.lafit_backend.models;

import io.github.vishwajiths2005.lafit_backend.enums.ActionType;
import io.github.vishwajiths2005.lafit_backend.enums.ResolutionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "resolution_claims")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ResolutionClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private UUID id;

    @Setter
    @OneToOne
    @JoinColumn(
            name = "item",
            referencedColumnName = "id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "FK_RC_ITEMS")
    )
    private Item item;

    @Setter
    @ManyToOne
    @JoinColumn(
            name = "claimant",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_RC_USERS")
    )
    private Users claimant;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ResolutionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
