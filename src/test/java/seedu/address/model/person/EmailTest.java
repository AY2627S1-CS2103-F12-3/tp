package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // missing parts
        assertFalse(Email.isValidEmail("@example.com")); // missing local part
        assertFalse(Email.isValidEmail("peterjackexample.com")); // missing '@' symbol
        assertFalse(Email.isValidEmail("peterjack@")); // missing domain name

        // invalid parts
        assertFalse(Email.isValidEmail("peterjack@-")); // invalid domain name
        assertFalse(Email.isValidEmail("peterjack@exam_ple.com")); // underscore in domain name
        assertFalse(Email.isValidEmail("peter jack@example.com")); // spaces in local part
        assertFalse(Email.isValidEmail("peterjack@exam ple.com")); // spaces in domain name
        assertFalse(Email.isValidEmail(" peterjack@example.com")); // leading space
        assertFalse(Email.isValidEmail("peterjack@example.com ")); // trailing space
        assertFalse(Email.isValidEmail("peterjack@@example.com")); // double '@' symbol
        assertFalse(Email.isValidEmail("peter@jack@example.com")); // '@' symbol in local part
        assertFalse(Email.isValidEmail(".peterjack@example.com")); // local part starts with a period
        assertFalse(Email.isValidEmail("peterjack.@example.com")); // local part ends with a period
        assertFalse(Email.isValidEmail("peter..jack@example.com")); // local part has two consecutive periods
        assertFalse(Email.isValidEmail("\"peter jack\"@example.com")); // quoted local part
        assertFalse(Email.isValidEmail("peter,jack@example.com")); // comma is not allowed without quotes
        assertFalse(Email.isValidEmail("pétér@example.com")); // non-English letters
        assertFalse(Email.isValidEmail("peterjack@example@com")); // '@' symbol in domain name
        assertFalse(Email.isValidEmail("peterjack@.example.com")); // domain name starts with a period
        assertFalse(Email.isValidEmail("peterjack@example.com.")); // domain name ends with a period
        assertFalse(Email.isValidEmail("peterjack@-example.com")); // domain name starts with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.com-")); // domain name ends with a hyphen
        assertFalse(Email.isValidEmail("peterjack@example.c")); // top level domain has less than two chars

        // valid email
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("a@bc")); // minimal
        assertTrue(Email.isValidEmail("test@localhost")); // alphabets only
        assertTrue(Email.isValidEmail("123@145")); // numeric local part and domain name
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
        assertTrue(Email.isValidEmail("o'brien@example.com")); // apostrophe in local part
        assertTrue(Email.isValidEmail("peter._jack@example.com")); // adjacent special characters
        assertTrue(Email.isValidEmail("peter.-jack@example.com")); // period followed by a hyphen
        assertTrue(Email.isValidEmail("-peterjack-@example.com")); // starts and ends with a hyphen
        assertTrue(Email.isValidEmail("a!#$%&*/=?^`{|}~b@example.com")); // other symbols allowed by RFC 5322
        assertTrue(Email.isValidEmail("peterjack@exa--mple.com")); // consecutive hyphens inside a domain part
        assertTrue(Email.isValidEmail("peterjack@xn--80ak6aa92e.com")); // encoded non-English domain name
    }

    @Test
    public void findValidationError_validEmail_returnsEmpty() {
        assertEquals(Optional.empty(), Email.findValidationError("peter.jack@example.com"));
        assertEquals(Optional.empty(), Email.findValidationError("a@bc"));
    }

    @Test
    public void findValidationError_invalidEmail_returnsSpecificMessage() {
        assertValidationError(Email.MESSAGE_EMPTY, "");
        assertValidationError(Email.MESSAGE_MISSING_AT, "peterjackexample.com");
        assertValidationError(Email.MESSAGE_MULTIPLE_AT, "peter@jack@example.com");
        assertValidationError(Email.MESSAGE_MISSING_NAME, "@example.com");
        assertValidationError(String.format(Email.MESSAGE_INVALID_LOCAL_PART_CHARACTER, "“,”"),
                "peter,jack@example.com");
        assertValidationError(Email.MESSAGE_LOCAL_PART_DOTS, ".peterjack@example.com");
        assertValidationError(Email.MESSAGE_LOCAL_PART_DOTS, "peterjack.@example.com");
        assertValidationError(Email.MESSAGE_LOCAL_PART_DOTS, "peter..jack@example.com");
        assertValidationError(String.format(Email.MESSAGE_INVALID_DOMAIN_CHARACTER, "“_”"),
                "peterjack@exam_ple.com");
        assertValidationError(Email.MESSAGE_DOMAIN_PARTS, "peterjack@"); // empty domain
        assertValidationError(Email.MESSAGE_DOMAIN_PARTS, "peterjack@.example.com");
        assertValidationError(Email.MESSAGE_DOMAIN_PARTS, "peterjack@example..com");
        assertValidationError(Email.MESSAGE_DOMAIN_PARTS, "peterjack@-example.com");
        assertValidationError(Email.MESSAGE_DOMAIN_PARTS, "peterjack@example-.com");
        assertValidationError(Email.MESSAGE_DOMAIN_ENDING, "peterjack@example.c");
    }

    @Test
    public void findValidationError_invisibleCharacter_namesCharacter() {
        assertValidationError(String.format(Email.MESSAGE_INVALID_LOCAL_PART_CHARACTER, "a space"),
                "peter jack@example.com");
        assertValidationError(String.format(Email.MESSAGE_INVALID_DOMAIN_CHARACTER, "a tab"),
                "peterjack@exam\tple.com");
        assertValidationError(String.format(Email.MESSAGE_INVALID_LOCAL_PART_CHARACTER, "a new line"),
                "peter\njack@a.com");
        assertValidationError(String.format(Email.MESSAGE_INVALID_LOCAL_PART_CHARACTER, "“é”"),
                "pétér@example.com");
        assertValidationError(String.format(Email.MESSAGE_INVALID_LOCAL_PART_CHARACTER, "a quotation mark"),
                "\"peterjack\"@example.com");
    }

    @Test
    public void findValidationError_severalProblems_returnsFirstInCheckOrder() {
        assertValidationError(Email.MESSAGE_MULTIPLE_AT, "peter jack@@example.com"); // '@' count before characters
        assertValidationError(Email.MESSAGE_MISSING_NAME, "@exam_ple.com"); // missing name before domain problems
        assertValidationError(String.format(Email.MESSAGE_INVALID_LOCAL_PART_CHARACTER, "“,”"),
                "peter,..jack@example.com"); // characters before dots
        assertValidationError(Email.MESSAGE_LOCAL_PART_DOTS, "peter..jack@exam_ple.com"); // local part before domain
        assertValidationError(String.format(Email.MESSAGE_INVALID_DOMAIN_CHARACTER, "“_”"),
                "peterjack@-exam_ple.c"); // domain characters before domain parts
        assertValidationError(Email.MESSAGE_DOMAIN_PARTS, "peterjack@-example.c"); // domain parts before ending length
    }

    private static void assertValidationError(String expectedMessage, String email) {
        assertEquals(Optional.of(expectedMessage), Email.findValidationError(email));
        assertFalse(Email.isValidEmail(email));
    }

    @Test
    public void hasDotlessDomain() {
        assertTrue(new Email("john@gmailcom").hasDotlessDomain());
        assertTrue(new Email("john.doe@gmailcom").hasDotlessDomain()); // periods before '@' are ignored
        assertTrue(new Email("a@bc").hasDotlessDomain());
        assertFalse(new Email("john@gmail.com").hasDotlessDomain());
        assertFalse(new Email("e1234567@u.nus.edu").hasDotlessDomain());
    }

    @Test
    public void getUncommonSymbols() {
        assertEquals("", new Email("o'brien.doe_1+jobs-x@example.com").getUncommonSymbols()); // common symbols only
        assertEquals("#", new Email("john#doe@example.com").getUncommonSymbols());
        assertEquals("! #", new Email("a!b#c!@example.com").getUncommonSymbols()); // each symbol listed once
        assertEquals("", new Email("john@ex-ample.com").getUncommonSymbols()); // domain is not checked
    }

    @Test
    public void getDomain() {
        assertEquals("example.com", new Email("john.doe@example.com").getDomain());
    }

    @Test
    public void isSameEmail() {
        Email email = new Email("alex@example.com");

        assertTrue(email.isSameEmail(new Email("alex@example.com")));
        assertTrue(email.isSameEmail(new Email("Alex@Example.COM"))); // different case
        assertFalse(email.isSameEmail(new Email("alex.tan@example.com")));
        assertThrows(NullPointerException.class, () -> email.isSameEmail(null));
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@email")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@email")));
    }
}
