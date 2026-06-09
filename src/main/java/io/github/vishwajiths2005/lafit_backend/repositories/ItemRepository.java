package io.github.vishwajiths2005.lafit_backend.repositories;

import io.github.vishwajiths2005.lafit_backend.enums.ItemStatus;
import io.github.vishwajiths2005.lafit_backend.enums.ItemType;
import io.github.vishwajiths2005.lafit_backend.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    List<Item> findByStatus(ItemStatus status);
    List<Item> findByStatusAndReportedById(ItemStatus status, UUID id);

    List<Item> findByReportedById(UUID id);

    List<Item> findByType(ItemType type);
    List<Item> findByTypeAndReportedById(ItemType type, UUID id);

    List<Item> findByTypeAndStatus(ItemType type, ItemStatus status);
    List<Item> findByTypeAndStatusAndReportedById(ItemType type, ItemStatus status, UUID id);

    List<Item> findByItemName(String itemName);

}
