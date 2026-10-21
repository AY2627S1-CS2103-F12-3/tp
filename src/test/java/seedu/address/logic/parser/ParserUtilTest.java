package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Interview;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Status;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.RoleTitle;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_ROLE_TITLE = "1234";
    private static final String INVALID_EXPERIENCE_LEVEL = "Senior\nLevel";
    private static final String INVALID_STATUS = "ghosted";
    private static final String INVALID_INTERVIEW_FORMAT = "20/10/2026 14:00";
    private static final String INVALID_INTERVIEW_DATE_TIME = "2026-02-30 14:00";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_INTERVIEW = "2026-10-20 14:00";
    private static final String VALID_NOTE = "Interview 2026-10-20 14:00, scored 8/10";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";
    private static final String VALID_ROLE_TITLE = "Software Engineer";
    private static final String VALID_EXPERIENCE_LEVEL = "2 years";
    private static final String VALID_STATUS_STRING = "INTERVIEW";
    private static final Status VALID_STATUS = Status.valueOf(VALID_STATUS_STRING);

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseRoleIndex_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRoleIndex(null, "Extra text."));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRoleIndex("1", null));
    }

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseNote_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseNote(null));
    }

    @Test
    public void parseNote_validValueWithoutWhitespace_returnsNote() {
        assertEquals(new Note(VALID_NOTE), ParserUtil.parseNote(VALID_NOTE));
    }

    @Test
    public void parseNote_validValueWithWhitespace_returnsTrimmedNote() {
        String noteWithWhitespace = WHITESPACE + VALID_NOTE + WHITESPACE;
        assertEquals(new Note(VALID_NOTE), ParserUtil.parseNote(noteWithWhitespace));
    }

    @Test
    public void parseNote_whitespaceOnly_returnsEmptyNote() {
        assertEquals(new Note(""), ParserUtil.parseNote(WHITESPACE));
    }

    @Test
    public void parseInterview_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseInterview(null));
    }

    @Test
    public void parseInterview_invalidFormat_throwsParseException() {
        assertThrows(ParseException.class, Interview.MESSAGE_INVALID_FORMAT, () ->
                ParserUtil.parseInterview(INVALID_INTERVIEW_FORMAT));
    }

    @Test
    public void parseInterview_whitespaceOnly_throwsParseException() {
        assertThrows(ParseException.class, Interview.MESSAGE_INVALID_FORMAT, () ->
                ParserUtil.parseInterview(WHITESPACE));
    }

    @Test
    public void parseInterview_impossibleDateTime_throwsParseException() {
        assertThrows(ParseException.class, Interview.MESSAGE_INVALID_DATE_TIME, () ->
                ParserUtil.parseInterview(INVALID_INTERVIEW_DATE_TIME));
    }

    @Test
    public void parseInterview_validValueWithWhitespace_returnsTrimmedInterview() throws Exception {
        String interviewWithWhitespace = WHITESPACE + VALID_INTERVIEW + WHITESPACE;
        assertEquals(new Interview(VALID_INTERVIEW), ParserUtil.parseInterview(interviewWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseRoleTitle_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRoleTitle(null));
    }

    @Test
    public void parseRoleTitle_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, RoleTitle.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseRoleTitle(INVALID_ROLE_TITLE));
    }

    @Test
    public void parseRoleTitle_validValueWithWhitespace_returnsTrimmedRoleTitle() throws Exception {
        String roleTitleWithWhitespace = WHITESPACE + VALID_ROLE_TITLE + WHITESPACE;
        assertEquals(new RoleTitle(VALID_ROLE_TITLE), ParserUtil.parseRoleTitle(roleTitleWithWhitespace));
    }

    @Test
    public void parseExperienceLevel_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseExperienceLevel(null));
    }

    @Test
    public void parseExperienceLevel_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, ExperienceLevel.MESSAGE_CONSTRAINTS, () ->
                ParserUtil.parseExperienceLevel(INVALID_EXPERIENCE_LEVEL));
    }

    @Test
    public void parseExperienceLevel_validValueWithWhitespace_returnsTrimmedExperienceLevel() throws Exception {
        String experienceLevelWithWhitespace = WHITESPACE + VALID_EXPERIENCE_LEVEL + WHITESPACE;
        assertEquals(new ExperienceLevel(VALID_EXPERIENCE_LEVEL),
                ParserUtil.parseExperienceLevel(experienceLevelWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseStatus_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStatus(null));
    }

    @Test
    public void parseStatus_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Status.getConstraintsMessage(), () ->
                ParserUtil.parseStatus(""));
        assertThrows(ParseException.class, Status.getConstraintsMessage(), () ->
                ParserUtil.parseStatus(WHITESPACE));
        assertThrows(ParseException.class, Status.getConstraintsMessage(), () ->
                ParserUtil.parseStatus(INVALID_STATUS));
    }

    @Test
    public void parseStatus_validValueWithoutWhitespace_returnsStatus() throws Exception {
        assertEquals(VALID_STATUS, ParserUtil.parseStatus(VALID_STATUS_STRING));
    }

    @Test
    public void parseStatus_validValueWithWhitespace_returnsStatus() throws Exception {
        assertEquals(VALID_STATUS, ParserUtil.parseStatus(WHITESPACE + VALID_STATUS_STRING + WHITESPACE));
    }

    @Test
    public void parseStatus_validValueWithDifferentCase_returnsStatus() throws Exception {
        assertEquals(VALID_STATUS, ParserUtil.parseStatus(VALID_STATUS_STRING.toLowerCase()));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
