package nl.lukerieff.protocol.channel;

import nl.lukerieff.protocol.FrameOuterClass;
import nl.lukerieff.protocol.server.Server;
import nl.lukerieff.protocol.client.Client;
import nl.lukerieff.protocol.channel.events.Event;
import nl.lukerieff.protocol.channel.events.EventListeners;
import nl.lukerieff.protocol.channel.services.*;
import nl.lukerieff.protocol.channel.services.call.IncommingCall;
import nl.lukerieff.protocol.channel.services.call.OutgoingCall;

import java.util.HashMap;
import java.util.Map;

public class Channel {
    protected final int identifier;
    protected final Client client;
    protected final Map<Integer, IncommingCall> pendingIncomingCalls = new HashMap<>();
    protected final Map<Integer, OutgoingCall> pendingOutgoingCalls = new HashMap<>();
    protected Object userData;

    public Channel(final int identifier, final Client client) {
        this.identifier = identifier;
        this.client = client;
    }

    public final int getIdentifier() {
        return this.identifier;
    }

    public void setUserData(final Object userData) {
        this.userData = userData;
    }

    public final Object getUserData() {
        return this.userData;
    }


    /**
     * Handles the reception of a new event payload.
     * @param eventMsg the received event message.
     */
    protected void onEventMessage(final FrameOuterClass.Frame.Channeled.Event eventMsg) {
        // Decodes the event.
        final Event event = Event.decode(eventMsg);

        // Gets the event listeners and notifies all of them of the newly received event.
        final Server server = client.getServer();
        final EventListeners eventListeners = server.getChannelEventListeners();
        eventListeners.notifyAllListeners(this, event);
    }

    /**
     * Handles the reception of a new request payload.
     * @param requestMsg the received request message.
     */
    protected void onRequestMessage(final FrameOuterClass.Frame.Channeled.Request requestMsg) {
        // Decodes the request.
        final Request request = Request.decode(requestMsg);

        // Constructs the incoming call.
        final IncommingCall call = new IncommingCall(this, request);

        // Checks if there already is a request with the given request number,
        //  if so discard this, and just ignore it.
        if (pendingIncomingCalls.containsKey(request.getRequestNumber())) {
            return;
        }

        // Inserts the request into the pending requests map.
        pendingIncomingCalls.put(request.getRequestNumber(), call);

        // Calls the requested service.
        client.getServer().getServices().call(call);
    }

    /**
     * Handles the reception of a new response payload.
     * @param responseMsg the received response message.
     */
    protected void onResponseMessage(final FrameOuterClass.Frame.Channeled.Response responseMsg) {
        // Decodes the response.
        final Response response = Response.decode(responseMsg);

        // Gets the call and makes sure that it actually exists.
        final OutgoingCall call = pendingOutgoingCalls.get(response.getRequestNumber());
        if (call == null) {
            throw new Error("Received response payload for non-existing or already completed call");
        }

        // Calls the response handler.
        call.getResponseHandler().handle(response);

        // Removes the response handler from the map.
        pendingIncomingCalls.remove(response.getRequestNumber());
    }

    /**
     * Handles a newly received message.
     * @param channeledMsg the message to handle.
     */
    public void handleMessage(final FrameOuterClass.Frame.Channeled channeledMsg) {
        switch (channeledMsg.getChildCase()) {
            case EVENT -> onEventMessage(channeledMsg.getEvent());
            case REQUEST -> onRequestMessage(channeledMsg.getRequest());
            case RESPONSE -> onResponseMessage(channeledMsg.getResponse());
            case CHILD_NOT_SET -> {
                // TODO: Handle payload not set.
            }
        }
    }

    /**
     * Writes the given service response back to the client.
     * @param response the response to write back.
     */
    public void writeServiceResponse(final Response response) {
        // Makes sure that there is a pending request we're responding to.
        if (!pendingIncomingCalls.containsKey(response.getRequestNumber())) {
            throw new Error("Attempted to write response to non-pending service request.");
        }

        // Constructs the channeled message and the frame.
        final FrameOuterClass.Frame.Channeled channeledMsg = FrameOuterClass.Frame.Channeled
                .newBuilder().setChannel(identifier).setResponse(response.encode()).build();
        final FrameOuterClass.Frame frame = FrameOuterClass.Frame
                .newBuilder().setChanneled(channeledMsg).build();

        // Writes the frame.
        client.writeFrame(frame);

        // Removes the request from the map of pending service requests.
        pendingIncomingCalls.remove(response.getRequestNumber());
    }
}
