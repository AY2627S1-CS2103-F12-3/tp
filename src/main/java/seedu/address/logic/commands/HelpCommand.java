package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Shows the command overview or instructions for one command without changing recruitment data.
 */
public class HelpCommand extends Command {
    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the offline command guide.\n"
            + "Parameters: [COMMAND] (case-insensitive; omit to view all commands)\n"
            + "Examples: " + COMMAND_WORD + "; " + COMMAND_WORD + " add; " + COMMAND_WORD + " role";
    public static final String MESSAGE_TOO_MANY_ARGUMENTS = "Help accepts at most one command name.";
    public static final String MESSAGE_UNKNOWN_TOPIC =
            "No help is available for “%1$s”. Use help to view all commands.";

    private final String commandName;

    /**
     * Creates a command that shows the complete overview.
     */
    public HelpCommand() {
        this("");
    }

    /**
     * Creates a command that shows instructions for {@code commandName}, or the overview if it is empty.
     */
    public HelpCommand(String commandName) {
        this.commandName = requireNonNull(commandName);
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        String instructions = commandName.isEmpty() ? CommandGuide.getOverview()
                : CommandGuide.findHelp(commandName).orElseThrow(() ->
                        new CommandException(String.format(MESSAGE_UNKNOWN_TOPIC, commandName)));
        return new CommandResult(instructions, true, false);
    }
}
