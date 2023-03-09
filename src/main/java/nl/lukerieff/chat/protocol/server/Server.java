package nl.lukerieff.protocol.server;

import nl.lukerieff.protocol.channel.ChannelOptions;
import nl.lukerieff.protocol.client.Client;
import nl.lukerieff.protocol.channel.events.EventListeners;
import nl.lukerieff.protocol.channel.services.Services;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Logger;

public class Server implements Runnable {
    public static class Builder {
        public static final int DEFAULT_PORT = 50051;
        public static final int DEFAULT_BACKLOG = 1000;
        public static final InetAddress DEFAULT_INET_ADDRESS = null;
        public static final ChannelOptions DEFAULT_CHANNEL_OPTIONS = ChannelOptions.newBuilder().build();

        private int port = DEFAULT_PORT;
        private int backlog = DEFAULT_BACKLOG;
        private InetAddress inetAddress = DEFAULT_INET_ADDRESS;
        private ChannelOptions channelOptions = DEFAULT_CHANNEL_OPTIONS;
        private EventListeners eventListeners = new EventListeners();
        private Services services = new Services();

        public final Builder setPort(final int port) {
            this.port = port;

            return this;
        }

        public final Builder setBacklog(final int backlog) {
            this.backlog = backlog;

            return this;
        }

        public final Builder setInetAddress(final InetAddress inetAddress) {
            this.inetAddress = inetAddress;

            return this;
        }

        public final Builder setChannelOptions(final ChannelOptions channelOptions) {
            this.channelOptions = channelOptions;

            return this;
        }

        public final Builder setEventListeners(final EventListeners eventListeners) {
            this.eventListeners = eventListeners;

            return this;
        }

        public final Builder setServices(final Services services) {
            this.services = services;

            return this;
        }

        /**
         * Builds the server.
         * @param sslServerSocketFactory the ssl socket factory.
         * @return the built server.
         * @throws IOException the possible io exception.
         */
        public final Server build(final SSLServerSocketFactory sslServerSocketFactory) throws IOException {
            final SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory
                    .createServerSocket(port, backlog, inetAddress);

            return new Server(sslServerSocket, channelOptions, eventListeners, services);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private final SSLServerSocket sslServerSocket;
    private final ChannelOptions channelOptions;
    private final EventListeners channelEventListeners;
    private final Services services;
    private final Logger logger;

    /**
     * Constructs a new server.
     * @param sslServerSocket the server socket.
     * @param channelOptions the options for channels.
     * @param channelEventListeners the event listeners for channels.
     * @param services the services.
     */
    protected Server(final SSLServerSocket sslServerSocket, final ChannelOptions channelOptions,
                     final EventListeners channelEventListeners, final Services services) {
        this.sslServerSocket = sslServerSocket;
        this.channelOptions = channelOptions;
        this.channelEventListeners = channelEventListeners;
        this.services = services;
        this.logger = Logger.getLogger(Server.class.getName() + " " + sslServerSocket.getLocalSocketAddress() + ":" + sslServerSocket.getLocalPort());
    }

    public EventListeners getChannelEventListeners() {
        return this.channelEventListeners;
    }

    public final ChannelOptions getChannelOptions() {
        return this.channelOptions;
    }

    public Services getServices() {
        return this.services;
    }

    /**
     * Accepts a new client.
     */
    protected void acceptClient() {
        try {
            // Accepts a new SSL socket.
            final SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
            this.logger.info("Accepted new client socket " + sslSocket.getRemoteSocketAddress() + ", starting handshake ...");

            // Performs the SSL handshake.
            sslSocket.startHandshake();
            this.logger.info("Handshake finished.");

            // Creates the new server client, and a thread for it, after which we start it.
            final Client protocolClient = new Client(this, sslSocket);
            final Thread protocolClientThread = new Thread(protocolClient);
            protocolClientThread.start();
        } catch (IOException e) {
            this.logger.warning("An error occured while accepting new ssl socket: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        this.logger.info("Thread has started to accept new client sockets ...");

        while (true) {
            acceptClient();
        }
    }
}
