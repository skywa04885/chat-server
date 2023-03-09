package nl.lukerieff.chat.services;

import com.google.protobuf.InvalidProtocolBufferException;
import nl.lukerieff.chat.ServiceIdentifier;
import nl.lukerieff.chat.Session;
import nl.lukerieff.chat.protocol.channel.Channel;
import nl.lukerieff.chat.protocol.channel.services.call.IncommingCall;
import nl.lukerieff.chat.protocol.channel.services.Service;

public class UserServices {
  public static final Me ME = new Me();
  public static final Search SEARCH = new Search();

  /**
   * The service that's responsible for sending the user his/her own document.
   */
  protected static class Me extends Service {
    public Me() {
      super(ServiceIdentifier.USERS_ME.getIdentifier());
    }

    @Override
    public void called(final IncommingCall call) {
      final Channel channel = call.getChannel();
      final Session session = (Session) channel.getUserData();

      UsersServiceMessages.MeRequest meRequest;

      try {
        meRequest = UsersServiceMessages.MeRequest.parseFrom(call.getRequest().getBody());
      } catch (InvalidProtocolBufferException e) {

      }

      final UsersServiceMessages.MeResponse meResponse = UsersServiceMessages.MeResponse.newBuilder().build();
      call.respond(meResponse.toByteString());
    }
  }

  /**
   * The search service is responsible for searching through users (for adding new chats for example).
   */
  protected static class Search extends Service {
    public Search() {
      super(ServiceIdentifier.USERS_SEARCH.getIdentifier());
    }

    @Override
    public void called(final IncommingCall call) {
      try {
        // Parses the request.
        final UsersServiceMessages.SearchRequest searchRequest = UsersServiceMessages.SearchRequest
                .parseFrom(call.getRequest().getBody());

        // Sends the response.
        final UsersServiceMessages.SearchResponse searchResponse = UsersServiceMessages.SearchResponse.newBuilder().build();
        call.respond(searchResponse.toByteString());
      } catch (InvalidProtocolBufferException e) {

      }
    }
  }
}
