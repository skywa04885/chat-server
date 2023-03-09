package nl.lukerieff.protocol.client;

import nl.lukerieff.protocol.FrameOuterClass;
import nl.lukerieff.protocol.ProtocolError;
import nl.lukerieff.protocol.channel.Channel;
import nl.lukerieff.protocol.channel.ChannelOptions;
import nl.lukerieff.protocol.channel.authentication.AuthenticationResult;
import nl.lukerieff.protocol.channel.authentication.Authenticator;
import nl.lukerieff.protocol.server.Server;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Client implements Runnable {
    protected final Server server;
    protected final SSLSocket sslSocket;

    protected Map<Integer, Channel> channels = new HashMap<>();

    public Client(final Server server, final SSLSocket sslSocket) {
        this.server = server;
        this.sslSocket = sslSocket;
    }

    public Server getServer() {
        return this.server;
    }

    /**
     * Handles a channel claim command.
     * @param claimChannelMsg the claim channel message.
     */
    protected void onClaimChannelMessage(final FrameOuterClass.Frame.System.ClaimChannel claimChannelMsg) {
        // Makes sure the channel has not been claimed yet.
        if (channels.containsKey(claimChannelMsg.getChannel())) {
            throw new ProtocolError("Cannot claim channel once already claimed.");
        }

        // Creates the channel (temporary because we still need to possibly authenticate).
        final Channel channel = new Channel(claimChannelMsg.getChannel(), this);

        // Gets the authenticator.
        final Server server = getServer();
        final ChannelOptions channelOptions = server.getChannelOptions();
        final Authenticator authenticator = channelOptions.getAuthenticator();

        // Checks if an authenticator is present, if so perform the authentication procedure
        //  and accept or reject the channel claim based uppon that.
        if (authenticator != null) {
            // Performs the authentication procedure.
            final AuthenticationResult authenticationResult =
                    authenticator.authenticate(channel, claimChannelMsg.getBody());

            // Rejects the channel claim if needed.
            if (authenticationResult == AuthenticationResult.REJECTED) {
                return;
            }
        }

        // Claims the channel.
        this.channels.put(claimChannelMsg.getChannel(), new Channel(claimChannelMsg.getChannel(), this));
    }


    /**
     * Handles a channel release command.
     * @param releaseChannelMsg the release channel message.
     */
    protected void onReleaseChannelMessage(final FrameOuterClass.Frame.System.ReleaseChannel releaseChannelMsg) {
        // Makes sure the channel is actually claimed.
        Channel channel = channels.get(releaseChannelMsg.getChannel());
        if (channel == null) {
            throw new ProtocolError("Cannot release channel if not claimed.");
        }

        // Release the channel.
        this.channels.remove(releaseChannelMsg.getChannel());
    }

    /**
     * Handles a change in heartbeat command.
     * @param changeHeartbeatPeriodMsg the change heartbeat period message.
     */
    protected void onChangeHeartBeatMessage(final FrameOuterClass.Frame.System.ChangeHeartbeatPeriod changeHeartbeatPeriodMsg) {

    }

    /**
     * Handles a frame that contains a system payload.
     * @param system the system message.
     */
    protected void onSystemMessage(final FrameOuterClass.Frame.System system) {
        // Checks the kind of system payload we're dealing with and calls the appropriate handlers.
        switch (system.getChildCase()) {
            case CLAIM_CHANNEL -> onClaimChannelMessage(system.getClaimChannel());
            case RELEASE_CHANNEL -> onReleaseChannelMessage(system.getReleaseChannel());
            case CHANGE_HEARTBEAT_PERIOD ->
                    onChangeHeartBeatMessage(system.getChangeHeartbeatPeriod());
            case CHILD_NOT_SET -> {
                // TODO: Handle payload not set.
            }
        }
    }

    /**
     * Handles a frame that contains a heartbeat payload.
     * @param heartbeatMsg the heartbeat message.
     */
    protected void onHeartBeatMessage(final FrameOuterClass.Frame.Heartbeat heartbeatMsg) {

    }

    /**
     * Handles a frame that is meant to be forwarded to a channel.
     * @param channeledMsg the channeled message.
     */
    protected void onChanneledMessage(final FrameOuterClass.Frame.Channeled channeledMsg) {
        // Gets the channel to forward to.
        Channel channel = channels.get(channeledMsg.getChannel());
        if (channel == null) {
            throw new ProtocolError("Cannot forward channeled payload because channel " + channeledMsg.getChannel() + " has never been claimed!");
        }

        // Forwards the message to the channel.
        channel.handleMessage(channeledMsg);
    }

    /**
     * Processes a received frame.
     * @param frame the received frame to be processed.
     */
    protected void onFrame(final FrameOuterClass.Frame frame) {
        switch (frame.getChildCase()) {
            case SYSTEM -> onSystemMessage(frame.getSystem());
            case CHANNELED -> onChanneledMessage(frame.getChanneled());
            case HEARTBEAT -> onHeartBeatMessage(frame.getHeartbeat());
            case CHILD_NOT_SET -> {
                // TODO: Handle payload not set.
            }
        }

    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        try {
            final InputStream inputStream = sslSocket.getInputStream();

            while (true) {
                // Reads the first 4 bytes that will give us the length of the frame to be sent.
                final byte[] frameLengthBytes = inputStream.readNBytes(4);
                final int frameLength = ByteBuffer.wrap(frameLengthBytes).getInt();

                // Reads the frame with the just read length.
                final byte[] frameBytes = inputStream.readNBytes(frameLength);
                final FrameOuterClass.Frame frame = FrameOuterClass.Frame.parseFrom(frameBytes);

                // Processes the frame.
                onFrame(frame);
            }
        } catch (IOException e) {

        }
    }

    /**
     * Writes the given frame to the client.
     * @param frame the frame to write to the client.
     */
    public void writeFrame(final FrameOuterClass.Frame frame) {
        try {
            // Gets the output stream from the socket.
            final OutputStream outputStream = sslSocket.getOutputStream();

            // Gets the frame buffer and the buffer containing the length bytes.
            final byte[] frameBuffer = frame.toByteArray();
            final byte[] lengthBuffer = ByteBuffer.allocate(4)
                    .putInt(0, frameBuffer.length).array();

            // Writes the length and the frame to the socket.
            outputStream.write(lengthBuffer);
            outputStream.write(frameBuffer);

            // Flushes the buffer, which will slow down the communication but does not
            //  matter since we don't want it to become over-full.
            outputStream.flush();
        } catch (IOException ioException) {
            // TODO: Handle IO Exception.
        }
    }
}
