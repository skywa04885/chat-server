package nl.lukerieff;

public enum ServiceIdentifier {
    USERS_ME(0),
    USERS_SEARCH(1);

    private final int identifier;

    ServiceIdentifier(final int identifier) {
        this.identifier = identifier;
    }

    public final int getIdentifier() {
        return this.identifier;
    }
}
