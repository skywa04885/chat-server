package nl.lukerieff.protocol.channel.events;

import nl.lukerieff.protocol.channel.Channel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class EventListeners {
    protected Map<Integer, LinkedList<EventListener>> eventListenerMap = new HashMap<>();

    /**
     * Adds an event listener.
     * @param listener the listener to add.
     * @return the current instance.
     */
    public EventListeners add(final EventListener listener) {
        eventListenerMap.computeIfAbsent(listener.getEvent(), k -> new LinkedList<>()).add(listener);

        return this;
    }

    /**
     * Removes an event listener.
     * @param listener the listener to remove.
     * @return the current instance.
     */
    public EventListeners remove(final EventListener listener) {
        final LinkedList<EventListener> listeners = eventListenerMap.get(listener.getEvent());

        if (listeners != null) {
            listeners.remove(listener);
        }

        return this;
    }

    /**
     * Notifies all the event listeners about the event.
     *
     * @param channel the channel in which the event was emitted.
     * @param channeledEvent the channeled event that occurred.
     */
    public void notifyAllListeners(final Channel channel, final Event channeledEvent) {
        final LinkedList<EventListener> listeners = eventListenerMap.get(channeledEvent.getEvent());

        if (listeners == null) {
            return;
        }

        for (final EventListener listener: listeners) {
            listener.notify(channel, channeledEvent);
        }

    }
}
