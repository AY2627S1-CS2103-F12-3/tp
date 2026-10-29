package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailureWithReason;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.role.RoleStatus;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_returnsFindCommand() {
        assertParseSuccess(parser, "     ", new FindCommand(new NameContainsKeywordsPredicate(List.of())));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_roleAndStatusFilters_returnsFindCommand() throws Exception {
        FindCommand expected = new FindCommand(new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of("Software", "Engineer"), Set.of(RoleStatus.OPEN), List.of()));
        assertParseSuccess(parser, "Alice r/Software Engineer st/open", expected);
    }

    @Test
    public void parse_roleAndStatusWithoutName_returnsFindCommand() throws Exception {
        FindCommand expected = new FindCommand(new NameContainsKeywordsPredicate(
                List.of(), List.of("Software"), Set.of(RoleStatus.OPEN), List.of()));
        assertEquals(expected.toString(), parser.parse(" r/Software st/open").toString());
    }

    @Test
    public void parse_statusOnly_returnsFindCommand() throws Exception {
        FindCommand expected = new FindCommand(new NameContainsKeywordsPredicate(
                List.of(), List.of(), Set.of(RoleStatus.CLOSED), List.of()));
        assertEquals(expected.toString(), parser.parse(" st/closed").toString());
    }

    @Test
    public void parse_invalidStatus_throwsParseException() {
        assertParseFailureWithReason(parser, "Alice st/INVALID",
                "Invalid role status. Use one of: [OPEN, CLOSED].",
                "No enum constant seedu.address.model.role.RoleStatus.INVALID");
        assertParseFailure(parser, "Alice st/   ", "Invalid role status. Use one of: [OPEN, CLOSED].");
    }

    @Test
    public void parse_blankRoleFilter_ignoresBlankKeyword() throws Exception {
        FindCommand expected = new FindCommand(new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of(), Set.of(RoleStatus.OPEN), List.of()));
        assertEquals(expected.toString(), parser.parse("Alice r/   st/open").toString());
    }

}
