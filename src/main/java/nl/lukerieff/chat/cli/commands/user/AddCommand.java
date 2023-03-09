package nl.lukerieff.cli.commands.user;

import nl.lukerieff.Main;
import nl.lukerieff.models.User;
import org.bson.types.ObjectId;
import picocli.CommandLine;

import java.util.ArrayList;

@CommandLine.Command(
        name = "add",
        description = "Add a new user"
)
public class AddCommand implements Runnable {
    @CommandLine.Option(
            names = {"-u", "--username"},
            required = true,
            description = "The username of the new user",
            paramLabel = "<username>"
    )
    public String username;
    @CommandLine.Option(
            names = {"-e", "--email"},
            required = true,
            description = "The email of the new user",
            paramLabel = "<email>"
    )
    public String email;
    @CommandLine.Option(
            names = {"-f", "--full-name"},
            required = true,
            description = "The full name of the new user",
            paramLabel = "<full-name>"
    )
    public String fullName;
    @CommandLine.Option(
            names = {"-p", "--phone-number"},
            description = "The phone number of the new user",
            paramLabel = "<phone-number>"
    )
    public String phone;

    @Override
    public void run() {
        final User user = new User(
                new ObjectId(),
                username,
                email,
                fullName,
                phone,
                new User.Profile(
                        new User.Profile.Statuses(
                                new User.Profile.Statuses.Status(
                                        "Hi! I'm using Hannah & Luke messenger.",
                                        System.currentTimeMillis()
                                ),
                                new ArrayList<>()
                        ),
                        new User.Profile.Avatars(
                                null,
                                new ArrayList<>()
                        )
                ),
                new User.Meta(
                        System.currentTimeMillis(),
                        null
                )
        );

        Main.datastore.insert(user);
    }
}
