package seedu.address.testutil;

import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;
import seedu.address.model.role.RoleTitle;

/**
 * Builds {@link Role} objects for tests.
 */
public class RoleBuilder {

    public static final String DEFAULT_TITLE = "Software Engineer";
    public static final String DEFAULT_EXPERIENCE_LEVEL = "2 years";
    public static final RoleStatus DEFAULT_STATUS = RoleStatus.OPEN;

    private RoleTitle title;
    private ExperienceLevel experienceLevel;
    private RoleStatus status;

    /**
     * Creates a {@code RoleBuilder} with default field values.
     */
    public RoleBuilder() {
        title = new RoleTitle(DEFAULT_TITLE);
        experienceLevel = new ExperienceLevel(DEFAULT_EXPERIENCE_LEVEL);
        status = DEFAULT_STATUS;
    }

    /**
     * Copies the fields of {@code role} into a new builder.
     */
    public RoleBuilder(Role role) {
        title = role.getTitle();
        experienceLevel = role.getExperienceLevel();
        status = role.getStatus();
    }

    /**
     * Sets the role title.
     */
    public RoleBuilder withTitle(String title) {
        this.title = new RoleTitle(title);
        return this;
    }

    /**
     * Sets the required experience level.
     */
    public RoleBuilder withExperienceLevel(String experienceLevel) {
        this.experienceLevel = new ExperienceLevel(experienceLevel);
        return this;
    }

    /**
     * Sets the role status.
     */
    public RoleBuilder withStatus(RoleStatus status) {
        this.status = status;
        return this;
    }

    public Role build() {
        return new Role(title, experienceLevel, status);
    }
}
