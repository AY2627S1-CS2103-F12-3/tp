package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailureWithReason;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;

/**
 * Tests candidate indices and unexpected arguments for the delete command.
 */
public class DeleteCommandParserTest {

    private static final String FORMAT_ERROR =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        for (String input : List.of("1", " 1 ", "\t1\r\n", "0001")) {
            assertParseSuccess(parser, input, new DeleteCommand(INDEX_FIRST_PERSON));
        }
    }

    @Test
    public void parse_invalidIndex_throwsIndexParseException() {
        for (String input : List.of("", " ", "a", "0", "-1", "+1", "1.5", "1extra", "a extra", "0 extra")) {
            assertParseFailureWithReason(parser, input, FORMAT_ERROR, ParserUtil.MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_oversizedIndex_throwsSizeParseException() {
        for (String input : List.of("2147483648", "2147483648 extra")) {
            assertParseFailureWithReason(parser, input, FORMAT_ERROR,
                    String.format(ParserUtil.MESSAGE_INDEX_TOO_LARGE, "2147483648"));
        }
    }

    @Test
    public void parse_validIndexWithExtraArguments_throwsExtraTextParseException() {
        for (String input : List.of("1 extra", "1 2", "1 n/Alice", " 1\textra ", "1\nextra", "1 r/QA",
                "2147483647 extra")) {
            assertParseFailure(parser, input, FORMAT_ERROR + "\nThe delete command takes only a candidate index.");
        }
    }

    @Test
    public void parse_largestSupportedIndex_returnsDeleteCommand() {
        assertParseSuccess(parser, "2147483647", new DeleteCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }
}
