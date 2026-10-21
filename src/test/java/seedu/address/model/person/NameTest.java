package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only symbols
        assertFalse(Name.isValidName("-")); // only an allowed symbol
        assertFalse(Name.isValidName("peter*")); // contains a symbol that is not allowed
        assertFalse(Name.isValidName("Alice & Bob")); // contains a symbol that is not allowed
        assertFalse(Name.isValidName("12345")); // starts with a digit
        assertFalse(Name.isValidName("1John")); // starts with a digit
        assertFalse(Name.isValidName("(Mary) Tan")); // starts with a symbol
        assertFalse(Name.isValidName(" Peter")); // starts with a space
        assertFalse(Name.isValidName("a".repeat(Name.MAX_LENGTH + 1))); // one character too long

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("O'Brien")); // apostrophe
        assertTrue(Name.isValidName("Mary-Jane St. Claire")); // hyphen and period
        assertTrue(Name.isValidName("Tan, Mei Ling")); // comma
        assertTrue(Name.isValidName("Wong (Mary) Li")); // parentheses
        assertTrue(Name.isValidName("Ravi s/o Kumar")); // slash
        assertTrue(Name.isValidName("José")); // accented letter
        assertTrue(Name.isValidName("José")); // accent typed as a separate mark
        assertTrue(Name.isValidName("山田太郎")); // non-English letters
        assertTrue(Name.isValidName("a")); // exactly 1 character
        assertTrue(Name.isValidName("a".repeat(Name.MAX_LENGTH))); // exactly the maximum length
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
