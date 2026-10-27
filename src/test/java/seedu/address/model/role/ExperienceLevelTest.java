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

    @Test
    public void constructor_whitespaceOnly_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ExperienceLevel.MESSAGE_CONSTRAINTS, () ->
                new ExperienceLevel(" \t "));
    }

    @Test
    public void constructor_paddedMaximumLengthValue_trimsBeforeValidation() {
        String maximumLengthValue = "A".repeat(50);
        ExperienceLevel experienceLevel = new ExperienceLevel("  " + maximumLengthValue + "  ");
        assertEquals(maximumLengthValue, experienceLevel.value);
    }

    @Test
    public void isValidExperienceLevel_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ExperienceLevel.isValidExperienceLevel(null));
    }

    @Test
    public void validation_nonPrintableUnicode_rejectsValue() {
        String[] nonPrintableCharacters = {
            "\uD800", // isolated high surrogate
            "\uDC00", // isolated low surrogate
            "\u0378", // unassigned code point
            "\u2028", // line separator
            "\u2029" // paragraph separator
        };
        for (String character : nonPrintableCharacters) {
            // Embed the character so surrounding-whitespace removal cannot hide it.
            String invalidValue = "Entry" + character + "level";
            assertFalse(ExperienceLevel.isValidExperienceLevel(invalidValue),
                    "Non-printable code point: " + Integer.toHexString(character.codePointAt(0)));
            assertThrows(IllegalArgumentException.class, ExperienceLevel.MESSAGE_CONSTRAINTS, () ->
                    new ExperienceLevel(invalidValue));
        }
    }

    @Test
    public void validation_supplementaryCharacters_countsCodePoints() {
        String supplementaryCharacter = "\uD83D\uDE00";
        String maximumLengthValue = supplementaryCharacter.repeat(50);
        assertTrue(ExperienceLevel.isValidExperienceLevel(maximumLengthValue));
        assertEquals(maximumLengthValue, new ExperienceLevel(maximumLengthValue).value);

        String overlongValue = supplementaryCharacter.repeat(51);
        assertFalse(ExperienceLevel.isValidExperienceLevel(overlongValue));
        assertThrows(IllegalArgumentException.class, ExperienceLevel.MESSAGE_CONSTRAINTS, () ->
                new ExperienceLevel(overlongValue));
    }

    @Test
    public void equals_sameOrDifferentValues_comparesTrimmedText() {
        ExperienceLevel experienceLevel = new ExperienceLevel("Entry level");
        ExperienceLevel sameValue = new ExperienceLevel("  Entry level  ");

        assertTrue(experienceLevel.equals(experienceLevel));
        assertTrue(experienceLevel.equals(sameValue));
        assertTrue(sameValue.equals(experienceLevel));
        assertFalse(experienceLevel.equals(null));
        assertFalse(experienceLevel.equals("Entry level"));
        assertFalse(experienceLevel.equals(new ExperienceLevel("Senior")));
        assertFalse(experienceLevel.equals(new ExperienceLevel("entry level")));
    }

    @Test
    public void hashCode_equivalentTrimmedValues_returnsSameHashCode() {
        ExperienceLevel experienceLevel = new ExperienceLevel("Entry level");
        ExperienceLevel sameValue = new ExperienceLevel("  Entry level  ");
        assertEquals(experienceLevel.hashCode(), sameValue.hashCode());
    }

    @Test
    public void toString_trimmedValue_returnsStoredText() {
        assertEquals("Entry level", new ExperienceLevel("  Entry level  ").toString());
    }
}
