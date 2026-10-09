package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE_TITLE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.role.Role;

/**
 * Adds an open role to RecruiterBuddy.
 */
public class AddRoleCommand extends Command {

    public static final String COMMAND_GROUP = "role";
    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_GROUP + " " + COMMAND_WORD
            + ": Adds an open role. "
            + "Parameters: "
            + PREFIX_ROLE_TITLE + "ROLE_TITLE "
            + PREFIX_EXPERIENCE_LEVEL + "EXPERIENCE_LEVEL\n"
            + "Example: " + COMMAND_GROUP + " " + COMMAND_WORD + " "
            + PREFIX_ROLE_TITLE + "Software Engineer "
            + PREFIX_EXPERIENCE_LEVEL + "2 years";

    public static final String MESSAGE_SUCCESS = "Added open role: %1$s.";
    public static final String MESSAGE_DUPLICATE_ROLE = "A role named “%1$s” already exists.";

    private final Role toAdd;

    /**
     * Creates an {@code AddRoleCommand} to add {@code role}.
     */
    public AddRoleCommand(Role role) {
        requireNonNull(role);
        toAdd = role;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasRole(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_ROLE, toAdd.getTitle()));
        }

        model.addRole(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getTitle()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddRoleCommand otherAddRoleCommand)) {
            return false;
        }

        return toAdd.equals(otherAddRoleCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
