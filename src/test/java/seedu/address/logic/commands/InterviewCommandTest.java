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
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Interview;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class InterviewCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        Interview interview = new Interview(VALID_INTERVIEW_AMY);
        assertThrows(NullPointerException.class, () ->
                new InterviewCommand(null, interview));
        assertThrows(NullPointerException.class, () ->
                new InterviewCommand(INDEX_FIRST_PERSON, null));
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
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        InterviewCommand interviewCommand = new InterviewCommand(outOfBoundIndex, new Interview(VALID_INTERVIEW_AMY));

        assertCommandFailure(interviewCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        InterviewCommand interviewCommand = new InterviewCommand(outOfBoundIndex, new Interview(VALID_INTERVIEW_AMY));

        assertCommandFailure(interviewCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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
