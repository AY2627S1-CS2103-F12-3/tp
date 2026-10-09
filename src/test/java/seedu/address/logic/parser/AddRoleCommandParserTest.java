package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.AddRoleCommandParser.MESSAGE_MISSING_FIELD;
import static seedu.address.logic.parser.AddRoleCommandParser.MESSAGE_REPEATED_FIELD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE_TITLE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddRoleCommand;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
import seedu.address.testutil.RoleBuilder;

public class AddRoleCommandParserTest {

    private static final String VALID_TITLE = "Software Engineer";
    private static final String VALID_EXPERIENCE_LEVEL = "2 years";
    private static final String TITLE_DESC = " " + PREFIX_ROLE_TITLE + VALID_TITLE;
    private static final String EXPERIENCE_LEVEL_DESC =
            " " + PREFIX_EXPERIENCE_LEVEL + VALID_EXPERIENCE_LEVEL;

    private final AddRoleCommandParser parser = new AddRoleCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Role expectedRole = new RoleBuilder()
                .withTitle(VALID_TITLE)
                .withExperienceLevel(VALID_EXPERIENCE_LEVEL)
                .build();

        assertParseSuccess(parser, TITLE_DESC + EXPERIENCE_LEVEL_DESC,
                new AddRoleCommand(expectedRole));
        assertParseSuccess(parser, EXPERIENCE_LEVEL_DESC + TITLE_DESC,
                new AddRoleCommand(expectedRole));
    }

    @Test
    public void parse_missingRequiredField_failure() {
        assertParseFailure(parser, EXPERIENCE_LEVEL_DESC,
                String.format(MESSAGE_MISSING_FIELD, PREFIX_ROLE_TITLE));
        assertParseFailure(parser, TITLE_DESC,
                String.format(MESSAGE_MISSING_FIELD, PREFIX_EXPERIENCE_LEVEL));
        assertParseFailure(parser, "",
                String.format(MESSAGE_MISSING_FIELD, PREFIX_ROLE_TITLE));
    }

    @Test
    public void parse_repeatedField_failure() {
        assertParseFailure(parser, TITLE_DESC + TITLE_DESC + EXPERIENCE_LEVEL_DESC,
                MESSAGE_REPEATED_FIELD);
        assertParseFailure(parser, TITLE_DESC + EXPERIENCE_LEVEL_DESC + EXPERIENCE_LEVEL_DESC,
                MESSAGE_REPEATED_FIELD);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddRoleCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "unexpected" + TITLE_DESC + EXPERIENCE_LEVEL_DESC, expectedMessage);
    }

    @Test
    public void parse_invalidRoleTitle_failure() {
        assertParseFailure(parser, " " + PREFIX_ROLE_TITLE + "1234" + EXPERIENCE_LEVEL_DESC,
                RoleTitle.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " " + PREFIX_ROLE_TITLE + "Engineer@" + EXPERIENCE_LEVEL_DESC,
                RoleTitle.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidExperienceLevel_failure() {
        assertParseFailure(parser, TITLE_DESC + " " + PREFIX_EXPERIENCE_LEVEL,
                ExperienceLevel.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, TITLE_DESC + " " + PREFIX_EXPERIENCE_LEVEL + "Senior\nLevel",
                ExperienceLevel.MESSAGE_CONSTRAINTS);
    }
}
