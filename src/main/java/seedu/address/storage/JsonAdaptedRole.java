package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;
import seedu.address.model.role.RoleTitle;

/**
 * Jackson-friendly version of {@link Role}.
 */
class JsonAdaptedRole {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Role's %s field is missing!";
    public static final String INVALID_STATUS_MESSAGE_FORMAT = "Role status '%s' is invalid.";

    private final String title;
    private final String experienceLevel;
    private final String status;

    /**
     * Constructs a {@code JsonAdaptedRole} with the given details.
     */
    @JsonCreator
    public JsonAdaptedRole(@JsonProperty("title") String title,
            @JsonProperty("experienceLevel") String experienceLevel,
            @JsonProperty("status") String status) {
        this.title = title;
        this.experienceLevel = experienceLevel;
        this.status = status;
    }

    /**
     * Converts {@code source} into a Jackson-friendly role.
     */
    public JsonAdaptedRole(Role source) {
        title = source.getTitle().value;
        experienceLevel = source.getExperienceLevel().value;
        status = source.getStatus().name();
    }

    /**
     * Converts this adapted role into the model's {@code Role} object.
     *
     * @throws IllegalValueException if a stored field violates a model constraint.
     */
    public Role toModelType() throws IllegalValueException {
        if (title == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    RoleTitle.class.getSimpleName()));
        }
        if (!RoleTitle.isValidRoleTitle(title.strip())) {
            throw new IllegalValueException(RoleTitle.MESSAGE_CONSTRAINTS);
        }
        RoleTitle modelTitle = new RoleTitle(title);

        if (experienceLevel == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    ExperienceLevel.class.getSimpleName()));
        }
        if (!ExperienceLevel.isValidExperienceLevel(experienceLevel.strip())) {
            throw new IllegalValueException(ExperienceLevel.MESSAGE_CONSTRAINTS);
        }
        ExperienceLevel modelExperienceLevel = new ExperienceLevel(experienceLevel);

        if (status == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    RoleStatus.class.getSimpleName()));
        }

        RoleStatus modelStatus;
        try {
            modelStatus = RoleStatus.valueOf(status);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(String.format(INVALID_STATUS_MESSAGE_FORMAT, status), exception);
        }

        return new Role(modelTitle, modelExperienceLevel, modelStatus);
    }
}
