package io.github.vishwajiths2005.lafit_backend.services;

import io.github.vishwajiths2005.lafit_backend.dtos.ItemResponse;
import io.github.vishwajiths2005.lafit_backend.enums.ItemStatus;
import io.github.vishwajiths2005.lafit_backend.enums.ItemType;
import io.github.vishwajiths2005.lafit_backend.events.ItemEditedEvent;
import io.github.vishwajiths2005.lafit_backend.mappers.ItemMapper;
import io.github.vishwajiths2005.lafit_backend.models.AlreadyExistsException;
import io.github.vishwajiths2005.lafit_backend.models.Item;
import io.github.vishwajiths2005.lafit_backend.models.NotFoundException;
import io.github.vishwajiths2005.lafit_backend.models.Users;
import io.github.vishwajiths2005.lafit_backend.repositories.ItemRepository;
import lombok.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public ItemService(ItemMapper itemMapper, ItemRepository itemRepository, UserService userService, ApplicationEventPublisher eventPublisher) {
        this.itemMapper = itemMapper;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    public ItemResponse add(Item item, UUID userId) {
        Users user = userService.getById(userId);
        item.setReportedBy(user);
        List<Item> items = itemRepository.findByItemName(item.getItemName());
        for(Item item2 : items) {
            if(item.isSameItem(item2)) {
                throw new AlreadyExistsException("Same item 2 requests, why?");
            }
        }
        return itemMapper.itemToItemResponse(itemRepository.save(item));
    }

    public ItemResponse getById(UUID itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        return itemMapper.itemToItemResponse(item);
    }

    public void delete(UUID id) {
        itemRepository.deleteById(id);
    }

    public List<ItemResponse> getAllItems() {
        return itemRepository
                .findAll()
                .stream()
                .map(itemMapper::itemToItemResponse)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getByUserId(UUID userId) {
        return itemRepository
                .findByReportedById(userId)
                .stream()
                .map(itemMapper::itemToItemResponse)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getByStatus(ItemStatus status) {
        return itemRepository
                .findByStatus(status)
                .stream()
                .map(itemMapper::itemToItemResponse)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getByStatusAndUserId(ItemStatus status, UUID userId) {
        return itemRepository
                .findByStatusAndReportedById(status, userId)
                .stream()
                .map(itemMapper::itemToItemResponse)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getByTypeAndUserId(ItemType type, UUID id) {
        return itemRepository
                .findByTypeAndReportedById(type, id)
                .stream()
                .map(itemMapper::itemToItemResponse)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getByType(ItemType type) {
        return itemRepository
                .findByType(type)
                .stream()
                .map(itemMapper::itemToItemResponse)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getByStatusAndTypeAndUserId(ItemStatus status, ItemType type, UUID id) {
        return itemRepository
                .findByTypeAndStatusAndReportedById(type, status, id)
                .stream()
                .map(itemMapper::itemToItemResponse)
                .collect(Collectors.toList());
    }

    public List<ItemResponse> getByStatusAndType(ItemStatus status, ItemType type) {
        return itemRepository
                .findByTypeAndStatus(type, status)
                .stream()
                .map(itemMapper::itemToItemResponse)
                .collect(Collectors.toList());
    }

    public ItemResponse edit(Item item, UUID id) {
        Item oldItem = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found."));

        boolean isMeaningfulEdit = false;

        if(item.getType() != null) {
            oldItem.setType(item.getType());
            isMeaningfulEdit = true;
        }
        if(item.getItemName() != null) {
            oldItem.setItemName(item.getItemName());
            isMeaningfulEdit = true;
        }
        if(item.getItemDescription() != null) {
            oldItem.setItemDescription(item.getItemDescription());
            isMeaningfulEdit = true;
        }
        if(item.getImageLink() != null) {
            oldItem.setImageLink(item.getImageLink());
            isMeaningfulEdit = true;
        }
        if(item.getItemLocation() != null) {
            oldItem.setItemLocation(item.getItemLocation());
            isMeaningfulEdit = true;
        }
        if(item.getStatus() != null) {
            oldItem.setStatus(item.getStatus());
            isMeaningfulEdit = true;
        }

        if(isMeaningfulEdit) {
            eventPublisher.publishEvent(new ItemEditedEvent(this, id));
        }

        return itemMapper.itemToItemResponse(itemRepository.save(oldItem));
    }

    public ItemResponse editStatus(ItemStatus status, UUID id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Item not found."));
        item.setStatus(status);
        return itemMapper.itemToItemResponse(itemRepository.save(item));
    }

    public Item findById(@NonNull UUID itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
    }
}
