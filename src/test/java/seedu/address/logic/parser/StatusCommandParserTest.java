package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.STATUS_ACCEPTED;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.StatusCommand;
import seedu.address.model.tag.Tag;

public class StatusCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, StatusCommand.MESSAGE_USAGE);

    private StatusCommandParser parser = new StatusCommandParser();

    @Test
    public void parse_validArgs_returnsStatusCommand() {
        assertParseSuccess(parser, "1 accepted", new StatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED));

        // Surrounding and repeated whitespace is ignored.
        assertParseSuccess(parser, "  1   accepted  ", new StatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED));
    }

    @Test
    public void parse_missingIndexOrStatus_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "accepted", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "a accepted", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1accepted", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0 accepted", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "-1 accepted", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidStatus_failure() {
        assertParseFailure(parser, "1 accepted!", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 accepted status", Tag.MESSAGE_CONSTRAINTS);
    }
}
