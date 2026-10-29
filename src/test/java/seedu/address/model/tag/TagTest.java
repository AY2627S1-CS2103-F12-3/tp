package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
    }

    @Test
    public void equals_namesDifferOnlyInCase_returnsTrue() {
        assertEquals(new Tag("Referral"), new Tag("referral"));
        assertEquals(new Tag("mini"), new Tag("MINI"));
    }

    @Test
    public void equals_differentNames_returnsFalse() {
        assertNotEquals(new Tag("Referral"), new Tag("Referral2"));
    }

    @Test
    public void hashCode_namesDifferOnlyInCase_returnsSameHashCode() {
        assertEquals(new Tag("Referral").hashCode(), new Tag("referral").hashCode());
    }

    @Test
    public void isValidTagName_allowedLettersMarksDigitsAndSymbols_returnsTrue() {
        List<String> validTags = List.of("Java8", "José", "हिन्दी", "Cafe\u0301", "O'Brien", "C++", "C#",
                "front-end", "snake_case", ".NET", "v2.0", "人");

        for (String tag : validTags) {
            assertTrue(Tag.isValidTagName(tag), tag);
        }
    }

    @Test
    public void isValidTagName_disallowedCharacters_returnsFalse() {
        List<String> invalidTags = List.of("", "two words", "star*", "slash/name", "at@tag", "C\\Sharp",
                "line\nbreak", "😀");

        for (String tag : invalidTags) {
            assertFalse(Tag.isValidTagName(tag), tag);
        }
    }

    @Test
    public void isValidTagName_digitFromAnotherWritingSystem_returnsFalse() {
        assertFalse(Tag.isValidTagName("Skill१"));
    }
}
