package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailureWithReason;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.CloseRoleCommand;

public class CloseRoleCommandParserTest {
    private final CloseRoleCommandParser parser = new CloseRoleCommandParser();

    @Test
    public void parse_validIndex_success() {
        assertParseSuccess(parser, " 1 ", new CloseRoleCommand(Index.fromOneBased(1)));
        assertParseSuccess(parser, "2147483647", new CloseRoleCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String input : new String[]{"", " ", "0", "-1", "one", "1.5", "2147483648"}) {
            assertParseFailureWithReason(parser, input, MESSAGE_INVALID_ROLE_DISPLAYED_INDEX,
                    ParserUtil.MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_extraText_failure() {
        for (String input : new String[]{"1 extra", "1 2", "1\tx/extra"}) {
            assertParseFailure(parser, input, CloseRoleCommand.MESSAGE_EXTRA_TEXT);
        }
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
