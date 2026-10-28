package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Interview;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.testutil.PersonBuilder;

public class InterviewCommandTest {

    /** Fixes today's date at 2026-10-28, so the far-future warning does not depend on when tests run. */
    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-10-28T00:00:00Z"), ZoneOffset.UTC);

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        Interview interview = new Interview(VALID_INTERVIEW_AMY);
        assertThrows(NullPointerException.class, () ->
                new InterviewCommand(null, interview));
        assertThrows(NullPointerException.class, () ->
                new InterviewCommand(INDEX_FIRST_PERSON, null));
        assertThrows(NullPointerException.class, () ->
                new InterviewCommand(INDEX_FIRST_PERSON, interview, null));
    }

    @Test
    public void execute_scheduleInterviewUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withInterview(VALID_INTERVIEW_AMY).build();
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY));

        String expectedMessage = String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                firstPerson.getName(), VALID_INTERVIEW_AMY);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_pastInterview_success() {
        String pastInterview = "2020-01-15 09:30";
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withInterview(pastInterview).build();
        InterviewCommand interviewCommand = new InterviewCommand(INDEX_FIRST_PERSON, new Interview(pastInterview));

        String expectedMessage = String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                firstPerson.getName(), pastInterview);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personWithInterview_interviewReplacedAndNotePreserved() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithInterview = new PersonBuilder(firstPerson).withNote(VALID_NOTE_BOB)
                .withInterview(VALID_INTERVIEW_BOB).build();
        model.setPerson(firstPerson, personWithInterview);

        Person editedPerson = new PersonBuilder(personWithInterview).withInterview(VALID_INTERVIEW_AMY).build();
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY));

        String expectedMessage = String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                firstPerson.getName(), VALID_INTERVIEW_AMY);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithInterview, editedPerson);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sameInterview_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithInterview = new PersonBuilder(firstPerson).withInterview(VALID_INTERVIEW_AMY).build();
        model.setPerson(firstPerson, personWithInterview);

        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY));

        String expectedMessage = String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                firstPerson.getName(), VALID_INTERVIEW_AMY);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearInterview_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithInterview = new PersonBuilder(firstPerson).withInterview(VALID_INTERVIEW_AMY).build();
        model.setPerson(firstPerson, personWithInterview);

        Person editedPerson = new PersonBuilder(personWithInterview).withInterview(Interview.NONE).build();
        InterviewCommand interviewCommand = new InterviewCommand(INDEX_FIRST_PERSON, Interview.NONE);

        String expectedMessage = String.format(InterviewCommand.MESSAGE_CLEAR_SUCCESS, firstPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithInterview, editedPerson);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearPersonWithoutInterview_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(firstPerson.getInterview().isEmpty());
        InterviewCommand interviewCommand = new InterviewCommand(INDEX_FIRST_PERSON, Interview.NONE);

        String expectedMessage = String.format(InterviewCommand.MESSAGE_CLEAR_SUCCESS, firstPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_successAndFilterKept() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Person secondPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(secondPerson).withInterview(VALID_INTERVIEW_AMY).build();
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY));

        String expectedMessage = String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                secondPerson.getName(), VALID_INTERVIEW_AMY);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(secondPerson, editedPerson);
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_interviewMoreThanAYearAhead_warnsButSaves() {
        String farInterview = "2027-10-29 09:00"; // one day past a year from the fixed date
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withInterview(farInterview).build();
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(farInterview), FIXED_CLOCK);

        String expectedMessage = String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                firstPerson.getName(), farInterview) + "\n"
                + String.format(InterviewCommand.MESSAGE_FAR_FUTURE_WARNING, "2027-10-29");

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_interviewExactlyAYearAhead_noWarning() {
        String yearAheadInterview = "2027-10-28 09:00";
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withInterview(yearAheadInterview).build();
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(yearAheadInterview), FIXED_CLOCK);

        String expectedMessage = String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                firstPerson.getName(), yearAheadInterview);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_upcomingInterviewForFinalStatus_warnsButSaves() {
        // VALID_INTERVIEW_BOB is after the fixed date
        assertFinalStatusInterview(VALID_INTERVIEW_BOB, true);
    }

    @Test
    public void execute_interviewTodayForFinalStatus_warnsButSaves() {
        // earlier in the day than the fixed clock, but still today
        assertFinalStatusInterview("2026-10-28 09:00", true);
    }

    @Test
    public void execute_pastInterviewForFinalStatus_noWarning() {
        // recording an interview that already happened, the day before the fixed date
        assertFinalStatusInterview("2026-10-27 09:00", false);
    }

    /**
     * Schedules {@code interviewDateTime} for a rejected candidate and checks the result message,
     * which has the final status note only if {@code isNoteExpected}.
     */
    private void assertFinalStatusInterview(String interviewDateTime, boolean isNoteExpected) {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person rejectedPerson = new PersonBuilder(firstPerson).withStatus(Status.REJECTED).build();
        model.setPerson(firstPerson, rejectedPerson);

        Person editedPerson = new PersonBuilder(rejectedPerson).withInterview(interviewDateTime).build();
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(interviewDateTime), FIXED_CLOCK);

        String expectedMessage = String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                firstPerson.getName(), interviewDateTime);
        if (isNoteExpected) {
            expectedMessage += "\n" + String.format(InterviewCommand.MESSAGE_FINAL_STATUS_WARNING,
                    firstPerson.getName(), Status.REJECTED);
        }

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(rejectedPerson, editedPerson);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearCandidateWithFinalStatus_noWarning() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person hiredPerson = new PersonBuilder(firstPerson).withStatus(Status.HIRED)
                .withInterview(VALID_INTERVIEW_AMY).build();
        model.setPerson(firstPerson, hiredPerson);

        Person editedPerson = new PersonBuilder(hiredPerson).withInterview(Interview.NONE).build();
        InterviewCommand interviewCommand = new InterviewCommand(INDEX_FIRST_PERSON, Interview.NONE, FIXED_CLOCK);

        String expectedMessage = String.format(InterviewCommand.MESSAGE_CLEAR_SUCCESS, firstPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(hiredPerson, editedPerson);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sameTimeAsHiddenCandidates_namesThem() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person thirdPerson = model.getFilteredPersonList().get(INDEX_THIRD_PERSON.getZeroBased());
        Person secondWithInterview = new PersonBuilder(secondPerson).withInterview(VALID_INTERVIEW_AMY).build();
        Person thirdWithInterview = new PersonBuilder(thirdPerson).withInterview(VALID_INTERVIEW_AMY).build();
        model.setPerson(secondPerson, secondWithInterview);
        model.setPerson(thirdPerson, thirdWithInterview);
        // Only the first candidate is shown, so the others are hidden by the filter.
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person editedPerson = new PersonBuilder(firstPerson).withInterview(VALID_INTERVIEW_AMY).build();
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY), FIXED_CLOCK);

        String expectedMessage = String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                firstPerson.getName(), VALID_INTERVIEW_AMY) + "\n"
                + String.format(InterviewCommand.MESSAGE_SAME_TIME,
                        secondPerson.getName() + ", " + thirdPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_severalWarnings_eachOnItsOwnLine() {
        String farInterview = "2062-10-20 14:00";
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person withdrawnPerson = new PersonBuilder(firstPerson).withStatus(Status.WITHDRAWN).build();
        Person secondWithInterview = new PersonBuilder(secondPerson).withInterview(farInterview).build();
        model.setPerson(firstPerson, withdrawnPerson);
        model.setPerson(secondPerson, secondWithInterview);

        Person editedPerson = new PersonBuilder(withdrawnPerson).withInterview(farInterview).build();
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(farInterview), FIXED_CLOCK);

        String expectedMessage = String.join("\n",
                String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS, firstPerson.getName(), farInterview),
                String.format(InterviewCommand.MESSAGE_FINAL_STATUS_WARNING, firstPerson.getName(),
                        Status.WITHDRAWN),
                String.format(InterviewCommand.MESSAGE_FAR_FUTURE_WARNING, "2062-10-20"),
                String.format(InterviewCommand.MESSAGE_SAME_TIME, secondPerson.getName()));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(withdrawnPerson, editedPerson);

        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        int listSize = model.getFilteredPersonList().size();
        Index outOfBoundIndex = Index.fromOneBased(listSize + 1);
        InterviewCommand interviewCommand = new InterviewCommand(outOfBoundIndex, new Interview(VALID_INTERVIEW_AMY));

        assertCommandFailure(interviewCommand, model,
                String.format(Messages.MESSAGE_PERSON_INDEX_OUT_OF_RANGE, listSize + 1, listSize));
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        InterviewCommand interviewCommand = new InterviewCommand(outOfBoundIndex, new Interview(VALID_INTERVIEW_AMY));

        assertCommandFailure(interviewCommand, model,
                String.format(Messages.MESSAGE_PERSON_INDEX_OUT_OF_RANGE, 2, 1));
    }

    @Test
    public void execute_emptyDisplayedList_failure() {
        model.updateFilteredPersonList(person -> false);
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY));

        assertCommandFailure(interviewCommand, model, String.format(Messages.MESSAGE_PERSON_INDEX_EMPTY_LIST, 1));
    }

    @Test
    public void equals() {
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY));

        // same object -> returns true
        assertTrue(interviewCommand.equals(interviewCommand));

        // same values -> returns true
        assertTrue(interviewCommand.equals(
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY))));

        // different clock -> returns true, as the clock is not part of the user's request
        assertTrue(interviewCommand.equals(
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY), FIXED_CLOCK)));

        // different types -> returns false
        assertFalse(interviewCommand.equals(new ClearCommand()));

        // null -> returns false
        assertFalse(interviewCommand.equals(null));

        // different index -> returns false
        assertFalse(interviewCommand.equals(
                new InterviewCommand(INDEX_SECOND_PERSON, new Interview(VALID_INTERVIEW_AMY))));

        // different interview -> returns false
        assertFalse(interviewCommand.equals(
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_BOB))));
        assertFalse(interviewCommand.equals(new InterviewCommand(INDEX_FIRST_PERSON, Interview.NONE)));
    }

    @Test
    public void toStringMethod() {
        Interview interview = new Interview(VALID_INTERVIEW_AMY);
        InterviewCommand interviewCommand = new InterviewCommand(INDEX_FIRST_PERSON, interview);
        String expected = InterviewCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", interview=" + interview + "}";
        assertEquals(expected, interviewCommand.toString());
    }
}
