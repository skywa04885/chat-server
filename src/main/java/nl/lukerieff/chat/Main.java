package nl.lukerieff;

import com.auth0.jwt.algorithms.Algorithm;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import nl.lukerieff.cli.commands.RootCommand;
import nl.lukerieff.security.TokenSigner;
import picocli.CommandLine;

public class Main {
    private final static String DATABASE = "hannah_and_luke_v2";
    public static Datastore datastore;
    public static TokenSigner tokenSigner;

    private static void morphiaSetup() {
        datastore = Morphia.createDatastore(MongoClients.create(), DATABASE);
        datastore.getMapper().mapPackage("nl.lukerieff");
        datastore.ensureIndexes();
    }

    private static void tokenSignerSetup() {
        final Algorithm algorithm = Algorithm.HMAC256("test");
        final String issuer = "server";
        tokenSigner = new TokenSigner(algorithm, issuer);
    }

    public static void main(String[] args) {
        morphiaSetup();
        tokenSignerSetup();

        // Runs the CLI tool.
        final CommandLine commandLine = new CommandLine(new RootCommand());
        commandLine.execute(args);
    }
}