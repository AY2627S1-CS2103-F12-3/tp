package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an optional help topic without executing the command being described.
 */
public class HelpCommandParser implements Parser<HelpCommand> {
    /**
     * Parses zero or one command name for help lookup.
     *
     * @throws ParseException if more than one command name is supplied.
     */
    @Override
    public HelpCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.strip();
        if (trimmedArgs.isEmpty()) {
            return new HelpCommand();
        }

        String[] commandNames = trimmedArgs.split("\\s+");
        if (commandNames.length > 1) {
            throw new ParseException(HelpCommand.MESSAGE_TOO_MANY_ARGUMENTS);
        }
        return new HelpCommand(commandNames[0]);
    }
}
