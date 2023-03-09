package nl.lukerieff.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.logging.Logger;

public class TokenSigner {
    private static Logger logger = Logger.getLogger(TokenSigner.class.getName());

    private final Algorithm algorithm;
    private final String issuer;

    public TokenSigner(final Algorithm algorithm, final String issuer) {
        this.algorithm = algorithm;
        this.issuer = issuer;
    }

    /**
     * Signs the given token.
     *
     * @param token the token to sign.
     * @return the signed token.
     */
    public String sign(final Token token) {
        final Date issuedAt = new Date();
        final Date expiresAt = Date.from(LocalDateTime.now().plusMonths(4).toInstant(ZoneOffset.UTC));

        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withClaim("user", token.userId.toHexString())
                .sign(algorithm);
    }

    /**
     * Attempts to verify the given signed token.
     *
     * @param signedToken the signed token to verify.
     * @return the verified token or null if invalid.
     */
    public Token verify(final String signedToken) {
        final JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();

        // Attempts to verify signed token.
        DecodedJWT decodedJWT;
        try {
            decodedJWT = jwtVerifier.verify(signedToken);
        } catch (final JWTVerificationException jwtVerificationException) {
            logger.warning("Failed to verify json web token: " + jwtVerificationException.getMessage());
            return null;
        }

        final Claim userClaim = decodedJWT.getClaim("user");
        final ObjectId userId = new ObjectId(userClaim.asString());

        return new Token(userId);
    }
}
