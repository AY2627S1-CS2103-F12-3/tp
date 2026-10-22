package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_1;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_1_STRING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.StatusCommand;
import seedu.address.model.person.Status;

public class StatusCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, StatusCommand.MESSAGE_USAGE);

    private StatusCommandParser parser = new StatusCommandParser();

    @Test
    public void parse_validArgs_returnsStatusCommand() {
        for (Status status : Status.values()) {
            assertParseSuccess(parser, "1 st/" + status.name(),
                    new StatusCommand(Set.of(INDEX_FIRST_PERSON), status));
        }
    }

    @Test
    public void parse_validArgsWithWhitespace_returnsStatusCommand() {
        // Surrounding and repeated whitespace is ignored.
        assertParseSuccess(parser, "  1   st/  " + VALID_STATUS_1_STRING + "  ",
                new StatusCommand(Set.of(INDEX_FIRST_PERSON), VALID_STATUS_1));
    }

    @Test
    public void parse_validArgsDifferentCase_returnsStatusCommand() {
        assertParseSuccess(parser, "1 st/" + VALID_STATUS_1_STRING.toLowerCase(),
                new StatusCommand(Set.of(INDEX_FIRST_PERSON), VALID_STATUS_1));
    }

    @Test
    public void parse_batchIndices_returnsStatusCommand() {
        assertParseSuccess(parser, "3 1 st/REJECTED",
                new StatusCommand(Set.of(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON), Status.REJECTED));
    }

    @Test
    public void parse_batchWithWhitespace_returnsStatusCommand() {
        assertParseSuccess(parser, " \t3  1\t2   st/ ReJeCtEd \t",
                new StatusCommand(Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, INDEX_THIRD_PERSON),
                        Status.REJECTED));
    }

    @Test
    public void parse_repeatedIndices_deduplicatesByValue() {
        assertParseSuccess(parser, "2 1 2 1 st/HIRED",
                new StatusCommand(Set.of(Index.fromOneBased(1), Index.fromOneBased(2)), Status.HIRED));
    }

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_missingIndexOrStatus_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1   ", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " st/HIRED", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 2 HIRED", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String invalidIndex : new String[]{"a", "0", "-1", "1.5", "2147483648", "1,2"}) {
            assertParseFailure(parser, invalidIndex + " st/" + VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
            assertParseFailure(parser, "1 " + invalidIndex + " 2 st/" + VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
        }
        assertParseFailure(parser, "1 2 a st/" + VALID_STATUS_1_STRING, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidStatus_failure() {
        String expectedMessage = Status.getConstraintsMessage();
        assertParseFailure(parser, "1 st/accepted", expectedMessage);
        assertParseFailure(parser, "1 st/" + VALID_STATUS_1_STRING + "!", expectedMessage);
        assertParseFailure(parser, "1 st/pending resume screening", expectedMessage);
        assertParseFailure(parser, "1 st/" + VALID_STATUS_1_STRING + " extra", expectedMessage);
        assertParseFailure(parser, "1 2 st/accepted", expectedMessage);
    }

    @Test
    public void parse_emptyStatus_failure() {
        assertParseFailure(parser, "1 st/", Status.getConstraintsMessage());
        assertParseFailure(parser, "1 2 st/   ", Status.getConstraintsMessage());
    }

    @Test
    public void parse_repeatedStatusPrefix_failure() {
        String expectedMessage = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_STATUS);
        assertParseFailure(parser, "1 2 st/HIRED st/REJECTED", expectedMessage);
        assertParseFailure(parser, "1 2 st/HIRED st/HIRED", expectedMessage);
    }

    @Test
    public void parse_usageExample_returnsStatusCommand() {
        String example = StatusCommand.MESSAGE_USAGE.split("Example: ")[1];
        assertParseSuccess(parser, example.substring(StatusCommand.COMMAND_WORD.length()),
                new StatusCommand(Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, INDEX_THIRD_PERSON),
                        Status.values()[0]));
    }
}
