package nl.lukerieff.protocol.channel.services.call;

import com.google.protobuf.ByteString;
import nl.lukerieff.protocol.channel.Channel;
import nl.lukerieff.protocol.channel.services.Request;
import nl.lukerieff.protocol.channel.services.Response;

public class IncommingCall extends Call {
    public IncommingCall(Channel channel, Request request) {
        super(channel, request);
    }

    /**
     * Responds to the request with an empty response body.
     */
    public final void respond() {
        respond(null);
    }

    /**
     * Responds to the request with the given body.
     * @param body the body to respond with.
     */
    public final void respond(final ByteString body) {
        // Constructs a new response builder.
        final Response.Builder responseBuilder = Response.newBuilder(request.getRequestNumber());

        // If there is a body, put the body in the builder.
        if (body != null) {
            responseBuilder.setBody(body);
        }

        // Builds the service response.
        final Response response = responseBuilder.build();

        // Writes the service response to the channel.
        channel.writeServiceResponse(response);
    }
}
