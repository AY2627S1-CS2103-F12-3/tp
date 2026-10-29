package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import seedu.address.model.ModelManager;

public class UndoCommandTest {
    @Test
    public void execute_withoutHistory_returnsEmptyMessage() throws Exception {
        CommandResult result = new UndoCommand().execute(new ModelManager());
        assertEquals(UndoCommand.MESSAGE_EMPTY, result.getFeedbackToUser());
    }

    @Test
    public void isUndoable_returnsFalse() {
        assertFalse(new UndoCommand().isUndoable());
    }
}
