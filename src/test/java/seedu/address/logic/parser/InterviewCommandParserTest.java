package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_INTERVIEW;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailureWithReason;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.InterviewCommandParser.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.parser.InterviewCommandParser.MESSAGE_MISSING_SPACE_BEFORE_PREFIX;
import static seedu.address.logic.parser.InterviewCommandParser.MESSAGE_TEXT_AFTER_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INDEX_TOO_LARGE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.InterviewCommand;
import seedu.address.model.person.Interview;

public class InterviewCommandParserTest {

    private InterviewCommandParser parser = new InterviewCommandParser();

    @Test
    public void parse_indexAndInterview_success() {
        InterviewCommand expectedCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview("2026-10-20 14:00"));
        assertParseSuccess(parser, "1 d/2026-10-20 14:00", expectedCommand);

        // surrounding whitespace trimmed
        assertParseSuccess(parser, "  1   d/  2026-10-20 14:00  ", expectedCommand);
    }

    @Test
    public void parse_indexOnly_clearsInterview() {
        InterviewCommand expectedCommand = new InterviewCommand(INDEX_FIRST_PERSON, Interview.NONE);
        assertParseSuccess(parser, "1", expectedCommand);
        assertParseSuccess(parser, " 1   ", expectedCommand);
    }

    @Test
    public void parse_emptyInterview_failure() {
        assertParseFailure(parser, "1 d/", Interview.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 d/   ", Interview.MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_repeatedPrefix_failure() {
        assertParseFailure(parser, "1 d/2026-10-20 14:00 d/2026-10-21 14:00",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INTERVIEW));
    }

    @Test
    public void parse_invalidInterviewFormat_failure() {
        // single-digit hour
        assertParseFailure(parser, "1 d/2026-10-20 9:00", Interview.MESSAGE_INVALID_FORMAT);

        // wrong separators
        assertParseFailure(parser, "1 d/20/10/2026 14:00", Interview.MESSAGE_INVALID_FORMAT);

        // no time
        assertParseFailure(parser, "1 d/2026-10-20", Interview.MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_impossibleInterviewDateTime_failure() {
        // no such day
        assertParseFailure(parser, "1 d/2026-02-30 14:00", Interview.MESSAGE_INVALID_DATE_TIME);

        // not a leap year
        assertParseFailure(parser, "1 d/2026-02-29 14:00", Interview.MESSAGE_INVALID_DATE_TIME);

        // hour too large
        assertParseFailure(parser, "1 d/2026-10-20 24:00", Interview.MESSAGE_INVALID_DATE_TIME);
    }

    @Test
    public void parse_missingIndex_failure() {
        assertParseFailureWithReason(parser, "", MESSAGE_INVALID_FORMAT, ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailureWithReason(parser, "d/2026-10-20 14:00", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidIndex_failure() {
        // non-numeric index
        assertParseFailureWithReason(parser, "a d/2026-10-20 14:00", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);

        // zero index
        assertParseFailureWithReason(parser, "0 d/2026-10-20 14:00", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);

        // negative index
        assertParseFailureWithReason(parser, "-1 d/2026-10-20 14:00", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);

        // prefix joined to index without a space
        assertParseFailureWithReason(parser, "1d/2026-10-20 14:00", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_extraTextAroundIndex_failure() {
        assertParseFailureWithReason(parser, "1 2 d/2026-10-20 14:00", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailureWithReason(parser, "1 tomorrow", MESSAGE_INVALID_FORMAT,
                ParserUtil.MESSAGE_INVALID_INDEX);
    }
}
