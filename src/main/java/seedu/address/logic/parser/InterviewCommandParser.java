package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
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

    public static final String MESSAGE_MISSING_INDEX = "Missing candidate index. Format: "
            + InterviewCommand.FORMAT;
    public static final String MESSAGE_MISSING_SPACE_BEFORE_PREFIX = "Put a space between the index and "
            + PREFIX_INTERVIEW + ", e.g. " + InterviewCommand.EXAMPLE;
    public static final String MESSAGE_TEXT_AFTER_INDEX = "Unexpected text \"%1$s\" after the index. "
            + "Put the date and time after " + PREFIX_INTERVIEW + ", e.g. " + InterviewCommand.EXAMPLE;

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

        Index index = parseCandidateIndex(argMultimap.getPreamble());

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_INTERVIEW);

        Optional<String> interviewValue = argMultimap.getValue(PREFIX_INTERVIEW);
        Interview interview = interviewValue.isPresent()
                ? ParserUtil.parseInterview(interviewValue.get())
                : Interview.NONE;
        return new InterviewCommand(index, interview);
    }

    /**
     * Parses the preamble, which must be exactly one candidate index.
     * Each kind of mistake gets its own message, so the user can tell what to correct.
     *
     * @throws ParseException if the index is missing or invalid, or other text comes before {@code d/}.
     */
    private static Index parseCandidateIndex(String preamble) throws ParseException {
        String trimmedPreamble = preamble.trim();
        if (trimmedPreamble.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }

        String[] words = trimmedPreamble.split("\\s+", 2);
        String indexWord = words[0];
        // The tokenizer only finds a prefix after a space, so "d/..." or "1d/..." can stay in the preamble.
        if (indexWord.startsWith(PREFIX_INTERVIEW.getPrefix())) {
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }
        if (indexWord.contains(PREFIX_INTERVIEW.getPrefix())) {
            throw new ParseException(MESSAGE_MISSING_SPACE_BEFORE_PREFIX);
        }

        Index index = ParserUtil.parseIndex(indexWord);
        boolean hasTextAfterIndex = words.length > 1;
        if (hasTextAfterIndex) {
            throw new ParseException(String.format(MESSAGE_TEXT_AFTER_INDEX, words[1]));
        }
        return index;
    }

}
