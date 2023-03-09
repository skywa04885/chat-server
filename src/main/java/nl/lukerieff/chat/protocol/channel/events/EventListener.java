package nl.lukerieff.protocol.channel.events;

import nl.lukerieff.protocol.channel.Channel;

public abstract class EventListener {
    protected final int event;

    public EventListener(final int event) {
        this.event = event;
    }

    public final int getEvent() {
        return this.event;
    }

    /**
     * Gets called when a new event has been emitted for this listener.
     *
     * @param channel the channel in which the event was emitted.
     * @param channeledEvent the emitted event.
     */
    public abstract void notify(final Channel channel, final Event channeledEvent);
}
