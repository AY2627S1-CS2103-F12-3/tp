package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddRoleCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses commands in the {@code role} command group.
 */
public class RoleCommandParser implements Parser<Command> {

    private static final Pattern SUBCOMMAND_FORMAT = Pattern.compile("(?<subcommand>\\S+)(?<arguments>.*)");

    @Override
    public Command parse(String args) throws ParseException {
        Matcher matcher = SUBCOMMAND_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddRoleCommand.MESSAGE_USAGE));
        }

        String subcommand = matcher.group("subcommand");
        String arguments = matcher.group("arguments");

        return switch (subcommand) {
            case AddRoleCommand.COMMAND_WORD -> new AddRoleCommandParser().parse(arguments);
            default -> throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        };
    }
}
