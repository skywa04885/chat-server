package nl.lukerieff.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.bson.types.ObjectId;

public class Token {
    protected final ObjectId userId;

    public Token(final ObjectId userId) {
        this.userId = userId;
    }

    public final ObjectId getUserId() {
        return userId;
    }
}
