package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteStatusCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new DeleteStatusCommand object
 * Adapted from:
 */
public class DeleteStatusCommandParser implements Parser<DeleteStatusCommand> {
    private static final Pattern COMMAND_PATTERN = Pattern.compile("(?<targetIndex>\\d+)\\s+(?<targetStatus>.+)");

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteStatusCommand
     * and returns a DeleteStatusCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteStatusCommand parse(String args) throws ParseException {
        requireNonNull(args);

        Matcher matcher = COMMAND_PATTERN.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStatusCommand.MESSAGE_USAGE));
        }

        Index index;
        try {
            index = ParserUtil.parseIndex(matcher.group("targetIndex"));
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteStatusCommand.MESSAGE_USAGE), pe);
        }

        Tag status = ParserUtil.parseStatus(matcher.group("targetStatus"));
        return new DeleteStatusCommand(index, status);
    }
}
