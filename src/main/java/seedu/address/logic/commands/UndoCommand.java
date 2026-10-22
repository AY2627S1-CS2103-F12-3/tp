package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/** Reverts the most recent address-book change. */
public class UndoCommand extends Command {
    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Undid the previous change.";
    public static final String MESSAGE_EMPTY = "There is no change to undo.";

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        return new CommandResult(model.undoAddressBook() ? MESSAGE_SUCCESS : MESSAGE_EMPTY);
    }
}
