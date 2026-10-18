package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_ROLE_LIST_NOT_DISPLAYED;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ListChangeListener;
import seedu.address.logic.commands.EditRoleCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.EditRoleCommandParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Status;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;

public class EditRoleIntegrationTest {
    @TempDir
    public Path testFolder;

    private final Model model = new ModelManager();
    private JsonAddressBookStorage addressBookStorage;
    private Logic logic;

    @BeforeEach
    public void setUp() throws Exception {
        model.addRole(SOFTWARE_ENGINEER);
        model.addRole(PRODUCT_ANALYST);
        model.addRole(CLOSED_DESIGNER);
        model.addPerson(new PersonBuilder().withName("Alice").withNote("Keep interview details")
                .withStatus(Status.HIRED).withTags("referral").build());
        model.addPerson(new PersonBuilder().withName("Bob").withPhone("91234562")
                .withEmail("bob@example.com").withRole(CLOSED_DESIGNER.getTitle().value)
                .withStatus(Status.REJECTED).build());
        addressBookStorage = new JsonAddressBookStorage(testFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json"))));
        logic.execute("list role");
    }

    @Test
    public void execute_openAndClosedRoles_updatesObservableListAndSurvivesReload() throws Exception {
        AddressBook expected = new AddressBook(model.getAddressBook());
        Role editedOpen = new RoleBuilder(SOFTWARE_ENGINEER).withExperienceLevel("5 years").build();
        Role editedClosed = new RoleBuilder(CLOSED_DESIGNER).withExperienceLevel("Entry level").build();
        expected.setRole(SOFTWARE_ENGINEER, editedOpen);
        expected.setRole(CLOSED_DESIGNER, editedClosed);
        var observableRoles = logic.getFilteredRoleList();
        boolean[] wasListUpdated = {false};
        observableRoles.addListener((ListChangeListener<Role>) change -> wasListUpdated[0] = true);

        var result = logic.execute("role edit 1 x/ 5 years  ");
        logic.execute("role edit 3 x/Entry level");

        assertEquals("Edited role: Software Engineer.", result.getFeedbackToUser());
        assertTrue(result.isShowRoleList());
        assertTrue(model.isRoleListDisplayed());
        assertTrue(wasListUpdated[0]);
        assertSame(observableRoles, logic.getFilteredRoleList());
        assertEquals(List.of(editedOpen, PRODUCT_ANALYST, editedClosed), observableRoles);
        assertEquals(expected, model.getAddressBook());
        assertEquals(expected, addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_unchangedExperience_keepsSavedDataAndRoleView() throws Exception {
        byte[] original = Files.readAllBytes(addressBookStorage.getAddressBookFilePath());

        logic.execute("role edit 1 x/2 years");

        assertArrayEquals(original, Files.readAllBytes(addressBookStorage.getAddressBookFilePath()));
        assertTrue(model.isRoleListDisplayed());
    }

    @Test
    public void execute_invalidEdits_preservesDataFileAndFilters() throws Exception {
        logic.execute("find Alice");
        logic.execute("list role");
        AddressBook before = new AddressBook(model.getAddressBook());
        byte[] original = Files.readAllBytes(addressBookStorage.getAddressBookFilePath());

        assertRejected("role edit 1", ParseException.class, EditRoleCommandParser.MESSAGE_MISSING_FIELD);
        assertRejected("role edit 1 x/", ParseException.class, ExperienceLevel.MESSAGE_CONSTRAINTS);
        assertRejected("role edit 1 x/Junior x/Senior", ParseException.class,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EXPERIENCE_LEVEL));
        assertRejected("role edit 1 x/Senior r/New title", ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditRoleCommand.MESSAGE_USAGE));
        assertRejected("role edit 99 x/Senior", CommandException.class, MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);

        assertEquals(before, model.getAddressBook());
        assertArrayEquals(original, Files.readAllBytes(addressBookStorage.getAddressBookFilePath()));
        assertEquals(List.of(before.getPersonList().getFirst()), model.getFilteredPersonList());
        assertTrue(model.isRoleListDisplayed());
    }

    @Test
    public void execute_candidateCommandAfterListingRoles_blocksRoleEdit() throws Exception {
        logic.execute("find Alice");
        byte[] original = Files.readAllBytes(addressBookStorage.getAddressBookFilePath());
        AddressBook before = new AddressBook(model.getAddressBook());

        assertRejected("role edit 1 x/Senior", CommandException.class, MESSAGE_ROLE_LIST_NOT_DISPLAYED);

        assertEquals(before, model.getAddressBook());
        assertArrayEquals(original, Files.readAllBytes(addressBookStorage.getAddressBookFilePath()));
        assertEquals(List.of(before.getPersonList().getFirst()), model.getFilteredPersonList());
    }

    private void assertRejected(String command, Class<? extends Exception> exceptionType, String message) {
        assertThrows(exceptionType, message, () -> logic.execute(command));
    }
}
