package nl.lukerieff;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import dev.morphia.query.experimental.filters.Filters;
import nl.lukerieff.models.User;
import nl.lukerieff.protocol.FrameOuterClass;
import nl.lukerieff.protocol.channel.Channel;
import nl.lukerieff.protocol.channel.authentication.AuthenticationResult;
import nl.lukerieff.protocol.channel.authentication.Authenticator;
import nl.lukerieff.security.Token;
import nl.lukerieff.security.TokenSigner;

import java.util.logging.Logger;

/**
 * The token based authenticator.
 */
public class TokenAuthenticator extends Authenticator {
    private static final Logger logger = Logger.getLogger(TokenAuthenticator.class.getName());

    private final TokenSigner tokenSigner;

    public TokenAuthenticator(final TokenSigner tokenSigner) {
        this.tokenSigner = tokenSigner;
    }

    /**
     * Performs the authentication step for the given channel with the given body.
     * @param channel the channel to authenticate.
     * @param body the body containing the authentication token.
     * @return the result indicating if the client is accepted or rejected.
     */
    @Override
    public AuthenticationResult authenticate(final Channel channel, final ByteString body) {
        logger.info("Performing authentication for " + channel.getIdentifier());

        // If there is no body present, there can be no token, so reject the channel claim.
        if (body == null) {
            return AuthenticationResult.REJECTED;
        }

        // Attempts to decode the token authentication body.
        FrameOuterClass.TokenAuthenticationBody tokenAuthenticationBody;
        try {
            tokenAuthenticationBody = FrameOuterClass.TokenAuthenticationBody.parseFrom(body);
        } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
            logger.warning("Failed to decode token authentication body: "
                    + invalidProtocolBufferException.getMessage());
            return AuthenticationResult.REJECTED;
        }

        // Attempts to decode the token.
        final Token token = tokenSigner.verify(tokenAuthenticationBody.getToken());
        if (token == null) {
            return AuthenticationResult.REJECTED;
        }

        // Finds the user associated with the verified token.
        final User user = Main.datastore
                .find(User.class)
                .filter(Filters.eq("_id", token.getUserId()))
                .first();
        if (user == null) {
            logger.warning("Failed to perform token authentication, could not find user with id: " +
                    token.getUserId());
            return AuthenticationResult.REJECTED;
        }

        // Creates the session and stores it as user data in the channel.
        final Session session = new Session(user);
        channel.setUserData(session);

        // Accepts the client.
        return AuthenticationResult.ACCEPTED;
    }
}
