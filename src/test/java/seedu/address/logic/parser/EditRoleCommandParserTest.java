package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditRoleCommand;
import seedu.address.model.role.ExperienceLevel;

public class EditRoleCommandParserTest {
    private static final int MAX_EXPERIENCE_LENGTH = 50;
    private static final String INVALID_FORMAT_MESSAGE =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditRoleCommand.MESSAGE_USAGE);

    private final EditRoleCommandParser parser = new EditRoleCommandParser();

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
    public void parse_missingExperience_failure() {
        assertParseFailure(parser, " 1", EditRoleCommandParser.MESSAGE_MISSING_FIELD);
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
            assertParseFailure(parser, " " + index + " x/Senior", MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
        }
    }

    @Test
    public void parse_extraPreamble_failure() {
        assertParseFailure(parser, " 1 extra x/Senior", INVALID_FORMAT_MESSAGE);
        assertParseFailure(parser, " 1 2 x/Senior", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void parse_titleEdit_failure() {
        for (String args : new String[] {" 1 r/New title", " 1 x/Senior r/New title",
            " 1 r/New title x/Senior", " 1 x/Senior r/"}) {
            assertParseFailure(parser, args, INVALID_FORMAT_MESSAGE);
        }
    }
}
