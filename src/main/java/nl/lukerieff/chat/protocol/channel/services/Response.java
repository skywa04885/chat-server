package nl.lukerieff.protocol.channel.services;

import com.google.protobuf.ByteString;
import nl.lukerieff.protocol.FrameOuterClass;

public class Response {
    public static class Builder {
        protected final int requestNumber;
        protected long timestamp;
        protected ByteString body;

        public Builder(final int requestNumber) {
            this.requestNumber = requestNumber;
            this.timestamp = System.currentTimeMillis();
        }

        public Builder setTimestamp(final long timestamp) {
            this.timestamp = timestamp;

            return this;
        }

        public Builder setBody(final ByteString body) {
            this.body = body;

            return this;
        }

        public Response build() {
            return new Response(requestNumber, timestamp, body);
        }
    }

    public static Builder newBuilder(final int requestNumber) {
        return new Builder(requestNumber);
    }

    protected final int requestNumber;
    protected final long timestamp;
    protected final ByteString body;

    public Response(final int requestNumber, final long timestamp, final ByteString body) {
        this.requestNumber = requestNumber;
        this.timestamp = timestamp;
        this.body = body;
    }

    public final int getRequestNumber() {
        return this.requestNumber;
    }

    public FrameOuterClass.Frame.Channeled.Response encode() {
        final FrameOuterClass.Frame.Channeled.Response.Builder builder = FrameOuterClass.Frame.Channeled.Response.newBuilder()
                .setReqNo(requestNumber)
                .setTimestamp(timestamp);

        if (body != null) {
            builder.setBody(body);
        }

        return builder.build();
    }

    public static Response decode(final FrameOuterClass.Frame.Channeled.Response responseMsg) {
        final Builder builder = newBuilder(responseMsg.getReqNo()).setTimestamp(responseMsg.getTimestamp());

        if (responseMsg.hasBody()) {
            builder.setBody(responseMsg.getBody());
        }

        return builder.build();
    }
}
