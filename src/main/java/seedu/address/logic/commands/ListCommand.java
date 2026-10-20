package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_ROLES;

import seedu.address.model.Model;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {
    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " [role]";

    public static final String MESSAGE_SUCCESS = "Listed all candidates.";
    public static final String MESSAGE_ROLE_SUCCESS = "Listed all roles.";

    private final boolean listRoles;

    public ListCommand() {
        this(false);
    }

    public ListCommand(boolean listRoles) {
        this.listRoles = listRoles;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (listRoles) {
            model.updateFilteredRoleList(PREDICATE_SHOW_ALL_ROLES);
            return new CommandResult(MESSAGE_ROLE_SUCCESS, false, false, true);
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
