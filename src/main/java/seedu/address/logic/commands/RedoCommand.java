package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

public class RedoCommand extends Command {
    public static final String COMMAND_WORD = "redo";
    public static final String MESSAGE_SUCCESS = "Redid the previous change.";
    public static final String MESSAGE_EMPTY = "There is no change to redo.";

    @Override
    public boolean isUndoable() { return false; }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        return new CommandResult(model.redoAddressBook() ? MESSAGE_SUCCESS : MESSAGE_EMPTY);
    }
}
