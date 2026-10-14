package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_1;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_2;
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
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code StatusCommand}.
 */
public class StatusCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Updating a person's status preserves all other fields, including notes and tags.
     */
    @Test
    public void execute_specifiedUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withStatus(VALID_STATUS_1).build();
        StatusCommand statusCommand = new StatusCommand(INDEX_FIRST_PERSON, VALID_STATUS_1);

        String expectedMessage = String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, VALID_STATUS_1);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personWithStatusField_statusFieldReplaced() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithStatus = new PersonBuilder(firstPerson).withStatus(VALID_STATUS_1).build();
        model.setPerson(firstPerson, personWithStatus);

        Person editedPerson = new PersonBuilder(personWithStatus).withStatus(VALID_STATUS_2).build();
        StatusCommand statusCommand = new StatusCommand(INDEX_FIRST_PERSON, VALID_STATUS_2);
        String expectedMessage = String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, VALID_STATUS_2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithStatus, editedPerson);

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sameStatus_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        StatusCommand statusCommand = new StatusCommand(INDEX_FIRST_PERSON, firstPerson.getStatus());
        String expectedMessage = String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, firstPerson.getStatus());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Person editedPerson = new PersonBuilder(personInFilteredList).withStatus(VALID_STATUS_1).build();

        StatusCommand statusCommand = new StatusCommand(INDEX_FIRST_PERSON, VALID_STATUS_1);

        String expectedMessage = String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, VALID_STATUS_1);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personInFilteredList, editedPerson);

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        StatusCommand statusCommand = new StatusCommand(outOfBoundIndex, VALID_STATUS_1);

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

        StatusCommand statusCommand = new StatusCommand(outOfBoundIndex, VALID_STATUS_1);

        assertCommandFailure(statusCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final StatusCommand standardCommand = new StatusCommand(INDEX_FIRST_PERSON, VALID_STATUS_1);

        // same values -> returns true
        StatusCommand commandWithSameValues = new StatusCommand(INDEX_FIRST_PERSON, VALID_STATUS_1);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new StatusCommand(INDEX_SECOND_PERSON, VALID_STATUS_1)));

        // different status -> returns false
        assertFalse(standardCommand.equals(new StatusCommand(INDEX_FIRST_PERSON, VALID_STATUS_2)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        StatusCommand statusCommand = new StatusCommand(index, VALID_STATUS_1);
        String expected = StatusCommand.class.getCanonicalName() + "{index=" + index + ", status="
                + VALID_STATUS_1 + "}";
        assertEquals(expected, statusCommand.toString());
    }

}
