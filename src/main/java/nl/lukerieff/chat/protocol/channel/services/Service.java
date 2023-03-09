package nl.lukerieff.protocol.channel.services;

import nl.lukerieff.protocol.channel.services.call.IncommingCall;

public abstract class Service {
    protected final int identifier;

    public Service(final int identifier) {
        this.identifier = identifier;
    }

    public final int getIdentifier() {
        return this.identifier;
    }

    public abstract void called(final IncommingCall call);
}
