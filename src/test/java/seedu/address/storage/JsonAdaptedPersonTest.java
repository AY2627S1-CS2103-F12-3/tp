package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static seedu.address.storage.JsonAdaptedPerson.INVALID_STATUS_MESSAGE_FORMAT;
import static seedu.address.storage.JsonAdaptedPerson.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Interview;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Status;
import seedu.address.model.role.RoleTitle;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedPersonTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend!";
    private static final String INVALID_STATUS = "GHOSTED";
    private static final String INVALID_ROLE = "2027";
    private static final String INVALID_INTERVIEW_FORMAT = "20/10/2026 14:00";
    private static final String INVALID_INTERVIEW_DATE_TIME = "2026-02-30 14:00";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_ROLE = BENSON.getRole().toString();
    private static final String VALID_NOTE = BENSON.getNote().toString();
    private static final String VALID_INTERVIEW = BENSON.getInterview().toString();
    private static final String VALID_STATUS = BENSON.getStatus().name();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validPersonDetails_returnsPerson() throws Exception {
        JsonAdaptedPerson person = new JsonAdaptedPerson(BENSON);
        assertEquals(BENSON, person.toModelType());
    }

    @Test
    public void toModelType_personWithNote_returnsPersonWithNote() throws Exception {
        Person personWithNote = new PersonBuilder(BENSON).withNote(VALID_NOTE_BOB).build();
        JsonAdaptedPerson person = new JsonAdaptedPerson(personWithNote);
        assertEquals(personWithNote, person.toModelType());
    }

    @Test
    public void toModelType_nullNote_returnsPersonWithEmptyNote() throws Exception {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        null, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        Person expectedPerson = new PersonBuilder(BENSON).withNote("").build();
        assertEquals(expectedPerson, person.toModelType());
    }

    @Test
    public void toModelType_personWithInterview_returnsPersonWithInterview() throws Exception {
        Person personWithInterview = new PersonBuilder(BENSON).withInterview(VALID_INTERVIEW_BOB).build();
        JsonAdaptedPerson person = new JsonAdaptedPerson(personWithInterview);
        assertEquals(personWithInterview, person.toModelType());
    }

    @Test
    public void toModelType_nullInterview_returnsPersonWithNoInterview() throws Exception {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, null, VALID_STATUS, VALID_TAGS);
        Person expectedPerson = new PersonBuilder(BENSON).withInterview(Interview.NONE).build();
        assertEquals(expectedPerson, person.toModelType());
    }

    @Test
    public void toModelType_emptyInterview_returnsPersonWithNoInterview() throws Exception {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, "", VALID_STATUS, VALID_TAGS);
        Person expectedPerson = new PersonBuilder(BENSON).withInterview(Interview.NONE).build();
        assertEquals(expectedPerson, person.toModelType());
    }

    @Test
    public void toModelType_invalidInterviewFormat_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, INVALID_INTERVIEW_FORMAT, VALID_STATUS, VALID_TAGS);
        assertThrows(IllegalValueException.class, Interview.MESSAGE_INVALID_FORMAT, person::toModelType);
    }

    @Test
    public void toModelType_impossibleInterviewDateTime_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, INVALID_INTERVIEW_DATE_TIME, VALID_STATUS, VALID_TAGS);
        assertThrows(IllegalValueException.class, Interview.MESSAGE_INVALID_DATE_TIME, person::toModelType);
    }

    @Test
    public void toModelType_personWithStatus_returnsPersonWithStatus() throws Exception {
        Person hiredPerson = new PersonBuilder(BENSON).withStatus(Status.HIRED).build();
        JsonAdaptedPerson person = new JsonAdaptedPerson(hiredPerson);
        assertEquals(hiredPerson, person.toModelType());
    }

    @Test
    public void toModelType_nullStatus_returnsPersonWithDefaultStatus() throws Exception {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, null, VALID_TAGS);
        Person expectedPerson = new PersonBuilder(BENSON).withStatus(Person.DEFAULT_STATUS).build();
        assertEquals(expectedPerson, person.toModelType());
    }

    @Test
    public void toModelType_invalidStatus_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, INVALID_STATUS, VALID_TAGS);
        String expectedMessage = String.format(INVALID_STATUS_MESSAGE_FORMAT, INVALID_STATUS);
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_lowerCaseStatus_throwsIllegalValueException() {
        String lowerCaseStatus = Status.HIRED.name().toLowerCase();
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, lowerCaseStatus, VALID_TAGS);
        String expectedMessage = String.format(INVALID_STATUS_MESSAGE_FORMAT, lowerCaseStatus);
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(null, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, null, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_MISSING_AT;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, null, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, INVALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, null, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidRole_throwsIllegalValueException() {
        for (String title : new String[] {INVALID_ROLE, "Engineer@Platform"}) {
            JsonAdaptedPerson person =
                    new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, title,
                            VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
            assertThrows(IllegalValueException.class, RoleTitle.MESSAGE_CONSTRAINTS, person::toModelType);
        }
    }

    @Test
    public void toModelType_nullRole_throwsIllegalValueException() {
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, null,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, RoleTitle.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, person::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPerson person =
                new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS, VALID_ROLE,
                        VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, invalidTags);
        assertThrows(IllegalValueException.class, person::toModelType);
    }

    @Test
    public void toModelType_tagsDifferOnlyInCase_keepsOneTag() throws Exception {
        List<JsonAdaptedTag> tags = List.of(new JsonAdaptedTag("Referral"), new JsonAdaptedTag("referral"));
        JsonAdaptedPerson person = new JsonAdaptedPerson(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                VALID_ROLE, VALID_NOTE, VALID_INTERVIEW, VALID_STATUS, tags);

        assertEquals(1, person.toModelType().getTags().size());
    }

}
