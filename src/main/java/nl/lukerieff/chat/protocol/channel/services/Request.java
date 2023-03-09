package nl.lukerieff.protocol.channel.services;

import com.google.protobuf.ByteString;
import nl.lukerieff.protocol.FrameOuterClass;

public class Request {
    public static class Builder {
        protected int requestNumber;
        protected long timestamp;
        protected final int service;
        protected ByteString body;

        public Builder(final int service) {
            this.requestNumber = 0;
            this.timestamp = System.currentTimeMillis();
            this.service = service;
        }

        public Builder setRequestNumber(final int requestNumber) {
            this.requestNumber = requestNumber;

            return this;
        }

        public Builder setTimestamp(final long timestamp) {
            this.timestamp = timestamp;

            return this;
        }

        public Builder setBody(final ByteString body) {
            this.body = body;

            return this;
        }

        public Request build() {
            return new Request(requestNumber, timestamp, service, body);
        }
    }

    public static Builder newBuilder(final int service) {
        return new Builder(service);
    }

    protected final int requestNumber;
    protected final long timestamp;
    protected final int service;
    protected final ByteString body;

    public Request(final int requestNumber, final long timestamp, final int service, final ByteString body) {
        this.requestNumber = requestNumber;
        this.timestamp = timestamp;
        this.service = service;
        this.body = body;
    }

    public final int getRequestNumber() {
        return requestNumber;
    }

    public final long getTimestamp() {
        return timestamp;
    }

    public final int getService() {
        return service;
    }

    public final ByteString getBody() {
        return body;
    }

    public static Request decode(final FrameOuterClass.Frame.Channeled.Request requestMsg) {
        Builder builder = newBuilder(requestMsg.getServiceNo()).setRequestNumber(requestMsg.getReqNo()).setTimestamp(requestMsg.getTimestamp());

        if (requestMsg.hasBody()) {
            builder.setBody(requestMsg.getBody());
        }

        return builder.build();
    }
}
