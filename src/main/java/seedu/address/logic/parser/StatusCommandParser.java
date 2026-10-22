package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.StatusCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Status;

/**
 * Parses input arguments and creates a new StatusCommand object.
 */
public class StatusCommandParser implements Parser<StatusCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the StatusCommand
     * and returns a StatusCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public StatusCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_STATUS);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_STATUS);

        if (argMultimap.getValue(PREFIX_STATUS).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, StatusCommand.MESSAGE_USAGE));
        }

        Set<Index> indices;
        indices = new HashSet<>();

        String[] stringIndices = argMultimap.getPreamble().split("\\s+");
        for (String s : stringIndices) {
            try {
                Index index = ParserUtil.parseIndex(s);
                indices.add(index);
            } catch (ParseException e) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, StatusCommand.MESSAGE_USAGE), e);
            }
        }

        Status status = ParserUtil.parseStatus(argMultimap.getValue(PREFIX_STATUS).get());

        assert !indices.isEmpty() : "Successful parsing must produce at least one candidate index";
        assert status != null : "Successful parsing must produce a non-null status";
        return new StatusCommand(indices, status);
    }
}
