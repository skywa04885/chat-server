package nl.lukerieff.protocol.channel.services.call;

import nl.lukerieff.protocol.channel.Channel;
import nl.lukerieff.protocol.channel.services.Request;
import nl.lukerieff.protocol.channel.services.ResponseHandler;

public class OutgoingCall extends Call {
    protected final ResponseHandler responseHandler;

    public OutgoingCall(final Channel channel, final Request request, final ResponseHandler responseHandler) {
        super(channel, request);
        this.responseHandler = responseHandler;
    }

    public final ResponseHandler getResponseHandler() {
        return responseHandler;
    }
}
