package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ROLE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.model.role.RoleTitle;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same email, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(BOB).withEmail(ALICE.getEmail().value).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same email in different case -> returns true
        editedAlice = new PersonBuilder(BOB).withEmail(ALICE.getEmail().value.toUpperCase()).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same phone, all other attributes different -> returns true
        editedAlice = new PersonBuilder(BOB).withPhone(ALICE.getPhone().value).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same name, different email and phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // all attributes different -> returns false
        assertFalse(ALICE.isSamePerson(BOB));
    }

    @Test
    public void hasSameEmail() {
        assertTrue(ALICE.hasSameEmail(new PersonBuilder(BOB).withEmail(ALICE.getEmail().value).build()));
        assertFalse(ALICE.hasSameEmail(BOB));
        assertThrows(NullPointerException.class, () -> ALICE.hasSameEmail(null));
    }

    @Test
    public void hasSamePhone() {
        assertTrue(ALICE.hasSamePhone(new PersonBuilder(BOB).withPhone(ALICE.getPhone().value).build()));
        assertFalse(ALICE.hasSamePhone(BOB));
        assertThrows(NullPointerException.class, () -> ALICE.hasSamePhone(null));
    }

    @Test
    public void hasPossiblySamePhone() {
        String phoneWithCountryCode = "+65" + ALICE.getPhone().value;
        assertTrue(ALICE.hasPossiblySamePhone(
                new PersonBuilder(BOB).withPhone(phoneWithCountryCode).build()));
        assertFalse(ALICE.hasPossiblySamePhone(
                new PersonBuilder(BOB).withPhone(ALICE.getPhone().value).build()));
        assertFalse(ALICE.hasPossiblySamePhone(BOB));
        assertThrows(NullPointerException.class, () -> ALICE.hasPossiblySamePhone(null));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different role -> returns false
        editedAlice = new PersonBuilder(ALICE).withRole(VALID_ROLE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different note -> returns false
        editedAlice = new PersonBuilder(ALICE).withNote(VALID_NOTE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different interview -> returns false
        editedAlice = new PersonBuilder(ALICE).withInterview(VALID_INTERVIEW_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different status -> returns false
        editedAlice = new PersonBuilder(ALICE).withStatus(Status.HIRED).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void hashCodeMethod() {
        // same values -> same hash code
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertEquals(ALICE.hashCode(), aliceCopy.hashCode());

        // different note -> different hash code
        Person editedAlice = new PersonBuilder(ALICE).withNote(VALID_NOTE_BOB).build();
        assertNotEquals(ALICE.hashCode(), editedAlice.hashCode());
    }

    @Test
    public void withRole_newRole_changesOnlyRole() {
        Person expectedPerson = new PersonBuilder(ALICE).withRole(VALID_ROLE_BOB).build();
        assertEquals(expectedPerson, ALICE.withRole(new RoleTitle(VALID_ROLE_BOB)));
    }

    @Test
    public void withRole_personWithInterview_interviewPreserved() {
        Person aliceWithInterview = new PersonBuilder(ALICE).withInterview(VALID_INTERVIEW_BOB).build();
        Person expectedPerson = new PersonBuilder(aliceWithInterview).withRole(VALID_ROLE_BOB).build();
        assertEquals(expectedPerson, aliceWithInterview.withRole(new RoleTitle(VALID_ROLE_BOB)));
    }

    @Test
    public void withInterview_newInterview_changesOnlyInterview() {
        Person expectedPerson = new PersonBuilder(ALICE).withInterview(VALID_INTERVIEW_BOB).build();
        assertEquals(expectedPerson, ALICE.withInterview(new Interview(VALID_INTERVIEW_BOB)));
    }

    @Test
    public void withNote_newNote_changesOnlyNote() {
        Person expectedPerson = new PersonBuilder(ALICE).withNote(VALID_NOTE_BOB).build();
        assertEquals(expectedPerson, ALICE.withNote(new Note(VALID_NOTE_BOB)));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName()
                + ", phone=" + ALICE.getPhone() + ", email=" + ALICE.getEmail()
                + ", address=" + ALICE.getAddress() + ", role=" + ALICE.getRole()
                + ", note=" + ALICE.getNote() + ", interview=" + ALICE.getInterview()
                + ", status=" + ALICE.getStatus() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
