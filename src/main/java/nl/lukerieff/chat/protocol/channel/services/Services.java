package nl.lukerieff.protocol.channel.services;

import nl.lukerieff.protocol.channel.services.call.IncommingCall;

import java.util.HashMap;
import java.util.Map;

public class Services {
    protected final Map<Integer, Service> services = new HashMap<>();

    public void add(final Service service) {
        if (this.services.containsKey(service.getIdentifier())) {
            // TODO: Handle already existing service.
            return;
        }

        services.put(service.getIdentifier(), service);
    }

    public void remove(final Service service) {
        if (!this.services.containsKey(service.getIdentifier())) {
            // TODO: Handle non existing service.
            return;
        }

        services.remove(service.getIdentifier());
    }

    public void call(final IncommingCall call) {
        final Service service = this.services.get(call.getRequest().getService());

        if (service == null) {
            // TODO: Handle non existing service.
            return;
        }

        service.called(call);
    }
}
