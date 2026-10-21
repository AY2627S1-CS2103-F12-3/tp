package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_ROLE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_ROLE_LIST_NOT_DISPLAYED;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE_TITLE;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.RoleCommandParser;
import seedu.address.model.Model;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;

/**
 * Edits a displayed role and keeps its candidates associated with its updated title.
 */
public class EditRoleCommand extends Command {

    public static final String COMMAND_WORD = "edit";
    public static final String MESSAGE_USAGE = RoleCommandParser.COMMAND_WORD + " " + COMMAND_WORD
            + ": Edits the title or required experience level of a displayed role.\n"
            + "Parameters: INDEX (must be a positive integer) [" + PREFIX_ROLE_TITLE + "ROLE_TITLE] ["
            + PREFIX_EXPERIENCE_LEVEL + "EXPERIENCE_LEVEL]\n"
            + "Example: " + RoleCommandParser.COMMAND_WORD + " " + COMMAND_WORD + " 1 "
            + PREFIX_EXPERIENCE_LEVEL + "Senior";
    public static final String MESSAGE_SUCCESS = "Edited role: %1$s.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";

    private static final Logger logger = LogsCenter.getLogger(EditRoleCommand.class);

    private final Index targetIndex;
    private final RoleTitle title;
    private final ExperienceLevel experienceLevel;

    /**
     * Creates a command to change the experience level of the role at {@code targetIndex}.
     */
    public EditRoleCommand(Index targetIndex, ExperienceLevel experienceLevel) {
        this(targetIndex, null, requireNonNull(experienceLevel));
    }

    /**
     * Creates a command to edit the supplied role fields; a null field is left unchanged.
     * At least one of {@code title} and {@code experienceLevel} must be supplied.
     */
    public EditRoleCommand(Index targetIndex, RoleTitle title, ExperienceLevel experienceLevel) {
        requireNonNull(targetIndex);
        checkArgument(CollectionUtil.isAnyNonNull(title, experienceLevel), MESSAGE_NOT_EDITED);
        this.targetIndex = targetIndex;
        this.title = title;
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
        Role editedRole = new Role(title == null ? role.getTitle() : title,
                experienceLevel == null ? role.getExperienceLevel() : experienceLevel, role.getStatus());
        if (!role.isSameRole(editedRole) && model.hasRole(editedRole)) {
            logger.info("Rejected role edit because the destination title already exists.");
            throw new CommandException(String.format(MESSAGE_DUPLICATE_ROLE, editedRole.getTitle()));
        }
        model.setRole(role, editedRole);
        logger.info("Updated role details: " + role.getTitle() + " -> " + editedRole.getTitle());
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedRole.getTitle()), false, false, true);
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
                && Objects.equals(title, otherCommand.title)
                && Objects.equals(experienceLevel, otherCommand.experienceLevel);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("title", title)
                .add("experienceLevel", experienceLevel)
                .toString();
    }
}
