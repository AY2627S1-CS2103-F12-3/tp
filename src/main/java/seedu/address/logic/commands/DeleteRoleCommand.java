package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.RoleCommandParser;
import seedu.address.model.Model;
import seedu.address.model.role.Role;

/**
 * Deletes a displayed role only when it has no associated candidates.
 */
public class DeleteRoleCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String MESSAGE_USAGE = RoleCommandParser.COMMAND_WORD + " " + COMMAND_WORD
            + ": Deletes a displayed role with no candidates.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + RoleCommandParser.COMMAND_WORD + " " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Deleted role: %1$s.";
    public static final String MESSAGE_ASSOCIATED_CANDIDATES =
            "Cannot delete role: %1$s. It has %2$d associated candidate(s).";
    public static final String MESSAGE_EXTRA_TEXT = "The role delete command takes only a role index.";

    private static final Logger logger = LogsCenter.getLogger(DeleteRoleCommand.class);

    private final Index targetIndex;

    /**
     * Creates a command to delete the role at {@code targetIndex}.
     */
    public DeleteRoleCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Role> displayedRoles = model.getFilteredRoleList();
        if (targetIndex.getZeroBased() >= displayedRoles.size()) {
            throw new CommandException(MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
        }
        Role role = displayedRoles.get(targetIndex.getZeroBased());
        long candidateCount = model.getAddressBook().getPersonList().stream()
                .filter(person -> role.getTitle().isSameTitle(person.getRole()))
                .count();
        if (candidateCount > 0) {
            logger.fine("Rejected role deletion: " + candidateCount + " associated candidate(s).");
            throw new CommandException(String.format(MESSAGE_ASSOCIATED_CANDIDATES, role.getTitle(), candidateCount));
        }
        model.deleteRole(role);
        logger.info("Deleted role in memory at displayed index " + targetIndex.getOneBased() + ".");
        return new CommandResult(String.format(MESSAGE_SUCCESS, role.getTitle()), false, false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteRoleCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
