package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given arguments in the context of the ListCommand.
     *
     * <p>The list command does not require any arguments.</p>
     *
     * @return a ListCommand object for execution.
     */
    @Override
    public ListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (!trimmedArgs.isEmpty() && !trimmedArgs.equals("role")) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListCommand.MESSAGE_USAGE));
        }

        return new ListCommand(trimmedArgs.equals("role"));
    }
}
