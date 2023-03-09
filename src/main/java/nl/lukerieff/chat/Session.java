package nl.lukerieff;

import nl.lukerieff.models.User;

public class Session {
    private final User user;

    public Session(final User user) {
        this.user = user;
    }

    public final User getUser() {
        return user;
    }
}
