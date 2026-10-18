package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_ROLE_LIST_NOT_DISPLAYED;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.RoleCommandParser;
import seedu.address.model.Model;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;

/**
 * Edits the required experience level of a displayed role without changing its recruitment history.
 */
public class EditRoleCommand extends Command {

    public static final String COMMAND_WORD = "edit";
    public static final String MESSAGE_USAGE = RoleCommandParser.COMMAND_WORD + " " + COMMAND_WORD
            + ": Edits the required experience level of a displayed role.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_EXPERIENCE_LEVEL + "EXPERIENCE_LEVEL\n"
            + "Example: " + RoleCommandParser.COMMAND_WORD + " " + COMMAND_WORD + " 1 "
            + PREFIX_EXPERIENCE_LEVEL + "Senior";
    public static final String MESSAGE_SUCCESS = "Edited role: %1$s.";

    private final Index targetIndex;
    private final ExperienceLevel experienceLevel;

    /**
     * Creates a command to change the experience level of the role at {@code targetIndex}.
     */
    public EditRoleCommand(Index targetIndex, ExperienceLevel experienceLevel) {
        requireAllNonNull(targetIndex, experienceLevel);
        this.targetIndex = targetIndex;
        this.experienceLevel = experienceLevel;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.isRoleListDisplayed()) {
            throw new CommandException(MESSAGE_ROLE_LIST_NOT_DISPLAYED);
        }
        List<Role> displayedRoles = model.getFilteredRoleList();
        if (targetIndex.getZeroBased() >= displayedRoles.size()) {
            throw new CommandException(MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
        }
        Role role = displayedRoles.get(targetIndex.getZeroBased());
        Role editedRole = new Role(role.getTitle(), experienceLevel, role.getStatus());
        model.setRole(role, editedRole);
        return new CommandResult(String.format(MESSAGE_SUCCESS, role.getTitle()), false, false, true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditRoleCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex)
                && experienceLevel.equals(otherCommand.experienceLevel);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("experienceLevel", experienceLevel)
                .toString();
    }
}
