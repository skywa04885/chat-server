package nl.lukerieff.cli.commands;

import nl.lukerieff.cli.commands.server.ListenCommand;
import picocli.CommandLine;

@CommandLine.Command(
        name = "server",
        subcommands = {
                ListenCommand.class
        }
)
public class ServerCommand implements Runnable {
    @Override
    public void run() {

    }
}
