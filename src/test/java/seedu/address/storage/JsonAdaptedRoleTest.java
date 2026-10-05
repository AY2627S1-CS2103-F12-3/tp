package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedRole.INVALID_STATUS_MESSAGE_FORMAT;
import static seedu.address.storage.JsonAdaptedRole.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.RoleStatus;
import seedu.address.model.role.RoleTitle;

public class JsonAdaptedRoleTest {

    private static final String INVALID_TITLE = "Software_Engineer";
    private static final String INVALID_EXPERIENCE_LEVEL = "Entry\nlevel";
    private static final String INVALID_STATUS = "AVAILABLE";

    private static final String VALID_TITLE = SOFTWARE_ENGINEER.getTitle().toString();
    private static final String VALID_EXPERIENCE_LEVEL = SOFTWARE_ENGINEER.getExperienceLevel().toString();
    private static final String VALID_STATUS = SOFTWARE_ENGINEER.getStatus().name();

    @Test
    public void toModelType_validRoleDetails_returnsRole() throws Exception {
        JsonAdaptedRole role = new JsonAdaptedRole(SOFTWARE_ENGINEER);
        assertEquals(SOFTWARE_ENGINEER, role.toModelType());
    }

    @Test
    public void toModelType_invalidTitle_throwsIllegalValueException() {
        JsonAdaptedRole role = new JsonAdaptedRole(INVALID_TITLE, VALID_EXPERIENCE_LEVEL, VALID_STATUS);
        assertThrows(IllegalValueException.class, RoleTitle.MESSAGE_CONSTRAINTS, role::toModelType);
    }

    @Test
    public void toModelType_nullTitle_throwsIllegalValueException() {
        JsonAdaptedRole role = new JsonAdaptedRole(null, VALID_EXPERIENCE_LEVEL, VALID_STATUS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, RoleTitle.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, role::toModelType);
    }

    @Test
    public void toModelType_invalidExperienceLevel_throwsIllegalValueException() {
        JsonAdaptedRole role = new JsonAdaptedRole(VALID_TITLE, INVALID_EXPERIENCE_LEVEL, VALID_STATUS);
        assertThrows(IllegalValueException.class, ExperienceLevel.MESSAGE_CONSTRAINTS, role::toModelType);
    }

    @Test
    public void toModelType_nullExperienceLevel_throwsIllegalValueException() {
        JsonAdaptedRole role = new JsonAdaptedRole(VALID_TITLE, null, VALID_STATUS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT,
                ExperienceLevel.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, role::toModelType);
    }

    @Test
    public void toModelType_invalidStatus_throwsIllegalValueException() {
        JsonAdaptedRole role = new JsonAdaptedRole(VALID_TITLE, VALID_EXPERIENCE_LEVEL, INVALID_STATUS);
        String expectedMessage = String.format(INVALID_STATUS_MESSAGE_FORMAT, INVALID_STATUS);
        assertThrows(IllegalValueException.class, expectedMessage, role::toModelType);
    }

    @Test
    public void toModelType_nullStatus_throwsIllegalValueException() {
        JsonAdaptedRole role = new JsonAdaptedRole(VALID_TITLE, VALID_EXPERIENCE_LEVEL, null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, RoleStatus.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, role::toModelType);
    }
}
