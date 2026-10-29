package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.copyModelWithPersonReplaced;
import static seedu.address.logic.commands.CommandTestUtil.replacePerson;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Interview;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.testutil.PersonBuilder;

public class InterviewCommandTest {

    /** Fixes today's date at 2026-10-28, so date warnings do not depend on when tests run. */
    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-10-28T00:00:00Z"), ZoneOffset.UTC);
    /** Keeps the common valid interview dates in the future for tests that do not concern date warnings. */
    private static final Clock BEFORE_VALID_INTERVIEWS_CLOCK =
            Clock.fixed(Instant.parse("2026-10-01T00:00:00Z"), ZoneOffset.UTC);

    /** A fourth typical candidate, for tests that need more than two other candidates. */
    private static final Index INDEX_FOURTH_PERSON = Index.fromOneBased(4);

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
        assertScheduleSuccess(INDEX_FIRST_PERSON, VALID_INTERVIEW_AMY, BEFORE_VALID_INTERVIEWS_CLOCK);
    }

    @Test
    public void execute_pastInterview_warnsButSaves() {
        assertScheduleSuccess(INDEX_FIRST_PERSON, "2020-01-15 09:30", FIXED_CLOCK,
                InterviewCommand.MESSAGE_PAST_WARNING);
    }

    @Test
    public void execute_personWithInterview_interviewReplacedAndNotePreserved() {
        replacePerson(model, INDEX_FIRST_PERSON,
                builder -> builder.withNote(VALID_NOTE_BOB).withInterview(VALID_INTERVIEW_BOB));

        assertScheduleSuccess(INDEX_FIRST_PERSON, VALID_INTERVIEW_AMY, BEFORE_VALID_INTERVIEWS_CLOCK);
    }

    @Test
    public void execute_sameInterview_success() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withInterview(VALID_INTERVIEW_AMY));

        assertScheduleSuccess(INDEX_FIRST_PERSON, VALID_INTERVIEW_AMY, BEFORE_VALID_INTERVIEWS_CLOCK);
    }

    @Test
    public void execute_clearInterview_success() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withInterview(VALID_INTERVIEW_AMY));

        assertClearSuccess(INDEX_FIRST_PERSON);
    }

    @Test
    public void execute_clearPersonWithoutInterview_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(firstPerson.getInterview().isEmpty());

        assertClearSuccess(INDEX_FIRST_PERSON);
    }

    @Test
    public void execute_filteredList_successAndFilterKept() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        assertScheduleSuccess(INDEX_FIRST_PERSON, VALID_INTERVIEW_AMY, BEFORE_VALID_INTERVIEWS_CLOCK);
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_interviewMoreThanAYearAhead_warnsButSaves() {
        // one day past a year from the fixed date
        assertScheduleSuccess(INDEX_FIRST_PERSON, "2027-10-29 09:00", FIXED_CLOCK,
                InterviewCommand.MESSAGE_FAR_FUTURE_WARNING);
    }

    @Test
    public void execute_interviewExactlyAYearAhead_noWarning() {
        assertScheduleSuccess(INDEX_FIRST_PERSON, "2027-10-28 09:00", FIXED_CLOCK);
    }

    @Test
    public void execute_upcomingInterviewForFinalStatus_warnsButSaves() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withStatus(Status.REJECTED));

        // VALID_INTERVIEW_BOB is after the fixed date
        assertScheduleSuccess(INDEX_FIRST_PERSON, VALID_INTERVIEW_BOB, FIXED_CLOCK,
                String.format(InterviewCommand.MESSAGE_FINAL_STATUS_WARNING, Status.REJECTED));
    }

    @Test
    public void execute_interviewTodayForFinalStatus_warnsButSaves() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withStatus(Status.REJECTED));

        // earlier in the day than the fixed clock, but still today
        assertScheduleSuccess(INDEX_FIRST_PERSON, "2026-10-28 09:00", FIXED_CLOCK,
                String.format(InterviewCommand.MESSAGE_FINAL_STATUS_WARNING, Status.REJECTED));
    }

    @Test
    public void execute_pastInterviewForFinalStatus_noFinalStatusWarning() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withStatus(Status.REJECTED));

        // recording an interview that already happened, the day before the fixed date
        assertScheduleSuccess(INDEX_FIRST_PERSON, "2026-10-27 09:00", FIXED_CLOCK,
                InterviewCommand.MESSAGE_PAST_WARNING);
    }

    @Test
    public void execute_clearCandidateWithFinalStatus_noWarning() {
        replacePerson(model, INDEX_FIRST_PERSON,
                builder -> builder.withStatus(Status.HIRED).withInterview(VALID_INTERVIEW_AMY));

        assertClearSuccess(INDEX_FIRST_PERSON);
    }

    @Test
    public void execute_sameTimeAsHiddenCandidates_namesThem() {
        Person secondPerson = replacePerson(model, INDEX_SECOND_PERSON,
                builder -> builder.withInterview(VALID_INTERVIEW_AMY));
        Person thirdPerson = replacePerson(model, INDEX_THIRD_PERSON,
                builder -> builder.withInterview(VALID_INTERVIEW_AMY));
        // Only the first candidate is shown, so the others are hidden by the filter.
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        assertScheduleSuccess(INDEX_FIRST_PERSON, VALID_INTERVIEW_AMY, BEFORE_VALID_INTERVIEWS_CLOCK,
                String.format(InterviewCommand.MESSAGE_SAME_TIME,
                        secondPerson.getName() + ", " + thirdPerson.getName()));
    }

    @Test
    public void execute_moreThanTwoSameTimeCandidates_limitsAndTruncatesNames() {
        String longName = "A".repeat(40);
        replacePerson(model, INDEX_SECOND_PERSON,
                builder -> builder.withName(longName).withInterview(VALID_INTERVIEW_AMY));
        Person thirdPerson = replacePerson(model, INDEX_THIRD_PERSON,
                builder -> builder.withInterview(VALID_INTERVIEW_AMY));
        replacePerson(model, INDEX_FOURTH_PERSON, builder -> builder.withInterview(VALID_INTERVIEW_AMY));

        String displayedNames = "A".repeat(29) + "…, " + thirdPerson.getName() + " (+1 others)";
        assertScheduleSuccess(INDEX_FIRST_PERSON, VALID_INTERVIEW_AMY, BEFORE_VALID_INTERVIEWS_CLOCK,
                String.format(InterviewCommand.MESSAGE_SAME_TIME, displayedNames));
    }

    @Test
    public void execute_pastInterviewWithSameTimeCandidate_showsBothWarnings() {
        String pastInterview = "2026-10-27 09:00";
        Person secondPerson = replacePerson(model, INDEX_SECOND_PERSON,
                builder -> builder.withInterview(pastInterview));

        assertScheduleSuccess(INDEX_FIRST_PERSON, pastInterview, FIXED_CLOCK,
                InterviewCommand.MESSAGE_PAST_WARNING,
                String.format(InterviewCommand.MESSAGE_SAME_TIME, secondPerson.getName()));
    }

    @Test
    public void execute_severalWarnings_eachOnItsOwnLine() {
        String farInterview = "2062-10-20 14:00";
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withStatus(Status.WITHDRAWN));
        Person secondPerson = replacePerson(model, INDEX_SECOND_PERSON,
                builder -> builder.withInterview(farInterview));

        assertScheduleSuccess(INDEX_FIRST_PERSON, farInterview, FIXED_CLOCK,
                String.format(InterviewCommand.MESSAGE_FINAL_STATUS_WARNING, Status.WITHDRAWN),
                InterviewCommand.MESSAGE_FAR_FUTURE_WARNING,
                String.format(InterviewCommand.MESSAGE_SAME_TIME, secondPerson.getName()));
    }

    /**
     * Schedules {@code interviewDateTime} for the displayed candidate at {@code index}, with {@code clock}
     * giving today's date, and checks that only their interview changes, the current filter is kept, and
     * the success message is followed by {@code expectedWarnings}, one per line.
     */
    private void assertScheduleSuccess(Index index, String interviewDateTime, Clock clock,
            String... expectedWarnings) {
        Person personToEdit = model.getFilteredPersonList().get(index.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withInterview(interviewDateTime).build();
        InterviewCommand interviewCommand =
                new InterviewCommand(index, new Interview(interviewDateTime), clock);

        List<String> expectedLines = new ArrayList<>();
        expectedLines.add(String.format(InterviewCommand.MESSAGE_SCHEDULE_SUCCESS,
                personToEdit.getName(), interviewDateTime));
        expectedLines.addAll(List.of(expectedWarnings));
        String expectedMessage = String.join("\n", expectedLines);

        Model expectedModel = copyModelWithPersonReplaced(model, personToEdit, editedPerson);
        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Clears the interview of the displayed candidate at {@code index}, and checks that only their interview
     * changes and the result message is the clear success message with no warnings.
     */
    private void assertClearSuccess(Index index) {
        Person personToEdit = model.getFilteredPersonList().get(index.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withInterview(Interview.NONE).build();
        InterviewCommand interviewCommand = new InterviewCommand(index, Interview.NONE, FIXED_CLOCK);

        String expectedMessage =
                String.format(InterviewCommand.MESSAGE_CLEAR_SUCCESS, personToEdit.getName());

        Model expectedModel = copyModelWithPersonReplaced(model, personToEdit, editedPerson);
        assertCommandSuccess(interviewCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        int listSize = model.getFilteredPersonList().size();
        Index outOfBoundIndex = Index.fromOneBased(listSize + 1);
        InterviewCommand interviewCommand =
                new InterviewCommand(outOfBoundIndex, new Interview(VALID_INTERVIEW_AMY));

        assertCommandFailure(interviewCommand, model,
                String.format(Messages.MESSAGE_PERSON_INDEX_OUT_OF_RANGE, listSize + 1, listSize));
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        InterviewCommand interviewCommand =
                new InterviewCommand(outOfBoundIndex, new Interview(VALID_INTERVIEW_AMY));

        assertCommandFailure(interviewCommand, model,
                String.format(Messages.MESSAGE_PERSON_INDEX_OUT_OF_RANGE, 2, 1));
    }

    @Test
    public void execute_emptyDisplayedList_failure() {
        model.updateFilteredPersonList(person -> false);
        InterviewCommand interviewCommand =
                new InterviewCommand(INDEX_FIRST_PERSON, new Interview(VALID_INTERVIEW_AMY));

        assertCommandFailure(interviewCommand, model,
                String.format(Messages.MESSAGE_PERSON_INDEX_EMPTY_LIST, 1));
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
