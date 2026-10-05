package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_indexAndRemarkSpecified_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_REMARK + VALID_REMARK_AMY;

        assertParseSuccess(parser, userInput,
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY)));
    }

    @Test
    public void parse_emptyRemark_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_REMARK;

        assertParseSuccess(parser, userInput, new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
    }

    @Test
    public void parse_remarkPrefixNotSpecified_success() {
        String userInput = String.valueOf(INDEX_FIRST_PERSON.getOneBased());

        assertParseSuccess(parser, userInput, new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
    }

    @Test
    public void parse_invalidIndex_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0 " + PREFIX_REMARK + VALID_REMARK_AMY, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "abc " + PREFIX_REMARK + VALID_REMARK_AMY, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_duplicateRemarkPrefix_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_REMARK + VALID_REMARK_AMY
                + " " + PREFIX_REMARK + "Another remark";

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }
}
