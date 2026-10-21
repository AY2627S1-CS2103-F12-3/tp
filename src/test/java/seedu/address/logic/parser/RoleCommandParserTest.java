package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddRoleCommand;
import seedu.address.logic.commands.CloseRoleCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteRoleCommand;
import seedu.address.logic.commands.EditRoleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.role.ExperienceLevel;

public class RoleCommandParserTest {

    private final RoleCommandParser parser = new RoleCommandParser();

    @Test
    public void parse_editRoleCommand_success() throws Exception {
        assertEquals(new EditRoleCommand(Index.fromOneBased(1), new ExperienceLevel("Senior")),
                parser.parse("edit 1 x/Senior"));
    }

    @Test
    public void parse_deleteRoleCommand_success() throws Exception {
        assertEquals(new DeleteRoleCommand(Index.fromOneBased(1)), parser.parse("delete 1"));
    }

    @Test
    public void parse_closeRoleCommand_success() throws Exception {
        assertEquals(new CloseRoleCommand(Index.fromOneBased(1)), parser.parse("close 1"));
    }

    @Test
    public void parse_addRoleCommand_success() throws Exception {
        Command command = parser.parse("add r/Software Engineer x/2 years");
        assertEquals(new AddRoleCommand(SOFTWARE_ENGINEER), command);
    }

    @Test
    public void parse_missingSubcommand_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RoleCommandParser.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse(""));
    }

    @Test
    public void parse_unknownSubcommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parse("unknown"));
    }
}
