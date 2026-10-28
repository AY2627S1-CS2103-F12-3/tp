package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.HELP_TOPICS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class HelpCommandTest {
    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_help_success() {
        CommandResult expectedCommandResult = new CommandResult(CommandGuide.getOverview(), true, false);
        assertCommandSuccess(new HelpCommand(), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HelpCommand().execute(null));
    }

    @Test
    public void isUndoable_help_returnsFalse() {
        assertFalse(new HelpCommand().isUndoable());
    }

    @Test
    public void constructor_nullCommandName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HelpCommand(null));
    }

    @Test
    public void execute_supportedTopic_showsOnlyRequestedInstructions() throws Exception {
        for (String topic : HELP_TOPICS) {
            HelpCommand command = new HelpCommand(topic);
            CommandResult result = command.execute(model);
            assertEquals(CommandGuide.findHelp(topic).orElseThrow(), result.getFeedbackToUser(), topic);
            assertTrue(result.getFeedbackToUser().startsWith(topic + ":"), topic);
            assertTrue(result.isShowHelp());
            assertFalse(result.isExit());
            assertFalse(command.isUndoable());
            assertEquals(expectedModel, model);
        }
    }

    @Test
    public void execute_unknownTopic_reportsOriginalInputAndPreservesModel() {
        assertThrows(CommandException.class,
                "No help is available for “NotACommand”. Use help to view all commands.", () ->
                        new HelpCommand("NotACommand").execute(model));
        assertEquals(expectedModel, model);
    }
}
