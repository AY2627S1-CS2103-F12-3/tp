package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void constructor_phoneWithSeparators_storesDigitsOnly() {
        assertEquals("+6591234567", new Phone("+65 9123-4567").value);
        assertEquals("91234567", new Phone("9123 4567").value);
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("9123456")); // 7 digits
        assertFalse(Phone.isValidPhone("1234567890123456")); // 16 digits
        assertFalse(Phone.isValidPhone("+9123456")); // 7 digits after '+'
        assertFalse(Phone.isValidPhone("9123 456")); // 7 digits with a separator
        assertFalse(Phone.isValidPhone("++6591234567")); // more than one '+'
        assertFalse(Phone.isValidPhone("91+234567")); // '+' not at the start
        assertFalse(Phone.isValidPhone("+ 6591234567")); // separator after '+'
        assertFalse(Phone.isValidPhone("9123  4567")); // consecutive spaces
        assertFalse(Phone.isValidPhone("9123--4567")); // consecutive hyphens
        assertFalse(Phone.isValidPhone("9123 - 4567")); // consecutive separators
        assertFalse(Phone.isValidPhone("-91234567")); // separator at the start
        assertFalse(Phone.isValidPhone("9123-4567-")); // separator at the end
        assertFalse(Phone.isValidPhone("9123.4567")); // separator that is not allowed

        // valid phone numbers
        assertTrue(Phone.isValidPhone("91234567")); // exactly 8 digits
        assertTrue(Phone.isValidPhone("123456789012345")); // exactly 15 digits
        assertTrue(Phone.isValidPhone("+6591234567")); // with '+'
        assertTrue(Phone.isValidPhone("+65 9123-4567")); // with '+', space and hyphen
        assertTrue(Phone.isValidPhone("9123 4567")); // with a space
        assertTrue(Phone.isValidPhone("9123-4567")); // with a hyphen
        assertTrue(Phone.isValidPhone("+12345678")); // '+' does not count as a digit
    }

    @Test
    public void equals() {
        Phone phone = new Phone("91234567");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("91234567")));

        // same digits with separators -> returns true
        assertTrue(phone.equals(new Phone("9123 4567")));
        assertTrue(phone.equals(new Phone("9123-4567")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("91234568")));
    }
}
