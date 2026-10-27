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
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;

/**
 * Closes recruitment for a displayed role after all its candidates have final outcomes.
 */
public class CloseRoleCommand extends Command {

    public static final String COMMAND_WORD = "close";
    public static final String MESSAGE_USAGE = RoleCommandParser.COMMAND_WORD + " " + COMMAND_WORD
            + ": Closes recruitment for a displayed role.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + RoleCommandParser.COMMAND_WORD + " " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Closed role: %1$s.";
    public static final String MESSAGE_ALREADY_CLOSED = "%1$s is already closed.";
    public static final String MESSAGE_PENDING_CANDIDATES =
            "Cannot close role: %1$s. %2$d candidate(s) still pending.";
    public static final String MESSAGE_MULTIPLE_HIRED =
            "Cannot close role: %1$s. More than one candidate is marked HIRED.";
    public static final String MESSAGE_EXTRA_TEXT = "The role close command takes only a role index.";

    private static final Logger logger = LogsCenter.getLogger(CloseRoleCommand.class);

    private final Index targetIndex;

    /**
     * Creates a command to close the role at {@code targetIndex}.
     */
    public CloseRoleCommand(Index targetIndex) {
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
        if (role.isClosed()) {
            logger.fine("Rejected role closure: role is already closed.");
            throw new CommandException(String.format(MESSAGE_ALREADY_CLOSED, role.getTitle()));
        }
        verifyCandidateOutcomes(model, role);
        Role closedRole = new Role(role.getTitle(), role.getExperienceLevel(), RoleStatus.CLOSED);
        model.setRole(role, closedRole);
        logger.info("Closed role in memory at displayed index " + targetIndex.getOneBased() + ".");
        return new CommandResult(String.format(MESSAGE_SUCCESS, role.getTitle()), false, false);
    }

    private static void verifyCandidateOutcomes(Model model, Role role) throws CommandException {
        List<Person> candidates = model.getAddressBook().getPersonList().stream()
                .filter(person -> role.getTitle().isSameTitle(person.getRole()))
                .toList();
        long pendingCount = candidates.stream().filter(person -> person.getStatus().isPending()).count();
        if (pendingCount > 0) {
            logger.fine("Rejected role closure: " + pendingCount + " candidate(s) still pending.");
            throw new CommandException(String.format(MESSAGE_PENDING_CANDIDATES, role.getTitle(), pendingCount));
        }
        long hiredCount = candidates.stream().filter(person -> person.getStatus() == Status.HIRED).count();
        if (hiredCount > 1) {
            logger.fine("Rejected role closure: " + hiredCount + " candidates are marked HIRED.");
            throw new CommandException(String.format(MESSAGE_MULTIPLE_HIRED, role.getTitle()));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CloseRoleCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
