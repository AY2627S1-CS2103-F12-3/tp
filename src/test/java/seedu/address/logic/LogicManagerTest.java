package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.HELP_TOPICS;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ROLE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddRoleCommand;
import seedu.address.logic.commands.CloseRoleCommand;
import seedu.address.logic.commands.CommandGuide;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteRoleCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.StatusCommand;
import seedu.address.logic.commands.UndoCommand;
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
    private static final String DELETE_EXTRA_TEXT_ERROR =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE)
                    + "\nThe delete command takes only a candidate index.";

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;
    private JsonAddressBookStorage addressBookStorage;

    @BeforeEach
    public void setUp() {
        addressBookStorage = new JsonAddressBookStorage(temporaryFolder.resolve("recruiterbuddy.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_undoRedoWithArguments_preservesDataFileAndHistory() throws Exception {
        logic.execute("role add r/Software Engineer x/2 years");
        String saved = Files.readString(addressBookStorage.getAddressBookFilePath());
        assertThrows(ParseException.class, () -> logic.execute("undo unexpected"));
        assertEquals(1, model.getAddressBook().getRoleList().size());
        assertEquals(saved, Files.readString(addressBookStorage.getAddressBookFilePath()));
        logic.execute("undo");
        saved = Files.readString(addressBookStorage.getAddressBookFilePath());
        assertThrows(ParseException.class, () -> logic.execute("redo unexpected"));
        assertTrue(model.getAddressBook().getRoleList().isEmpty());
        assertEquals(saved, Files.readString(addressBookStorage.getAddressBookFilePath()));
        logic.execute("redo");
        assertEquals(1, model.getAddressBook().getRoleList().size());
    }

    @Test
    public void execute_findCandidateStatus_combinesFieldsWithOrAndTracksChanges() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(new PersonBuilder(AMY).withStatus(Status.INTERVIEW).build());
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("Other Candidate").build());
        logic.execute("find st/interview");
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(Status.INTERVIEW, model.getFilteredPersonList().getFirst().getStatus());
        logic.execute("find Other st/INTERVIEW");
        assertEquals(2, model.getFilteredPersonList().size());
        logic.execute("find st/INTERVIEW");
        logic.execute("status 1 st/REJECTED");
        logic.execute("find st/INTERVIEW");
        assertTrue(model.getFilteredPersonList().isEmpty());
        logic.execute("undo");
        logic.execute("find st/INTERVIEW");
        assertEquals(1, model.getFilteredPersonList().size());
        logic.execute("redo");
        logic.execute("find st/REJECTED");
        assertEquals(1, model.getFilteredPersonList().size());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_unexpectedEditText_preservesSavedDataFiltersAndHistory() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(AMY);
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("Other Candidate").build());
        logic.execute("edit 1 n/Amy Corrected");
        logic.execute("undo");
        logic.execute("find Amy");
        ReadOnlyAddressBook saved = addressBookStorage.readAddressBook().orElseThrow();
        String savedText = Files.readString(addressBookStorage.getAddressBookFilePath());
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE)
                + "\nUnexpected text after the index. Use the edit fields shown above.";

        for (String command : List.of("edit 1 st/HIRED", "edit 1 st/HIRED n/Accidental Change", "edit 1 extra")) {
            assertThrows(ParseException.class, expectedMessage, () -> logic.execute(command));
            assertEquals(saved, model.getAddressBook());
            assertEquals(savedText, Files.readString(addressBookStorage.getAddressBookFilePath()));
            assertEquals(List.of(AMY), logic.getFilteredPersonList());
            assertEquals(List.of(SOFTWARE_ENGINEER), logic.getFilteredRoleList());
        }

        assertEquals(UndoCommand.MESSAGE_EMPTY, logic.execute("undo").getFeedbackToUser());
        logic.execute("redo");
        assertEquals("Amy Corrected", model.getAddressBook().getPersonList().getFirst().getName().fullName);
    }

    @Test
    public void execute_unexpectedEditText_doesNotCreateDataFile() {
        assertParseException("edit 1 st/HIRED", String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE)
                + "\nUnexpected text after the index. Use the edit fields shown above.");
        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, Messages.getErrorMessageForInvalidPersonIndex(
                seedu.address.commons.core.index.Index.fromOneBased(9), 0));
    }

    @Test
    public void execute_deleteWithExtraArguments_preservesSavedDataAndFilters() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addRole(PRODUCT_ANALYST);
        model.addPerson(AMY);
        model.addPerson(new PersonBuilder().withName("Hidden Candidate").withPhone("91234567")
                .withEmail("hidden@example.com").withRole("Product Analyst").build());
        addressBookStorage.saveAddressBook(model.getAddressBook());
        byte[] saved = Files.readAllBytes(addressBookStorage.getAddressBookFilePath());
        ReadOnlyAddressBook expected = addressBookStorage.readAddressBook().orElseThrow();
        model.updateFilteredPersonList(AMY::equals);
        model.updateFilteredRoleList(SOFTWARE_ENGINEER::equals);

        for (String command : List.of("delete 1 extra", "delete 1 2", "delete 1 n/Accidental Deletion")) {
            assertThrows(ParseException.class, DELETE_EXTRA_TEXT_ERROR, () -> logic.execute(command));
            assertEquals(expected, model.getAddressBook());
            assertArrayEquals(saved, Files.readAllBytes(addressBookStorage.getAddressBookFilePath()));
            assertEquals(List.of(AMY), model.getFilteredPersonList());
            assertEquals(List.of(SOFTWARE_ENGINEER), model.getFilteredRoleList());
        }
    }

    @Test
    public void execute_deleteWithExtraArguments_preservesUndoAndRedoHistory() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(AMY);
        logic.execute("edit 1 n/Amy Updated");

        assertParseException("delete 1 extra", DELETE_EXTRA_TEXT_ERROR);
        logic.execute("undo");
        assertEquals(List.of(AMY), model.getAddressBook().getPersonList());

        assertParseException("delete 1 extra", DELETE_EXTRA_TEXT_ERROR);
        logic.execute("redo");
        assertEquals("Amy Updated", model.getAddressBook().getPersonList().getFirst().getName().fullName);
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_deleteWithExtraArguments_doesNotCreateDataFile() {
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(AMY);
        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));

        assertParseException("delete 1 extra", DELETE_EXTRA_TEXT_ERROR);

        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));
        assertEquals(List.of(AMY), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_help_preservesRecruitmentDataFileAndFilters() throws Exception {
        GuiSettings guiSettings = new GuiSettings(1000, 700, 10, 20);
        logic.setGuiSettings(guiSettings);
        model.addRole(SOFTWARE_ENGINEER);
        model.addPerson(AMY);
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredRoleList(role -> false);
        addressBookStorage.saveAddressBook(model.getAddressBook());
        String saved = Files.readString(addressBookStorage.getAddressBookFilePath());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        List<String> topics = new ArrayList<>(HELP_TOPICS);
        topics.add("");
        for (String topic : topics) {
            CommandResult result = logic.execute("help " + topic);
            String expected = topic.isEmpty() ? CommandGuide.getOverview() : CommandGuide.findHelp(topic).orElseThrow();
            assertEquals(expected, result.getFeedbackToUser());
            assertTrue(result.isShowHelp());
            assertFalse(result.isExit());
            assertEquals(expectedModel.getAddressBook(), model.getAddressBook());
            assertTrue(model.getFilteredPersonList().isEmpty());
            assertTrue(model.getFilteredRoleList().isEmpty());
            assertEquals(List.of(AMY), logic.getPersonList());
            assertEquals(guiSettings, logic.getGuiSettings());
            assertEquals(saved, Files.readString(addressBookStorage.getAddressBookFilePath()));
        }
    }

    @Test
    public void execute_help_unavailableStorageIsNotUsed() throws Exception {
        StorageManager unavailableStorage = new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"))) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) {
                throw new AssertionError("Viewing help must not save recruitment data");
            }
        };
        Logic readOnlyLogic = new LogicManager(model, unavailableStorage);

        assertEquals(CommandGuide.getOverview(), readOnlyLogic.execute("HELP").getFeedbackToUser());
        assertEquals(CommandGuide.findHelp("role").orElseThrow(),
                readOnlyLogic.execute("HELP ROLE").getFeedbackToUser());
        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));
    }

    @Test
    public void execute_help_preservesUndoAndRedoHistory() throws Exception {
        logic.execute("role add r/Software Engineer x/2 years");
        logic.execute("help");
        logic.execute("help clear");
        logic.execute("help undo");
        logic.execute("undo");
        assertTrue(model.getAddressBook().getRoleList().isEmpty());

        logic.execute("help");
        logic.execute("help redo");
        logic.execute("help exit");
        logic.execute("redo");
        assertEquals(List.of(SOFTWARE_ENGINEER), model.getAddressBook().getRoleList());
    }

    @Test
    public void execute_invalidHelp_preservesSavedDataFiltersAndRedo() throws Exception {
        logic.execute("role add r/Software Engineer x/2 years");
        logic.execute("undo");
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredRoleList(role -> false);
        String saved = Files.readString(addressBookStorage.getAddressBookFilePath());

        assertThrows(CommandException.class,
                "No help is available for “NotACommand”. Use help to view all commands.", () ->
                        logic.execute("help NotACommand"));
        assertThrows(ParseException.class, "Help accepts at most one command name.", () ->
                logic.execute("help list role"));
        assertEquals(saved, Files.readString(addressBookStorage.getAddressBookFilePath()));
        assertTrue(model.getAddressBook().getRoleList().isEmpty());
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertTrue(model.getFilteredRoleList().isEmpty());

        logic.execute("redo");
        assertEquals(List.of(SOFTWARE_ENGINEER), model.getAddressBook().getRoleList());
    }

    @Test
    public void execute_undoRedoRestoresAndPersistsHistory() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + ROLE_DESC_AMY;

        logic.execute(addCommand);
        assertEquals(1, model.getAddressBook().getPersonList().size());

        logic.execute("list");
        logic.execute("undo");
        assertEquals(0, model.getAddressBook().getPersonList().size());
        assertEquals(0, addressBookStorage.readAddressBook().orElseThrow().getPersonList().size());

        logic.execute("redo");
        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_newChangeAfterUndo_clearsRedoHistory() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + ROLE_DESC_AMY;
        logic.execute(addCommand);
        logic.execute("undo");
        logic.execute(addCommand);
        logic.execute("redo");
        assertEquals(RedoCommand.MESSAGE_EMPTY, logic.execute("redo").getFeedbackToUser());
    }

    @Test
    public void execute_failedChangeAfterUndo_preservesRedoHistory() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + ROLE_DESC_AMY;
        logic.execute(addCommand);
        logic.execute("undo");

        assertCommandException("delete 9", Messages.getErrorMessageForInvalidPersonIndex(
                seedu.address.commons.core.index.Index.fromOneBased(9), 0));
        logic.execute("redo");
        assertEquals(1, model.getAddressBook().getPersonList().size());
    }

    @Test
    public void execute_successfulNoOpAfterUndo_preservesRedoHistory() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        logic.execute("add n/Alice Tan p/91234567 e/alice@example.com a/Bedok r/Software Engineer");
        logic.execute("edit 1 n/Alicia");
        logic.execute("undo");

        assertEquals("Status \"Resume\" assigned to: Alice Tan (alice@example.com)",
                logic.execute("status 1 st/RESUME").getFeedbackToUser());
        assertEquals("Redid the previous change.", logic.execute("redo").getFeedbackToUser());
        assertEquals("Alicia", model.getAddressBook().getPersonList().getFirst().getName().fullName);
    }

    @Test
    public void execute_tagSpellingChange_isUndoable() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        logic.execute("add n/Alice Tan p/91234567 e/alice@example.com a/Bedok "
                + "r/Software Engineer t/java");

        logic.execute("edit 1 t/JAVA");
        assertEquals("JAVA", model.getAddressBook().getPersonList().getFirst()
                .getTags().stream().findFirst().orElseThrow().tagName);

        logic.execute("undo");
        assertEquals("java", model.getAddressBook().getPersonList().getFirst()
                .getTags().stream().findFirst().orElseThrow().tagName);
    }

    @Test
    public void execute_unchangedEditWithManyTags_preservesRedoHistory() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        logic.execute("add n/Alice Tan p/91234567 e/alice@example.com a/Bedok "
                + "r/Software Engineer t/a1 t/b2 t/c3 t/d4 t/e5 t/f6 t/g7 t/h8 "
                + "t/i9 t/j0 t/k1 t/l2 t/m3 t/n4 t/o5 t/p6");
        logic.execute("edit 1 n/Alicia");
        logic.execute("undo");

        logic.execute("edit 1 n/Alice Tan");
        assertEquals("Redid the previous change.", logic.execute("redo").getFeedbackToUser());
        assertEquals("Alicia", model.getAddressBook().getPersonList().getFirst().getName().fullName);
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
        Person first = new PersonBuilder().withNameAndDistinctContacts("First Candidate").build();
        Person second = new PersonBuilder().withNameAndDistinctContacts("Second Candidate").build();
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
        Person first = new PersonBuilder().withNameAndDistinctContacts("First Candidate").build();
        Person second = new PersonBuilder().withNameAndDistinctContacts("Second Candidate").build();
        Person unselected = new PersonBuilder().withNameAndDistinctContacts("Unselected Candidate").build();
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
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("First Candidate").build());
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("Second Candidate").build());
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
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("First Candidate").build());
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("Second Candidate").build());
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
