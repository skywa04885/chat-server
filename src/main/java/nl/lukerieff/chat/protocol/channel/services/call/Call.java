package nl.lukerieff.protocol.channel.services.call;

import nl.lukerieff.protocol.channel.Channel;
import nl.lukerieff.protocol.channel.services.Request;

public class Call {
    protected final Channel channel;
    protected final Request request;

    public Call(final Channel channel, final Request request) {
        this.channel = channel;
        this.request = request;
    }

    public final Channel getChannel() {
        return channel;
    }

    public final Request getRequest() {
        return request;
    }
}
