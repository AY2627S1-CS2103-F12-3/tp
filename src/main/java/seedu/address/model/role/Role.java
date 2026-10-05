package seedu.address.model.role;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a role opening in RecruiterBuddy.
 * Guarantees: details are present and not null; field values are validated and immutable.
 */
public class Role {

    private final RoleTitle title;
    private final ExperienceLevel experienceLevel;
    private final RoleStatus status;

    /**
     * Constructs an open {@code Role}.
     */
    public Role(RoleTitle title, ExperienceLevel experienceLevel) {
        this(title, experienceLevel, RoleStatus.OPEN);
    }

    /**
     * Constructs a {@code Role} with the given status.
     */
    public Role(RoleTitle title, ExperienceLevel experienceLevel, RoleStatus status) {
        requireAllNonNull(title, experienceLevel, status);
        this.title = title;
        this.experienceLevel = experienceLevel;
        this.status = status;
    }

    public RoleTitle getTitle() {
        return title;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public RoleStatus getStatus() {
        return status;
    }

    /**
     * Returns true if both roles have the same title, ignoring case.
     */
    public boolean isSameRole(Role otherRole) {
        if (otherRole == this) {
            return true;
        }

        return otherRole != null
                && title.value.equalsIgnoreCase(otherRole.title.value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Role otherRole)) {
            return false;
        }

        return title.equals(otherRole.title)
                && experienceLevel.equals(otherRole.experienceLevel)
                && status == otherRole.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, experienceLevel, status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("title", title)
                .add("experienceLevel", experienceLevel)
                .add("status", status)
                .toString();
    }
}
