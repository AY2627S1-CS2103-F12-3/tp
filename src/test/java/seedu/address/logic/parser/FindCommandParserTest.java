package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Status;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_allCandidateStatuses_acceptsMixedCaseAndRepeatedPrefixes() {
        for (Status status : Status.values()) {
            assertParseSuccess(parser, " st/" + status.name().toLowerCase(java.util.Locale.ROOT),
                    new FindCommand(new NameContainsKeywordsPredicate(List.of(), List.of(), Set.of(status))));
        }
        assertParseSuccess(parser, " st/interview HIRED st/resume",
                new FindCommand(new NameContainsKeywordsPredicate(List.of(), List.of(),
                        Set.of(Status.INTERVIEW, Status.HIRED, Status.RESUME))));
    }

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
                List.of("Alice"), List.of("Software", "Engineer"), Set.of(Status.INTERVIEW)));
        assertParseSuccess(parser, "Alice r/Software Engineer st/interview", expected);
    }

    @Test
    public void parse_roleAndStatusWithoutName_returnsFindCommand() throws Exception {
        FindCommand expected = new FindCommand(new NameContainsKeywordsPredicate(
                List.of(), List.of("Software"), Set.of(Status.INTERVIEW)));
        assertEquals(expected, parser.parse(" r/Software st/interview"));
    }

    @Test
    public void parse_statusOnly_returnsFindCommand() throws Exception {
        FindCommand expected = new FindCommand(new NameContainsKeywordsPredicate(
                List.of(), List.of(), Set.of(Status.HIRED)));
        assertEquals(expected, parser.parse(" st/hired"));
    }

    @Test
    public void parse_invalidStatus_throwsParseException() {
        for (String input : List.of("Alice st/INVALID", " st/OPEN", " st/CLOSED", " st/", " st/hired INVALID")) {
            assertParseFailure(parser, input, Status.getConstraintsMessage());
        }
    }

    @Test
    public void parse_blankRoleFilter_ignoresBlankKeyword() throws Exception {
        FindCommand expected = new FindCommand(new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of(), Set.of(Status.INTERVIEW)));
        assertEquals(expected, parser.parse("Alice r/   st/interview"));
    }

}
