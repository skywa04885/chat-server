package nl.lukerieff.cli.commands;

import nl.lukerieff.cli.commands.user.AddCommand;
import nl.lukerieff.cli.commands.user.TokenCommand;
import picocli.CommandLine;

@CommandLine.Command(
        name = "user",
        subcommands = {
                AddCommand.class,
                TokenCommand.class
        }
)
public class UserCommand { }
