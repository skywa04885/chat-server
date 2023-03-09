package nl.lukerieff.protocol.channel.events;

import com.google.protobuf.ByteString;
import nl.lukerieff.protocol.FrameOuterClass;

public class Event {
    protected final int event;
    protected final long timestamp;
    protected final ByteString body;

    public Event(final int event, final long timestamp, final ByteString body) {
        this.event = event;
        this.timestamp = timestamp;
        this.body = body;
    }

    public final int getEvent() {
        return this.event;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

    public final ByteString getBody() {
        return this.body;
    }

    public static Event decode(final FrameOuterClass.Frame.Channeled.Event eventMsg) {
        return new Event(eventMsg.getEvent(), eventMsg.getTimestamp(), eventMsg.getBody());
    }
}
