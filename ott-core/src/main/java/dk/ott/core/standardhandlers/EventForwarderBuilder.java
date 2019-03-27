package dk.ott.core.standardhandlers;

import dk.ott.core.eventhandling.AttributeValueCollector;
import dk.ott.core.eventhandling.ClearStoreHandler;
import dk.ott.core.eventhandling.EventHandler;
import dk.ott.core.eventhandling.TextValueCollector;

import java.util.ArrayList;
import java.util.List;

public class EventForwarderBuilder {
    private List<EventHandler> eventHandlers;

    private EventForwarderBuilder() {
        this.eventHandlers = new ArrayList<EventHandler>();
    }

    public static EventForwarderBuilder builder() {
        return new EventForwarderBuilder();
    }

    public EventForwarderBuilder attributeCollector(String attributeName, String storeKey) {
        eventHandlers.add(new AttributeValueCollector(attributeName, storeKey));
        return this;
    }

    public EventForwarderBuilder textValueCollector(String storeKey) {
        eventHandlers.add(new TextValueCollector(storeKey));
        return this;
    }

    public EventForwarderBuilder clearObjectStoreKeys(String... storeKey) {
        eventHandlers.add(new ClearStoreHandler(storeKey));
        return this;
    }

    public EventForwarder build() {
        return new EventForwarder(eventHandlers.toArray(new EventHandler[eventHandlers.size()]));
    }
}
