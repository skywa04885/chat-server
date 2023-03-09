package nl.lukerieff.protocol.channel.services;

public abstract class ResponseHandler {
    /**
     * Gets called when the response has been received.
     * @param response the received response.
     */
    public abstract void handle(final Response response);
}
