package io.github.vishwajiths2005.lafit_backend.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class ItemEditedEvent extends ApplicationEvent {

    private final UUID itemId;

    public ItemEditedEvent(Object source, UUID itemId) {
        super(source);
        this.itemId = itemId;
    }
}
