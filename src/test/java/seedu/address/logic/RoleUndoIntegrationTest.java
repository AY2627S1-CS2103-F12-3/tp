package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.DeleteRoleCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Status;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;

/**
 * Verifies session-scoped undo and redo of role changes through parsing, logic, model and storage.
 */
public class RoleUndoIntegrationTest {
    @TempDir
    public Path testFolder;

    private final Model model = new ModelManager();
    private JsonAddressBookStorage addressBookStorage;
    private StorageManager storage;
    private Logic logic;

    @BeforeEach
    public void setUp() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addRole(PRODUCT_ANALYST);
        model.addRole(CLOSED_DESIGNER);
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("Alice")
                .withStatus(Status.HIRED).withNote("Keep interview details").withTags("referral")
                .withInterview("2026-10-20 14:30").build());
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("Bob")
                .withStatus(Status.REJECTED).build());
        addressBookStorage = new JsonAddressBookStorage(testFolder.resolve("addressbook.json"));
        storage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
        logic = new LogicManager(model, storage);
        logic.execute("list role");
    }

    @Test
    public void execute_closeUndoRedo_restoresRoleStatusAndPreservesCandidates() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        AddressBook closed = new AddressBook(before);
        closed.setRole(SOFTWARE_ENGINEER,
                new RoleBuilder(SOFTWARE_ENGINEER).withStatus(RoleStatus.CLOSED).build());
        var observableRoles = logic.getFilteredRoleList();
        var observableCandidates = model.getAddressBook().getPersonList();

        logic.execute("role close 1");
        assertSavedState(closed);
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        logic.execute("list role");
        assertSavedState(before);
        assertEquals(RedoCommand.MESSAGE_SUCCESS, logic.execute("redo").getFeedbackToUser());
        logic.execute("list role");
        assertSavedState(closed);
        assertSame(observableRoles, logic.getFilteredRoleList());
        assertSame(observableCandidates, model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_deleteOpenRoleUndoRedo_restoresRoleDetailsAndPosition() throws Exception {
        assertDeletedRoleCanBeRestored(RoleStatus.OPEN);
    }

    @Test
    public void execute_deleteClosedRoleUndoRedo_restoresRoleDetailsAndPosition() throws Exception {
        assertDeletedRoleCanBeRestored(RoleStatus.CLOSED);
    }

    @Test
    public void execute_laterRoleChange_undoesChangesInReverseOrder() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        AddressBook closed = new AddressBook(before);
        closed.setRole(PRODUCT_ANALYST,
                new RoleBuilder(PRODUCT_ANALYST).withStatus(RoleStatus.CLOSED).build());

        logic.execute("role close 2");
        logic.execute("role edit 1 x/Senior");
        logic.execute("undo");
        logic.execute("list role");
        assertSavedState(closed);
        logic.execute("undo");
        logic.execute("list role");
        assertSavedState(before);
    }

    @Test
    public void execute_newChangeAfterUndo_clearsRedoHistory() throws Exception {
        logic.execute("role delete 2");
        logic.execute("undo");
        logic.execute("role add r/QA Designer x/Senior");
        AddressBook beforeRedo = new AddressBook(model.getAddressBook());

        assertEquals(RedoCommand.MESSAGE_EMPTY, logic.execute("redo").getFeedbackToUser());

        assertSavedState(beforeRedo);
    }

    @Test
    public void execute_failedRoleChange_doesNotConsumeUndoHistory() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        logic.execute("role close 1");

        String message = String.format(DeleteRoleCommand.MESSAGE_ASSOCIATED_CANDIDATES, "Software Engineer", 2);
        assertThrows(CommandException.class, message, () -> logic.execute("role delete 1"));
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());

        assertSavedState(before);
    }

    @Test
    public void execute_restartAfterClosure_keepsSavedDataButNotHistory() throws Exception {
        assertRoleChangeSurvivesRestartWithoutHistory("role close 2");
    }

    @Test
    public void execute_restartAfterDeletion_keepsSavedDataButNotHistory() throws Exception {
        assertRoleChangeSurvivesRestartWithoutHistory("role delete 2");
    }

    private void assertDeletedRoleCanBeRestored(RoleStatus status) throws Exception {
        Role emptyRole = new RoleBuilder(PRODUCT_ANALYST).withStatus(status).build();
        model.setRole(PRODUCT_ANALYST, emptyRole);
        logic.execute("list role");
        AddressBook before = new AddressBook(model.getAddressBook());
        AddressBook deleted = new AddressBook(before);
        deleted.removeRole(emptyRole);

        logic.execute("role delete 2");
        assertSavedState(deleted);
        assertEquals(UndoCommand.MESSAGE_SUCCESS, logic.execute("undo").getFeedbackToUser());
        logic.execute("list role");
        assertSavedState(before);
        assertEquals(emptyRole, logic.getFilteredRoleList().get(1));
        assertEquals(RedoCommand.MESSAGE_SUCCESS, logic.execute("redo").getFeedbackToUser());
        logic.execute("list role");
        assertSavedState(deleted);
    }

    private void assertRoleChangeSurvivesRestartWithoutHistory(String command) throws Exception {
        logic.execute(command);
        AddressBook saved = new AddressBook(model.getAddressBook());
        Model restartedModel = new ModelManager(addressBookStorage.readAddressBook().orElseThrow(), new UserPrefs());
        Logic restartedLogic = new LogicManager(restartedModel, storage);

        assertEquals(UndoCommand.MESSAGE_EMPTY, restartedLogic.execute("undo").getFeedbackToUser());
        assertEquals(RedoCommand.MESSAGE_EMPTY, restartedLogic.execute("redo").getFeedbackToUser());

        assertEquals(saved, restartedModel.getAddressBook());
        assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());
    }

    private void assertSavedState(ReadOnlyAddressBook expected) throws Exception {
        assertEquals(expected, model.getAddressBook());
        assertEquals(expected, addressBookStorage.readAddressBook().orElseThrow());
    }
}
