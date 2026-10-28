package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Formats full help instructions for every command for display.
 */
public class HelpCommand extends Command {
    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the offline command guide.\n"
            + "Parameters: None\n"
            + "Example: " + COMMAND_WORD;

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        return new CommandResult(CommandGuide.getOverview(), true, false);
    }
}
