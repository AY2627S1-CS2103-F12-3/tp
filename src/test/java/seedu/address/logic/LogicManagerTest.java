package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_PERSON_LIST_NOT_DISPLAYED;
import static seedu.address.logic.Messages.MESSAGE_ROLE_LIST_NOT_DISPLAYED;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ROLE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddRoleCommand;
import seedu.address.logic.commands.CloseRoleCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteRoleCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.StatusCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;
    private JsonAddressBookStorage addressBookStorage;

    @BeforeEach
    public void setUp() {
        addressBookStorage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_addRoleCommand_roleAddedAndPersisted() throws Exception {
        Role role = new RoleBuilder().build();
        String addRoleCommand = "role add r/Software Engineer x/2 years";

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addRole(role);

        assertCommandSuccess(addRoleCommand,
                String.format(AddRoleCommand.MESSAGE_SUCCESS, role.getTitle()), expectedModel);
        assertEquals(List.of(role), logic.getFilteredRoleList());
        assertEquals(List.of(role), addressBookStorage.readAddressBook().orElseThrow().getRoleList());
    }

    @Test
    public void execute_closeRoleCommand_preservesCandidatesAndPersistsClosure() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        Person candidate = new PersonBuilder().withStatus(Status.HIRED).build();
        model.addPerson(candidate);
        logic.execute("list role");

        CommandResult result = logic.execute("role close 1");
        Role closedRole = new RoleBuilder(SOFTWARE_ENGINEER).withStatus(RoleStatus.CLOSED).build();
        assertEquals(String.format(CloseRoleCommand.MESSAGE_SUCCESS, SOFTWARE_ENGINEER.getTitle()),
                result.getFeedbackToUser());
        assertEquals(List.of(closedRole), logic.getFilteredRoleList());
        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(List.of(closedRole), saved.getRoleList());
        assertEquals(List.of(candidate), saved.getPersonList());

        String addCandidate = "add n/Another Candidate p/91234567 e/another@example.com"
                + " a/Clementi r/Software Engineer";
        assertThrows(CommandException.class, String.format(AddCommand.MESSAGE_ROLE_CLOSED,
                SOFTWARE_ENGINEER.getTitle()), () -> logic.execute(addCandidate));
        assertEquals(saved, model.getAddressBook());
        assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_closePendingRole_keepsSavedDataUnchanged() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(new PersonBuilder().build());
        logic.execute("list role");
        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();

        assertThrows(CommandException.class, String.format(CloseRoleCommand.MESSAGE_PENDING_CANDIDATES,
                SOFTWARE_ENGINEER.getTitle(), 1), () -> logic.execute("role close 1"));
        assertEquals(saved, model.getAddressBook());
        assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_deleteRoleCommand_persistsDeletionAndTitleReuse() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addRole(PRODUCT_ANALYST);
        Person candidate = new PersonBuilder().withRole(PRODUCT_ANALYST.getTitle().value).build();
        model.addPerson(candidate);
        logic.execute("list role");
        CommandResult result = logic.execute("role delete 1");
        assertEquals(String.format(DeleteRoleCommand.MESSAGE_SUCCESS, SOFTWARE_ENGINEER.getTitle()),
                result.getFeedbackToUser());
        assertEquals(List.of(PRODUCT_ANALYST), logic.getFilteredRoleList());
        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(List.of(PRODUCT_ANALYST), saved.getRoleList());
        assertEquals(List.of(candidate), saved.getPersonList());

        logic.execute("role add r/software engineer x/Senior");
        Role recreated = new RoleBuilder().withTitle("software engineer").withExperienceLevel("Senior").build();
        saved = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(List.of(PRODUCT_ANALYST, recreated), saved.getRoleList());
        assertEquals(List.of(candidate), saved.getPersonList());
    }

    @Test
    public void execute_deleteOccupiedRole_keepsSavedDataUnchanged() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(new PersonBuilder().withStatus(Status.REJECTED).build());
        logic.execute("list role");
        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();

        assertThrows(CommandException.class, String.format(DeleteRoleCommand.MESSAGE_ASSOCIATED_CANDIDATES,
                SOFTWARE_ENGINEER.getTitle(), 1), () -> logic.execute("role delete 1"));
        assertEquals(saved, model.getAddressBook());
        assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_secondHire_rejectsChangeAndPreservesSavedData() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        Person first = new PersonBuilder().withName("First Candidate").build();
        Person second = new PersonBuilder().withName("Second Candidate").build();
        model.addPerson(first);
        model.addPerson(second);
        logic.execute("status 1 st/HIRED");
        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(Status.HIRED, saved.getPersonList().getFirst().getStatus());
        assertEquals(List.of(SOFTWARE_ENGINEER), saved.getRoleList());

        assertThrows(CommandException.class, String.format(StatusCommand.MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE,
                SOFTWARE_ENGINEER.getTitle()), () -> logic.execute("status 2 st/HIRED"));
        assertEquals(saved, model.getAddressBook());
        assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_batchStatus_persistsAllSelectedCandidates() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        Person first = new PersonBuilder().withName("First Candidate").build();
        Person second = new PersonBuilder().withName("Second Candidate").build();
        Person unselected = new PersonBuilder().withName("Unselected Candidate").build();
        model.addPerson(first);
        model.addPerson(second);
        model.addPerson(unselected);

        logic.execute("status 2 1 2 st/REJECTED");

        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(List.of(new PersonBuilder(first).withStatus(Status.REJECTED).build(),
                new PersonBuilder(second).withStatus(Status.REJECTED).build(), unselected), saved.getPersonList());
        assertEquals(List.of(SOFTWARE_ENGINEER), saved.getRoleList());
        assertEquals(saved, model.getAddressBook());
    }

    @Test
    public void execute_invalidBatchStatus_preservesModelAndSavedData() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(new PersonBuilder().withName("First Candidate").build());
        model.addPerson(new PersonBuilder().withName("Second Candidate").build());
        logic.execute("list");
        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();

        String expectedMessage = String.format(StatusCommand.MESSAGE_CANDIDATES_HIRED_SAME_ROLE,
                SOFTWARE_ENGINEER.getTitle());
        assertThrows(CommandException.class, expectedMessage, () -> logic.execute("status 1 2 st/HIRED"));
        assertEquals(saved, model.getAddressBook());
        assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());

        assertThrows(CommandException.class, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                logic.execute("status 1 99 st/REJECTED"));
        assertEquals(saved, model.getAddressBook());
        assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_roleLifecycle_preservesHistoryUntilExplicitDeletion() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(new PersonBuilder().withName("First Candidate").build());
        model.addPerson(new PersonBuilder().withName("Second Candidate").build());
        logic.execute("status 1 st/HIRED");
        logic.execute("status 2 st/REJECTED");
        logic.execute("list role");
        logic.execute("role close 1");

        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(RoleStatus.CLOSED, saved.getRoleList().getFirst().getStatus());
        assertEquals(Status.HIRED, saved.getPersonList().getFirst().getStatus());
        assertEquals(Status.REJECTED, saved.getPersonList().get(1).getStatus());
        assertThrows(CommandException.class, String.format(DeleteRoleCommand.MESSAGE_ASSOCIATED_CANDIDATES,
                SOFTWARE_ENGINEER.getTitle(), 2), () -> logic.execute("role delete 1"));
        assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());

        logic.execute("list");
        logic.execute("delete 2");
        logic.execute("delete 1");
        logic.execute("list role");
        logic.execute("role delete 1");
        saved = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(List.of(), saved.getRoleList());
        assertEquals(List.of(), saved.getPersonList());
    }

    @Test
    public void execute_addCandidateAfterRoleList_rejectsRoleIndicesAndAllowsCandidateIndices() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addRole(PRODUCT_ANALYST);
        assertViewAfterCommand("list role", true);
        assertViewAfterCommand("add n/Test Candidate p/91234567 e/test@example.com"
                + " a/Clementi r/Product Analyst", false);
        assertEquals(1, model.getFilteredPersonList().size());

        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        for (String command : new String[]{"role close 1", "role delete 1"}) {
            assertThrows(CommandException.class, MESSAGE_ROLE_LIST_NOT_DISPLAYED, () -> logic.execute(command));
            assertEquals(saved, model.getAddressBook());
            assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());
            assertFalse(model.isRoleListDisplayed());
        }
        logic.execute("status 1 st/REJECTED");
        assertEquals(Status.REJECTED, model.getFilteredPersonList().getFirst().getStatus());
        logic.execute("delete 1");
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_candidateIndexCommandAfterRoleList_rejectsEvenWithCandidatesInModel() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(new PersonBuilder().build());
        assertViewAfterCommand("list role", true);
        assertEquals(1, model.getFilteredPersonList().size());

        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        for (String command : new String[]{"delete 1", "edit 1 n/Changed Name",
            "note 1 Updated", "status 1 st/HIRED"}) {
            assertThrows(CommandException.class, MESSAGE_PERSON_LIST_NOT_DISPLAYED, () -> logic.execute(command));
            assertEquals(saved, model.getAddressBook());
            assertTrue(model.isRoleListDisplayed());
        }
    }

    @Test
    public void execute_nonRoleResultsAfterRoleList_keepViewAndGuardInSync() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(new PersonBuilder().build());
        for (String command : new String[]{"list", "find Amy", "help", "role add r/New Role x/Senior", "clear"}) {
            assertViewAfterCommand("list role", true);
            assertViewAfterCommand(command, false);
            assertThrows(CommandException.class, MESSAGE_ROLE_LIST_NOT_DISPLAYED, () ->
                    logic.execute("role delete 1"));
        }
    }

    @Test
    public void execute_successfulRoleCommands_keepRoleView() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        assertViewAfterCommand("list role", true);
        assertViewAfterCommand("role close 1", true);
        assertViewAfterCommand("role delete 1", true);
        assertTrue(model.getAddressBook().getRoleList().isEmpty());
    }

    @Test
    public void execute_failedCommands_preserveRoleView() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        assertViewAfterCommand("list role", true);
        assertThrows(ParseException.class, () -> logic.execute("role close invalid"));
        assertTrue(model.isRoleListDisplayed());
        assertThrows(CommandException.class, () -> logic.execute("role close 2"));
        assertTrue(model.isRoleListDisplayed());
        assertViewAfterCommand("role close 1", true);
    }

    @Test
    public void execute_saveFailure_preservesPreviousView() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        assertViewAfterCommand("list role", true);
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(temporaryFolder.resolve("failure.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw DUMMY_IO_EXCEPTION;
            }
        };
        logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("failure-prefs.json"))));
        assertThrows(CommandException.class, () -> logic.execute("list"));
        assertTrue(model.isRoleListDisplayed());

        model.setRoleListDisplayed(false);
        assertThrows(CommandException.class, () -> logic.execute("list role"));
        assertFalse(model.isRoleListDisplayed());
    }

    @Test
    public void execute_pendingStatusAfterClosure_keepsRecordsAndSavedDataUnchanged() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(new PersonBuilder().withStatus(Status.REJECTED).withTags("referral").build());
        logic.execute("list role");
        logic.execute("role close 1");
        logic.execute("find Amy");
        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        Person candidate = saved.getPersonList().getFirst();

        for (Status pending : new Status[]{Status.RESUME, Status.INTERVIEW,
            Status.OFFER, Status.DECISION}) {
            assertThrows(CommandException.class, String.format(StatusCommand.MESSAGE_PENDING_STATUS_FOR_CLOSED_ROLE,
                    candidate.getName(), candidate.getRole()), () -> logic.execute("status 1 st/" + pending.name()));
            assertEquals(saved, model.getAddressBook());
            assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());
            assertEquals(List.of(candidate), model.getFilteredPersonList());
            assertFalse(model.isRoleListDisplayed());
        }
    }

    @Test
    public void execute_pendingCorrectionForOpenRole_persistsAndPreventsClosure() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(new PersonBuilder().withStatus(Status.HIRED).build());
        logic.execute("status 1 st/DECISION");
        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(Status.DECISION, saved.getPersonList().getFirst().getStatus());
        assertEquals(RoleStatus.OPEN, saved.getRoleList().getFirst().getStatus());
        logic.execute("list role");
        assertThrows(CommandException.class, String.format(CloseRoleCommand.MESSAGE_PENDING_CANDIDATES,
                SOFTWARE_ENGINEER.getTitle(), 1), () -> logic.execute("role close 1"));
        assertEquals(saved, addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    @Test
    public void getFilteredRoleList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredRoleList().remove(0));
    }

    private void assertViewAfterCommand(String command, boolean showRoles) throws Exception {
        CommandResult result = logic.execute(command);
        assertEquals(showRoles, result.isShowRoleList());
        assertEquals(showRoles, model.isRoleListDisplayed());
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        model.addRole(SOFTWARE_ENGINEER);
        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + ROLE_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addRole(SOFTWARE_ENGINEER);
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
