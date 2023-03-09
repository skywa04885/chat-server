package nl.lukerieff.protocol;

public class ProtocolError extends Error {
    public ProtocolError(final String error) {
        super(error);
    }
}
