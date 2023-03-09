package nl.lukerieff.cli.commands;

import picocli.CommandLine;

@CommandLine.Command(
        name = "hannah_and_luke",
        subcommands = {
                ServerCommand.class,
                UserCommand.class,
        }
)
public class RootCommand {
}
