package io.github.vishwajiths2005.lafit_backend.models;

import io.github.vishwajiths2005.lafit_backend.enums.ItemStatus;
import io.github.vishwajiths2005.lafit_backend.enums.ItemType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private UUID id;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ItemType type;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ItemStatus status;

    @Setter
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Setter
    @Column(name = "item_description")
    private String itemDescription;

    @Setter
    @Column(name = "image_link")
    private String imageLink;

    @Setter
    @Column(name = "item_location", nullable = false)
    private String itemLocation;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reported_by",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ITEMS_USERS")
    )
    private Users reportedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public boolean isSameItem(Item item2) {
        return (
                this.getItemName().equals(item2.getItemName())
                && this.getItemDescription().equals(item2.getItemDescription())
                && this.getImageLink().equals(item2.getImageLink())
                && this.getItemLocation().equals(item2.getImageLink())
                && this.reportedBy.equals(item2.getReportedBy())
                );
    }

}
