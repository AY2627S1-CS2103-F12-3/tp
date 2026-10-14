package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_1;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_1_STRING;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.StatusCommand;
import seedu.address.model.person.Status;

public class StatusCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, StatusCommand.MESSAGE_USAGE);

    private StatusCommandParser parser = new StatusCommandParser();

    @Test
    public void parse_validArgs_returnsStatusCommand() {
        assertParseSuccess(parser, "1 " + VALID_STATUS_1_STRING, new StatusCommand(INDEX_FIRST_PERSON, VALID_STATUS_1));
    }

    @Test
    public void parse_validArgsWithWhitespace_returnsStatusCommand() {
        // Surrounding and repeated whitespace is ignored.
        assertParseSuccess(parser, "  1   " + VALID_STATUS_1_STRING + "  ",
                new StatusCommand(INDEX_FIRST_PERSON, VALID_STATUS_1));
    }

    @Test
    public void parse_validArgsDifferentCase_returnsStatusCommand() {
        assertParseSuccess(parser, "1 " + VALID_STATUS_1_STRING.toLowerCase(),
                new StatusCommand(INDEX_FIRST_PERSON, VALID_STATUS_1));
    }

    @Test
    public void parse_missingIndexOrStatus_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "a " + VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1" + VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0 " + VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "-1 " + VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "2147483648 " + VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidStatus_failure() {
        String expectedMessage = Status.getConstraintsMessage();
        assertParseFailure(parser, "1 accepted", expectedMessage);
        assertParseFailure(parser, "1 " + VALID_STATUS_1_STRING + "!", expectedMessage);
        assertParseFailure(parser, "1 pending resume screening", expectedMessage);
        assertParseFailure(parser, "1 " + VALID_STATUS_1_STRING + " extra", expectedMessage);
    }
}
