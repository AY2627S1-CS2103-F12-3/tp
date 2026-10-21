package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_ROLE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_ROLE_LIST_NOT_DISPLAYED;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPERIENCE_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE_TITLE;
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
import seedu.address.logic.commands.CloseRoleCommand;
import seedu.address.logic.commands.DeleteRoleCommand;
import seedu.address.logic.commands.EditRoleCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
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

        assertRejected("role edit 1", ParseException.class, EditRoleCommand.MESSAGE_NOT_EDITED);
        assertRejected("role edit 1 x/", ParseException.class, ExperienceLevel.MESSAGE_CONSTRAINTS);
        assertRejected("role edit 1 x/Junior x/Senior", ParseException.class,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EXPERIENCE_LEVEL));
        assertRejected("role edit 1 x/Senior r/", ParseException.class, RoleTitle.MESSAGE_CONSTRAINTS);
        assertRejected("role edit 1 r/Product Analyst x/Senior", CommandException.class,
                String.format(MESSAGE_DUPLICATE_ROLE, "Product Analyst"));
        assertRejected("role edit 1 r/Product Designer", CommandException.class,
                String.format(MESSAGE_DUPLICATE_ROLE, "Product Designer"));
        assertRejected("role edit 1 r/A r/B", ParseException.class,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ROLE_TITLE));
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

    @Test
    public void execute_rename_updatesHiddenCandidatesAndObservableListsAndSurvivesReload() throws Exception {
        Person hidden = new PersonBuilder().withNameAndDistinctContacts("Carol")
                .withRole(SOFTWARE_ENGINEER.getTitle().value)
                .withStatus(Status.WITHDRAWN).build();
        model.addPerson(hidden);
        List<Person> original = List.copyOf(model.getAddressBook().getPersonList());
        logic.execute("find Alice");
        logic.execute("list role");
        var observableRoles = logic.getFilteredRoleList();
        var observableCandidates = model.getAddressBook().getPersonList();
        boolean[] wasCandidateListUpdated = {false};
        observableCandidates.addListener((ListChangeListener<Person>) change -> wasCandidateListUpdated[0] = true);

        var result = logic.execute("role edit 1 r/Platform Engineer x/5 years");

        RoleTitle title = new RoleTitle("Platform Engineer");
        List<Person> expected = List.of(original.get(0).withRole(title), original.get(1), hidden.withRole(title));
        assertEquals("Edited role: Platform Engineer.", result.getFeedbackToUser());
        assertEquals(expected, observableCandidates);
        assertEquals(List.of(expected.getFirst()), model.getFilteredPersonList());
        assertSame(observableRoles, logic.getFilteredRoleList());
        assertSame(observableCandidates, model.getAddressBook().getPersonList());
        assertTrue(wasCandidateListUpdated[0]);
        assertTrue(model.isRoleListDisplayed());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
        assertTrue(model.findRole(SOFTWARE_ENGINEER.getTitle()).isEmpty());
        logic.execute("role add r/Software Engineer x/Entry level");
        assertEquals(4, model.getAddressBook().getRoleList().size());
        assertEquals(expected, model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_caseOnlyAndClosedRoleRename_preservesStatusAndSavesCanonicalTitles() throws Exception {
        logic.execute("role edit 1 r/software ENGINEER");
        logic.execute("role edit 3 r/Archived Designer");

        assertEquals(new RoleTitle("software ENGINEER"), model.getAddressBook().getPersonList().get(0).getRole());
        assertEquals(new RoleTitle("Archived Designer"), model.getAddressBook().getPersonList().get(1).getRole());
        assertEquals(Status.HIRED, model.getAddressBook().getPersonList().get(0).getStatus());
        assertEquals(Status.REJECTED, model.getAddressBook().getPersonList().get(1).getStatus());
        assertTrue(model.getAddressBook().getRoleList().get(2).isClosed());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_renamedRole_retainsHiringClosureAndDeletionGuards() throws Exception {
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("Carol").withStatus(Status.RESUME).build());
        logic.execute("role edit 1 r/Platform Engineer");
        logic.execute("list");

        assertRejected("status 3 HIRED", CommandException.class, "Platform Engineer already has a hired candidate.");
        logic.execute("list role");
        assertRejected("role close 1", CommandException.class,
                String.format(CloseRoleCommand.MESSAGE_PENDING_CANDIDATES, "Platform Engineer", 1));
        assertRejected("role delete 1", CommandException.class,
                String.format(DeleteRoleCommand.MESSAGE_ASSOCIATED_CANDIDATES, "Platform Engineer", 2));

        logic.execute("list");
        logic.execute("status 3 REJECTED");
        assertFalse(model.getAddressBook().getRoleList().getFirst().isClosed());
        logic.execute("list role");
        logic.execute("role close 1");

        assertTrue(model.getAddressBook().getRoleList().getFirst().isClosed());
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    private void assertRejected(String command, Class<? extends Exception> exceptionType, String message) {
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> displayedCandidates = List.copyOf(model.getFilteredPersonList());
        List<Role> displayedRoles = List.copyOf(model.getFilteredRoleList());
        assertThrows(exceptionType, message, () -> logic.execute(command));
        assertEquals(before, model.getAddressBook());
        assertEquals(displayedCandidates, model.getFilteredPersonList());
        assertEquals(displayedRoles, model.getFilteredRoleList());
    }
}
