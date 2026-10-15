package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteRoleCommand;

public class DeleteRoleCommandParserTest {
    private final DeleteRoleCommandParser parser = new DeleteRoleCommandParser();

    @Test
    public void parse_validIndex_success() {
        assertParseSuccess(parser, " 2 ", new DeleteRoleCommand(Index.fromOneBased(2)));
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "0", MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
    }

    @Test
    public void parse_extraText_failure() {
        assertParseFailure(parser, "1 extra", DeleteRoleCommand.MESSAGE_EXTRA_TEXT);
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
