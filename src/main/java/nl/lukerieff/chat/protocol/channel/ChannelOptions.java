package nl.lukerieff.protocol.channel;

import nl.lukerieff.protocol.channel.authentication.Authenticator;

public class ChannelOptions {
    public static class Builder {
        private Authenticator authenticator;

        public Builder() {}

        public final Builder setAuthenticator(final Authenticator authenticator) {
            this.authenticator = authenticator;

            return this;
        }

        public final ChannelOptions build() {
            return new ChannelOptions(authenticator);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private final Authenticator authenticator;

    public ChannelOptions(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public final Authenticator getAuthenticator() {
        return this.authenticator;
    }
}
