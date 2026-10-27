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
    public void constructor_commonJobTitlePunctuation_acceptsTitle() {
        for (String title : new String[] {"C++ Developer", "C# Developer", "Engineer (Backend)",
            "Manager, Sales", "R&D Engineer (C++/C#), Platform - 2.0"}) {
            assertTrue(RoleTitle.isValidRoleTitle(title));
            assertEquals(title, new RoleTitle("  " + title + "  ").value);
        }
    }

    @Test
    public void isValidRoleTitle_eachNewCharacter_acceptsCharacterIndependently() {
        for (String punctuation : new String[] {"+", "#", "(", ")", ","}) {
            assertTrue(RoleTitle.isValidRoleTitle("A" + punctuation));
            assertTrue(RoleTitle.isValidRoleTitle("A" + punctuation.repeat(99)));
            assertFalse(RoleTitle.isValidRoleTitle("A" + punctuation.repeat(100)));
            assertFalse(RoleTitle.isValidRoleTitle(punctuation));
        }
    }

    @Test
    public void constructor_invalidTitleWithNewPunctuation_throwsIllegalArgumentException() {
        for (String title : new String[] {"123 +#(),", "C++_Developer", "C#@Developer", "Engineer\n(Backend)",
            "Engineer\r(Backend)", "Manager,\tSales", "C#\u0000Developer", "Engineer\u200B(Backend)"}) {
            assertFalse(RoleTitle.isValidRoleTitle(title));
            assertThrows(IllegalArgumentException.class, RoleTitle.MESSAGE_CONSTRAINTS, () -> new RoleTitle(title));
        }
    }

    @Test
    public void isSameTitle() {
        RoleTitle roleTitle = new RoleTitle("Software Engineer");

        // same title in a different case -> returns true
        assertTrue(roleTitle.isSameTitle(new RoleTitle("SOFTWARE engineer")));

        // different title -> returns false
        assertFalse(roleTitle.isSameTitle(new RoleTitle("Software Engineer II")));

        // null -> returns false
        assertFalse(roleTitle.isSameTitle(null));
    }

    @Test
    public void constructor_titleWithSurroundingWhitespace_trimsWhitespace() {
        RoleTitle roleTitle = new RoleTitle("  Software Engineer  ");
        assertEquals("Software Engineer", roleTitle.value);
    }
}
