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
        assertEquals("91234567", new Phone("9123 - 4567").value);
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone(" - ")); // spaces and hyphens only
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("9123456")); // 7 digits
        assertFalse(Phone.isValidPhone("1234567890123456")); // 16 digits
        assertFalse(Phone.isValidPhone("+9123456")); // 7 digits after '+'
        assertFalse(Phone.isValidPhone("9123 456")); // 7 digits once spaces are ignored
        assertFalse(Phone.isValidPhone("++6591234567")); // more than one '+'
        assertFalse(Phone.isValidPhone("91+234567")); // '+' not at the start
        assertFalse(Phone.isValidPhone("9123.4567")); // separator that is not ignored

        // valid phone numbers
        assertTrue(Phone.isValidPhone("91234567")); // exactly 8 digits
        assertTrue(Phone.isValidPhone("123456789012345")); // exactly 15 digits
        assertTrue(Phone.isValidPhone("+6591234567")); // with '+'
        assertTrue(Phone.isValidPhone("+12345678")); // '+' does not count as a digit
        assertTrue(Phone.isValidPhone("+65 9123-4567")); // with '+', space and hyphen
        assertTrue(Phone.isValidPhone("9123 4567")); // with a space
        assertTrue(Phone.isValidPhone("9123-4567")); // with a hyphen
        assertTrue(Phone.isValidPhone("9123 - 4567")); // consecutive spaces and hyphens
        assertTrue(Phone.isValidPhone("+ 65 9123 4567")); // space after '+'
        assertTrue(Phone.isValidPhone("-91234567-")); // hyphens at the start and end
    }

    @Test
    public void isPossiblySameNumber_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone("91234567").isPossiblySameNumber(null));
    }

    @Test
    public void isPossiblySameNumber() {
        Phone localNumber = new Phone("91234567");

        // Singapore number with and without the country code -> returns true
        assertTrue(localNumber.isPossiblySameNumber(new Phone("+65 9123 4567")));
        assertTrue(new Phone("+6591234567").isPossiblySameNumber(localNumber));
        assertTrue(localNumber.isPossiblySameNumber(new Phone("6591234567"))); // country code without '+'
        assertTrue(new Phone("+6591234567").isPossiblySameNumber(new Phone("6591234567")));

        // overseas number with and without '+' -> returns true
        assertTrue(new Phone("+60123456789").isPossiblySameNumber(new Phone("60123456789")));

        // exactly the same number, even with different separators -> returns false
        assertFalse(localNumber.isPossiblySameNumber(new Phone("9123 4567")));
        assertFalse(new Phone("+6591234567").isPossiblySameNumber(new Phone("+65 9123-4567")));

        // different numbers -> returns false
        assertFalse(localNumber.isPossiblySameNumber(new Phone("91234568")));
        assertFalse(localNumber.isPossiblySameNumber(new Phone("+85291234567"))); // other country code
        // 65 and 7 digits is not stripped
        assertFalse(localNumber.isPossiblySameNumber(new Phone("659123456")));
        assertFalse(new Phone("912345678").isPossiblySameNumber(new Phone("65912345678"))); // 65 and 9 digits

        // overseas number with and without its country code -> returns false,
        // as overseas formats are not known
        assertFalse(new Phone("+60123456789").isPossiblySameNumber(new Phone("0123456789")));
    }

    @Test
    public void isPlausibleNumber() {
        // Singapore numbers -> returns true
        assertTrue(new Phone("91234567").isPlausibleNumber()); // mobile
        assertTrue(new Phone("61234567").isPlausibleNumber()); // landline
        assertTrue(new Phone("81234567").isPlausibleNumber());
        assertTrue(new Phone("31234567").isPlausibleNumber());
        assertTrue(new Phone("+65 9123 4567").isPlausibleNumber()); // with country code
        assertTrue(new Phone("6591234567").isPlausibleNumber()); // country code without '+'

        // overseas numbers with a country code -> returns true
        assertTrue(new Phone("+60 12-345 6789").isPlausibleNumber());
        assertTrue(new Phone("+12345678").isPlausibleNumber());

        // numbers treated as Singapore numbers that do not look like one -> returns false
        assertFalse(new Phone("12345678").isPlausibleNumber()); // starts with 1
        assertFalse(new Phone("51234567").isPlausibleNumber()); // starts with 5
        assertFalse(new Phone("912345678").isPlausibleNumber()); // 9 digits
        assertFalse(new Phone("0123456789").isPlausibleNumber()); // overseas number without its country code
        // Singapore code, then not a Singapore number
        assertFalse(new Phone("+6512345678").isPlausibleNumber());
        assertFalse(new Phone("+65912345678").isPlausibleNumber()); // Singapore code, then 9 digits
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
