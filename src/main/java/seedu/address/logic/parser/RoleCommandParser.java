package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddRoleCommand;
import seedu.address.logic.commands.CloseRoleCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteRoleCommand;
import seedu.address.logic.commands.EditRoleCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses commands in the {@code role} command group.
 */
public class RoleCommandParser implements Parser<Command> {

    public static final String COMMAND_WORD = "role";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Manages role openings.\n"
            + "Available commands:\n"
            + AddRoleCommand.MESSAGE_USAGE + "\n"
            + EditRoleCommand.MESSAGE_USAGE + "\n"
            + CloseRoleCommand.MESSAGE_USAGE + "\n"
            + DeleteRoleCommand.MESSAGE_USAGE;

    private static final Pattern SUBCOMMAND_FORMAT = Pattern.compile("(?<subcommand>\\S+)(?<arguments>.*)");

    @Override
    public Command parse(String args) throws ParseException {
        Matcher matcher = SUBCOMMAND_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        String subcommand = matcher.group("subcommand");
        String arguments = matcher.group("arguments");

        return switch (subcommand) {
            case AddRoleCommand.COMMAND_WORD -> new AddRoleCommandParser().parse(arguments);
            case EditRoleCommand.COMMAND_WORD -> new EditRoleCommandParser().parse(arguments);
            case CloseRoleCommand.COMMAND_WORD -> new CloseRoleCommandParser().parse(arguments);
            case DeleteRoleCommand.COMMAND_WORD -> new DeleteRoleCommandParser().parse(arguments);
            default -> throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        };
    }
}
