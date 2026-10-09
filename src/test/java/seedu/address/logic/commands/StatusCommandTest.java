package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.STATUS_ACCEPTED;
import static seedu.address.logic.commands.CommandTestUtil.STATUS_REJECTED;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for statusCommand.
 */
public class StatusCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Adding status to person preserves existing tags.
     */
    @Test
    public void execute_specifiedUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        Set<Tag> tags = new HashSet<>(firstPerson.getTags());
        tags.add(STATUS_ACCEPTED);

        String[] tagsArray = tags.stream()
                .map(x -> x.toString())
                .map(s -> s.replace("[", "").replace("]", ""))
                .toArray(String[]::new);
        Person editedPerson = new PersonBuilder(firstPerson).withTags(tagsArray).build();

        StatusCommand statusCommand = new StatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED);

        String expectedMessage = String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, STATUS_ACCEPTED);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Set<Tag> tags = new HashSet<>(personInFilteredList.getTags());
        tags.add(STATUS_ACCEPTED);

        String[] tagsArray = tags.stream()
                .map(x -> x.toString())
                .map(s -> s.replace("[", "").replace("]", ""))
                .toArray(String[]::new);
        Person editedPerson = new PersonBuilder(personInFilteredList).withTags(tagsArray).build();

        StatusCommand statusCommand = new StatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED);

        String expectedMessage = String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, STATUS_ACCEPTED);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        StatusCommand statusCommand = new StatusCommand(outOfBoundIndex, STATUS_ACCEPTED);

        assertCommandFailure(statusCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        StatusCommand statusCommand = new StatusCommand(outOfBoundIndex, STATUS_ACCEPTED);

        assertCommandFailure(statusCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final StatusCommand standardCommand = new StatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED);

        // same values -> returns true
        StatusCommand commandWithSameValues = new StatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new StatusCommand(INDEX_SECOND_PERSON, STATUS_ACCEPTED)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new StatusCommand(INDEX_FIRST_PERSON, STATUS_REJECTED)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        StatusCommand statusCommand = new StatusCommand(index, STATUS_ACCEPTED);
        String expected = StatusCommand.class.getCanonicalName() + "{index=" + index + ", status="
                + STATUS_ACCEPTED + "}";
        assertEquals(expected, statusCommand.toString());
    }

}
