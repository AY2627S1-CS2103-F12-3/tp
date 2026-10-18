package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTERVIEW;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.InterviewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Interview;

/**
 * Parses input arguments and creates a new InterviewCommand object
 */
public class InterviewCommandParser implements Parser<InterviewCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the InterviewCommand
     * and returns an InterviewCommand object for execution.
     * Leaving out {@code d/} clears the interview, but an empty {@code d/} is rejected,
     * so that a mistyped date does not clear an interview by accident.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public InterviewCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_INTERVIEW);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, InterviewCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_INTERVIEW);

        Optional<String> interviewValue = argMultimap.getValue(PREFIX_INTERVIEW);
        Interview interview = interviewValue.isPresent()
                ? ParserUtil.parseInterview(interviewValue.get())
                : Interview.NONE;
        return new InterviewCommand(index, interview);
    }

}
