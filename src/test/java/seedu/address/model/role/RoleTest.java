package seedu.address.model.role;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.RoleBuilder;

public class RoleTest {

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        RoleTitle title = new RoleTitle("Software Engineer");
        ExperienceLevel experienceLevel = new ExperienceLevel("2 years");

        assertThrows(NullPointerException.class, () -> new Role(null, experienceLevel));
        assertThrows(NullPointerException.class, () -> new Role(title, null));
        assertThrows(NullPointerException.class, () -> new Role(title, experienceLevel, null));
    }

    @Test
    public void isSameRole() {
        assertTrue(SOFTWARE_ENGINEER.isSameRole(SOFTWARE_ENGINEER));

        Role sameTitleDifferentCase = new RoleBuilder(SOFTWARE_ENGINEER)
                .withTitle("software engineer")
                .withExperienceLevel("Entry level")
                .withStatus(RoleStatus.CLOSED)
                .build();
        assertTrue(SOFTWARE_ENGINEER.isSameRole(sameTitleDifferentCase));

        assertFalse(SOFTWARE_ENGINEER.isSameRole(PRODUCT_ANALYST));
        assertFalse(SOFTWARE_ENGINEER.isSameRole(null));
    }

    @Test
    public void equals() {
        Role softwareEngineerCopy = new RoleBuilder(SOFTWARE_ENGINEER).build();
        assertTrue(SOFTWARE_ENGINEER.equals(softwareEngineerCopy));

        Role differentExperienceLevel = new RoleBuilder(SOFTWARE_ENGINEER)
                .withExperienceLevel("Senior")
                .build();
        assertFalse(SOFTWARE_ENGINEER.equals(differentExperienceLevel));

        Role differentStatus = new RoleBuilder(SOFTWARE_ENGINEER)
                .withStatus(RoleStatus.CLOSED)
                .build();
        assertFalse(SOFTWARE_ENGINEER.equals(differentStatus));

        assertFalse(SOFTWARE_ENGINEER.equals(null));
        assertFalse(SOFTWARE_ENGINEER.equals(5));
    }
}
