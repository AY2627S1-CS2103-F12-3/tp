package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.copyModelWithPersonReplaced;
import static seedu.address.logic.commands.CommandTestUtil.replacePerson;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
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
        assertNoteSuccess(INDEX_FIRST_PERSON, VALID_NOTE_AMY, NoteCommand.MESSAGE_ADD_NOTE_SUCCESS);
    }

    @Test
    public void execute_personWithNote_noteReplaced() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withNote(VALID_NOTE_BOB));

        assertNoteSuccess(INDEX_FIRST_PERSON, VALID_NOTE_AMY, NoteCommand.MESSAGE_REPLACE_NOTE_SUCCESS);
    }

    @Test
    public void execute_sameNote_unchangedMessage() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withNote(VALID_NOTE_AMY));

        // Nothing is lost, so the message does not say the note was replaced.
        assertNoteSuccess(INDEX_FIRST_PERSON, VALID_NOTE_AMY, NoteCommand.MESSAGE_UNCHANGED_NOTE_SUCCESS);
    }

    @Test
    public void execute_personWithStatus_statusPreserved() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withStatus(Status.HIRED));

        assertNoteSuccess(INDEX_FIRST_PERSON, VALID_NOTE_AMY, NoteCommand.MESSAGE_ADD_NOTE_SUCCESS);
    }

    @Test
    public void execute_personWithInterview_interviewPreserved() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withInterview(VALID_INTERVIEW_BOB));

        assertNoteSuccess(INDEX_FIRST_PERSON, VALID_NOTE_AMY, NoteCommand.MESSAGE_ADD_NOTE_SUCCESS);
    }

    @Test
    public void execute_emptyNote_noteCleared() {
        replacePerson(model, INDEX_FIRST_PERSON, builder -> builder.withNote(VALID_NOTE_BOB));

        assertNoteSuccess(INDEX_FIRST_PERSON, "", NoteCommand.MESSAGE_CLEAR_NOTE_SUCCESS);
    }

    @Test
    public void execute_emptyNoteOnPersonWithoutNote_noNoteToClearMessage() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertTrue(firstPerson.getNote().isEmpty());

        assertNoteSuccess(INDEX_FIRST_PERSON, "", NoteCommand.MESSAGE_NO_NOTE_TO_CLEAR);
    }

    @Test
    public void execute_noteFilteredList_successAndFilterKept() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        assertNoteSuccess(INDEX_FIRST_PERSON, VALID_NOTE_AMY, NoteCommand.MESSAGE_ADD_NOTE_SUCCESS);
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_note_messageNamesCandidate() throws Exception {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        String firstName = firstPerson.getName().toString();

        CommandResult addResult =
                new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY)).execute(model);
        assertEquals("Added note to " + firstName + ".", addResult.getFeedbackToUser());

        CommandResult sameResult =
                new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_AMY)).execute(model);
        assertEquals("Updated note of " + firstName + ".", sameResult.getFeedbackToUser());

        CommandResult replaceResult =
                new NoteCommand(INDEX_FIRST_PERSON, new Note(VALID_NOTE_BOB)).execute(model);
        assertEquals("Replaced note of " + firstName + ".", replaceResult.getFeedbackToUser());

        CommandResult clearResult = new NoteCommand(INDEX_FIRST_PERSON, Note.NONE).execute(model);
        assertEquals("Cleared note of " + firstName + ". Use undo to revert.\nPrevious note: "
                + VALID_NOTE_BOB,
                clearResult.getFeedbackToUser());

        CommandResult clearAgainResult = new NoteCommand(INDEX_FIRST_PERSON, Note.NONE).execute(model);
        assertEquals(firstName + " has no note to clear.", clearAgainResult.getFeedbackToUser());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        NoteCommand noteCommand = new NoteCommand(outOfBoundIndex, new Note(VALID_NOTE_AMY));

        assertCommandFailure(noteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        NoteCommand noteCommand = new NoteCommand(outOfBoundIndex, new Note(VALID_NOTE_AMY));

        assertCommandFailure(noteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
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

    /**
     * Sets the note of the displayed candidate at {@code index} to {@code newNote}, and checks that only
     * their note changes, the current filter is kept, and the result message follows
     * {@code expectedMessageFormat}.
     * Like the messages in {@code NoteCommand}, the format takes the candidate's name and previous note.
     */
    private void assertNoteSuccess(Index index, String newNote, String expectedMessageFormat) {
        Person personToEdit = model.getFilteredPersonList().get(index.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withNote(newNote).build();
        NoteCommand noteCommand = new NoteCommand(index, new Note(newNote));

        String expectedMessage =
                String.format(expectedMessageFormat, personToEdit.getName(), personToEdit.getNote());

        Model expectedModel = copyModelWithPersonReplaced(model, personToEdit, editedPerson);
        assertCommandSuccess(noteCommand, model, expectedMessage, expectedModel);
    }
}
