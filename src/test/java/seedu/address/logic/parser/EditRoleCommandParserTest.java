package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailureWithReason;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditRoleCommand;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.RoleTitle;

public class EditRoleCommandParserTest {
    private static final int MAX_EXPERIENCE_LENGTH = 50;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditRoleCommand.MESSAGE_USAGE);

    private final EditRoleCommandParser parser = new EditRoleCommandParser();

    private static String expectedIndexReason(String index) {
        return index.equals("2147483648")
                ? String.format(ParserUtil.MESSAGE_INDEX_TOO_LARGE, index)
                : ParserUtil.MESSAGE_INVALID_INDEX;
    }

    @Test
    public void parse_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validExperience_success() {
        for (String experience : new String[] {"Senior", "2 years", "Entry level", "A",
            "A".repeat(MAX_EXPERIENCE_LENGTH), "😀".repeat(MAX_EXPERIENCE_LENGTH)}) {
            assertParseSuccess(parser, " 1 x/  " + experience + "  ",
                    new EditRoleCommand(Index.fromOneBased(1), new ExperienceLevel(experience)));
        }
        assertParseSuccess(parser, " 2 x/Junior", new EditRoleCommand(Index.fromOneBased(2),
                new ExperienceLevel("Junior")));
    }

    @Test
    public void parse_noFields_failure() {
        assertParseFailure(parser, " 1", EditRoleCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_repeatedExperience_failure() {
        assertParseFailure(parser, " 1 x/Senior x/Senior",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_EXPERIENCE_LEVEL));
    }

    @Test
    public void parse_invalidExperience_failure() {
        for (String experience : new String[] {"", "   ", "A".repeat(MAX_EXPERIENCE_LENGTH + 1),
            "Senior\nLevel", "Senior\tLevel", "Senior\rLevel", "Senior\u200BLevel"}) {
            assertParseFailure(parser, " 1 x/" + experience, ExperienceLevel.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String index : new String[] {"", "0", "-1", "abc", "1.5", "2147483648"}) {
            assertParseFailureWithReason(parser, " " + index + " x/Senior", MESSAGE_INVALID_ROLE_DISPLAYED_INDEX,
                    expectedIndexReason(index));
        }
    }

    @Test
    public void parse_extraPreamble_failure() {
        assertParseFailure(parser, " 1 extra x/Senior", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, " 1 2 x/Senior", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_validTitle_success() {
        for (String title : new String[] {"A", "A".repeat(MAX_TITLE_LENGTH), "R&D / Engineer's - 2.0", "工程师"}) {
            assertParseSuccess(parser, " 1 r/  " + title + "  ",
                    new EditRoleCommand(Index.fromOneBased(1), new RoleTitle(title), null));
        }
    }

    @Test
    public void parse_combinedEdits_acceptsEitherFieldOrder() {
        EditRoleCommand expected = new EditRoleCommand(Index.fromOneBased(1), new RoleTitle("New title"),
                new ExperienceLevel("Senior"));
        assertParseSuccess(parser, " 1 r/New title x/Senior", expected);
        assertParseSuccess(parser, " 1 x/Senior r/New title", expected);
    }

    @Test
    public void parse_invalidTitle_failure() {
        for (String title : new String[] {"", "  ", "123", "Bad@Title", "A".repeat(MAX_TITLE_LENGTH + 1),
            "Software\nEngineer", "Software\tEngineer"}) {
            assertParseFailure(parser, " 1 r/" + title, RoleTitle.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, " 1 x/Senior r/", RoleTitle.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 r/Valid title x/", ExperienceLevel.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedTitleOrBothFields_failure() {
        assertParseFailure(parser, " 1 r/New title r/New title",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ROLE_TITLE));
        assertParseFailure(parser, " 1 r/A r/B x/Junior x/Senior",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ROLE_TITLE,
                        CliSyntax.PREFIX_EXPERIENCE_LEVEL));
    }
}
