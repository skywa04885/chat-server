package nl.lukerieff.protocol.channel.authentication;

import com.google.protobuf.ByteString;
import nl.lukerieff.protocol.channel.Channel;

public abstract class Authenticator {
    public abstract AuthenticationResult authenticate(final Channel channel, final ByteString body);
}
