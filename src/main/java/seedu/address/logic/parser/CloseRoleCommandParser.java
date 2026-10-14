package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.CloseRoleCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the displayed role index for a {@code CloseRoleCommand}.
 */
public class CloseRoleCommandParser implements Parser<CloseRoleCommand> {

    @Override
    public CloseRoleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.strip();
        if (trimmedArgs.split("\\s+").length > 1) {
            throw new ParseException(CloseRoleCommand.MESSAGE_EXTRA_TEXT);
        }
        try {
            Index index = ParserUtil.parseIndex(trimmedArgs);
            return new CloseRoleCommand(index);
        } catch (ParseException exception) {
            throw new ParseException(MESSAGE_INVALID_ROLE_DISPLAYED_INDEX, exception);
        }
    }
}
