package seedu.address.model.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class ExperienceLevelTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ExperienceLevel(null));
    }

    @Test
    public void constructor_invalidExperienceLevel_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new ExperienceLevel("Entry\nlevel"));
    }

    @Test
    public void isValidExperienceLevel() {
        assertFalse(ExperienceLevel.isValidExperienceLevel(""));
        assertFalse(ExperienceLevel.isValidExperienceLevel("\n"));
        assertFalse(ExperienceLevel.isValidExperienceLevel("Entry\tlevel"));
        assertFalse(ExperienceLevel.isValidExperienceLevel("Entry\u200Blevel"));
        assertFalse(ExperienceLevel.isValidExperienceLevel("A".repeat(51)));

        assertTrue(ExperienceLevel.isValidExperienceLevel("A"));
        assertTrue(ExperienceLevel.isValidExperienceLevel("A".repeat(50)));
        assertTrue(ExperienceLevel.isValidExperienceLevel("Entry level"));
        assertTrue(ExperienceLevel.isValidExperienceLevel("2+ years (Java/C++)"));
        assertTrue(ExperienceLevel.isValidExperienceLevel("経験 2 年"));
    }

    @Test
    public void constructor_experienceLevelWithSurroundingWhitespace_trimsWhitespace() {
        ExperienceLevel experienceLevel = new ExperienceLevel("  Entry level  ");
        assertEquals("Entry level", experienceLevel.value);
    }
}
