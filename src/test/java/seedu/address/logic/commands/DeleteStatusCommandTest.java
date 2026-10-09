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
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteStatusCommand}.
 */
public class DeleteStatusCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_existingStatusUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithAcceptedStatus = new PersonBuilder(firstPerson).withTags("friends", "accepted").build();
        model.setPerson(firstPerson, personWithAcceptedStatus);
        Person editedPerson = new PersonBuilder(personWithAcceptedStatus).withTags("friends").build();
        DeleteStatusCommand deleteStatusCommand = new DeleteStatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED);

        String expectedMessage = String.format(DeleteStatusCommand.MESSAGE_DELETE_STATUS_SUCCESS, STATUS_ACCEPTED);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithAcceptedStatus, editedPerson);

        assertCommandSuccess(deleteStatusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_existingStatusFilteredList_success() {
        Person secondPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person personWithAcceptedStatus = new PersonBuilder(secondPerson)
                .withTags("owesMoney", "friends", "accepted").build();
        model.setPerson(secondPerson, personWithAcceptedStatus);
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withTags("owesMoney", "friends").build();
        DeleteStatusCommand deleteStatusCommand = new DeleteStatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED);

        String expectedMessage = String.format(DeleteStatusCommand.MESSAGE_DELETE_STATUS_SUCCESS, STATUS_ACCEPTED);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personInFilteredList, editedPerson);

        assertCommandSuccess(deleteStatusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_missingStatus_failure() {
        DeleteStatusCommand deleteStatusCommand = new DeleteStatusCommand(INDEX_FIRST_PERSON, STATUS_REJECTED);

        assertCommandFailure(deleteStatusCommand, model, DeleteStatusCommand.MESSAGE_STATUS_NOT_FOUND);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteStatusCommand deleteStatusCommand = new DeleteStatusCommand(outOfBoundIndex, STATUS_ACCEPTED);

        assertCommandFailure(deleteStatusCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertTrue(INDEX_SECOND_PERSON.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteStatusCommand deleteStatusCommand = new DeleteStatusCommand(INDEX_SECOND_PERSON, STATUS_ACCEPTED);

        assertCommandFailure(deleteStatusCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteStatusCommand standardCommand = new DeleteStatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED);

        assertTrue(standardCommand.equals(standardCommand));
        assertTrue(standardCommand.equals(new DeleteStatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED)));
        assertFalse(standardCommand.equals(null));
        assertFalse(standardCommand.equals(new ClearCommand()));
        assertFalse(standardCommand.equals(new DeleteStatusCommand(INDEX_SECOND_PERSON, STATUS_ACCEPTED)));
        assertFalse(standardCommand.equals(new DeleteStatusCommand(INDEX_FIRST_PERSON, STATUS_REJECTED)));
    }

    @Test
    public void toStringMethod() {
        DeleteStatusCommand deleteStatusCommand = new DeleteStatusCommand(INDEX_FIRST_PERSON, STATUS_ACCEPTED);
        String expected = DeleteStatusCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", targetStatus=" + STATUS_ACCEPTED + "}";

        assertEquals(expected, deleteStatusCommand.toString());
    }
}
