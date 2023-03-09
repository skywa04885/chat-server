package nl.lukerieff.cli.commands.user;

import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filters;
import nl.lukerieff.Main;
import nl.lukerieff.models.User;
import nl.lukerieff.security.Token;
import picocli.CommandLine;

@CommandLine.Command(
        name = "token",
        description = "Get the authentication token for the given user"
)
public class TokenCommand implements Runnable {
    @CommandLine.Option(
            names = {"-u", "--username"},
            description = "The username of the user",
            paramLabel = "<username>"
    )
    public String username;
    @CommandLine.Option(
            names = {"-e", "--email"},
            description = "The email of the user",
            paramLabel = "<email>"
    )
    public String email;

    @Override
    public void run() {
        // Constructs the base query.
        Query<User> query = Main.datastore.find(User.class);

        // Adds the username or email filter to the query.
        if (username != null) {
            query = query.filter(Filters.eq("username", username));
        } else if (email != null) {
            query = query.filter(Filters.eq("email", email));
        } else {
            System.out.println("Please specify either an username or email.");
            return;
        }

        // Gets the first user that matches the filters.
        final User user = query.first();
        if (user == null) {
            System.out.println("User could not be found.");
            return;
        }

        // Builds the token with the user id.
        final Token token = new Token(user.getId());

        // Signs the token and shows it to the administrator.
        final String signedToken = Main.tokenSigner.sign(token);
        System.out.println("Token: " + signedToken);
    }
}
