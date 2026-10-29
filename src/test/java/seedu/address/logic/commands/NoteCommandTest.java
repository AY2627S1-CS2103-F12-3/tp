package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
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
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code NoteCommand}.
 */
public class NoteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_noteUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withNote(VALID_NOTE_AMY).build();
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY));

        String expectedMessage = String.format(NoteCommand.MESSAGE_ADD_NOTE_SUCCESS,
                editedPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personWithNote_noteReplaced() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithNote = new PersonBuilder(firstPerson).withNote(VALID_NOTE_BOB).build();
        model.setPerson(firstPerson, personWithNote);

        Person editedPerson = new PersonBuilder(personWithNote).withNote(VALID_NOTE_AMY).build();
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY));

        String expectedMessage = String.format(NoteCommand.MESSAGE_REPLACE_NOTE_SUCCESS,
                editedPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithNote, editedPerson);

        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sameNote_unchangedMessage() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithNote = new PersonBuilder(firstPerson).withNote(VALID_NOTE_AMY).build();
        model.setPerson(firstPerson, personWithNote);

        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY));

        // Nothing is lost, so the message does not say the note was replaced.
        String expectedMessage = String.format(NoteCommand.MESSAGE_UNCHANGED_NOTE_SUCCESS,
                personWithNote.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personWithStatus_statusPreserved() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person hiredPerson = new PersonBuilder(firstPerson).withStatus(Status.HIRED).build();
        model.setPerson(firstPerson, hiredPerson);

        Person editedPerson = new PersonBuilder(hiredPerson).withNote(VALID_NOTE_AMY).build();
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY));

        String expectedMessage = String.format(NoteCommand.MESSAGE_ADD_NOTE_SUCCESS,
                editedPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(hiredPerson, editedPerson);

        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personWithInterview_interviewPreserved() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithInterview = new PersonBuilder(firstPerson).withInterview(VALID_INTERVIEW_BOB).build();
        model.setPerson(firstPerson, personWithInterview);

        Person editedPerson = new PersonBuilder(personWithInterview).withNote(VALID_NOTE_AMY).build();
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY));

        String expectedMessage = String.format(NoteCommand.MESSAGE_ADD_NOTE_SUCCESS,
                editedPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithInterview, editedPerson);

        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_emptyNote_noteCleared() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithNote = new PersonBuilder(firstPerson).withNote(VALID_NOTE_BOB).build();
        model.setPerson(firstPerson, personWithNote);

        Person editedPerson = new PersonBuilder(personWithNote).withNote("").build();
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(""));

        String expectedMessage = String.format(NoteCommand.MESSAGE_CLEAR_NOTE_SUCCESS,
                editedPerson.getName(), VALID_NOTE_BOB);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithNote, editedPerson);

        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_emptyNoteOnPersonWithoutNote_noNoteToClearMessage() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(firstPerson.getNote().isEmpty());
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(""));

        String expectedMessage = String.format(NoteCommand.MESSAGE_NO_NOTE_TO_CLEAR, firstPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noteFilteredList_successAndFilterKept() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Person secondPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(secondPerson).withNote(VALID_NOTE_AMY).build();
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY));

        String expectedMessage = String.format(NoteCommand.MESSAGE_ADD_NOTE_SUCCESS,
                editedPerson.getName());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(secondPerson, editedPerson);
        showPersonAtIndex(expectedModel, INDEX_SECOND_PERSON);

        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_note_messageNamesCandidate() throws Exception {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String firstName = firstPerson.getName().toString();

        CommandResult addResult = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY)).execute(model);
        assertEquals("Added note to " + firstName + ".", addResult.getFeedbackToUser());

        CommandResult sameResult = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY)).execute(model);
        assertEquals("Updated note of " + firstName + ".", sameResult.getFeedbackToUser());

        CommandResult replaceResult = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_BOB)).execute(model);
        assertEquals("Replaced note of " + firstName + ".", replaceResult.getFeedbackToUser());

        CommandResult clearResult = new NoteCommand(INDEX_FIRST_PERSON, new Note("")).execute(model);
        assertEquals("Cleared note of " + firstName + ". Use undo to revert.\nPrevious note: " + VALID_NOTE_BOB,
                clearResult.getFeedbackToUser());

        CommandResult clearAgainResult = new NoteCommand(INDEX_FIRST_PERSON, new Note("")).execute(model);
        assertEquals(firstName + " has no note to clear.", clearAgainResult.getFeedbackToUser());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        int listSize = model.getFilteredPersonList().size();
        Index outOfBoundIndex = Index.fromOneBased(listSize + 1);
        NoteCommand noteCommand = new NoteCommand(outOfBoundIndex, new Note(VALID_NOTE_AMY));

        assertCommandFailure(noteCommand, model,
                String.format(Messages.MESSAGE_PERSON_INDEX_OUT_OF_RANGE, listSize + 1, listSize));
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        NoteCommand noteCommand = new NoteCommand(outOfBoundIndex, new Note(VALID_NOTE_AMY));

        assertCommandFailure(noteCommand, model, String.format(Messages.MESSAGE_PERSON_INDEX_OUT_OF_RANGE, 2, 1));
    }

    @Test
    public void execute_emptyDisplayedList_failure() {
        model.updateFilteredPersonList(person -> false);
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY));

        assertCommandFailure(noteCommand, model, String.format(Messages.MESSAGE_PERSON_INDEX_EMPTY_LIST, 1));
    }

    @Test
    public void equals() {
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY));

        // same object -> returns true
        assertTrue(noteCommand.equals(noteCommand));

        // same values -> returns true
        assertTrue(noteCommand.equals(new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY))));

        // different types -> returns false
        assertFalse(noteCommand.equals(new ClearCommand()));

        // null -> returns false
        assertFalse(noteCommand.equals(null));

        // different index -> returns false
        assertFalse(noteCommand.equals(new NoteCommand(INDEX_SECOND_PERSON, new Note(VALID_NOTE_AMY))));

        // different note -> returns false
        assertFalse(noteCommand.equals(new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_BOB))));
    }

    @Test
    public void toStringMethod() {
        Note note = new Note(VALID_NOTE_AMY);
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, note);
        String expected = NoteCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", note=" + note + "}";
        assertEquals(expected, noteCommand.toString());
    }
}
