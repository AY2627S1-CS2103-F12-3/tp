package seedu.address.model.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RoleTitleTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RoleTitle(null));
    }

    @Test
    public void constructor_invalidTitle_throwsIllegalArgumentException() {
        String invalidTitle = "Software_Engineer";
        assertThrows(IllegalArgumentException.class, () -> new RoleTitle(invalidTitle));
    }

    @Test
    public void isValidRoleTitle() {
        assertFalse(RoleTitle.isValidRoleTitle(""));
        assertFalse(RoleTitle.isValidRoleTitle(" "));
        assertFalse(RoleTitle.isValidRoleTitle("123"));
        assertFalse(RoleTitle.isValidRoleTitle("Engineer@Platform"));
        assertFalse(RoleTitle.isValidRoleTitle("A".repeat(101)));

        assertTrue(RoleTitle.isValidRoleTitle("A"));
        assertTrue(RoleTitle.isValidRoleTitle("A".repeat(100)));
        assertTrue(RoleTitle.isValidRoleTitle("R&D Engineer - Platform/API 2.0"));
        assertTrue(RoleTitle.isValidRoleTitle("Directeur d'Ingénierie"));
    }

    @Test
    public void constructor_titleWithSurroundingWhitespace_trimsWhitespace() {
        RoleTitle roleTitle = new RoleTitle("  Software Engineer  ");
        assertEquals("Software Engineer", roleTitle.value);
    }
}
