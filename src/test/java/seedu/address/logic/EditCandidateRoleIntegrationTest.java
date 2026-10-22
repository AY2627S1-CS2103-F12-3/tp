package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.StatusCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.role.RoleStatus;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;
import seedu.address.testutil.TypicalRoles;

public class EditCandidateRoleIntegrationTest {

    @TempDir
    public Path testFolder;

    private JsonAddressBookStorage storage;
    private Model model;
    private Logic logic;
    private Person alice;

    @BeforeEach
    public void setUp() throws Exception {
        AddressBook addressBook = TypicalRoles.getTypicalRoleAddressBook();
        addressBook.setRole(SOFTWARE_ENGINEER,
                new RoleBuilder(SOFTWARE_ENGINEER).withStatus(RoleStatus.CLOSED).build());
        alice = new PersonBuilder().withName("Alice").withNote("Keep interview details")
                .withTags("referral").withStatus(Status.HIRED).build();
        addressBook.addPerson(alice);
        storage = new JsonAddressBookStorage(testFolder.resolve("addressbook.json"));
        storage.saveAddressBook(addressBook);
        model = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_combinedTransfer_updatesObservableCardDataAndPersistsCanonicalRole() throws Exception {
        ObservableList<Person> displayed = logic.getFilteredPersonList();
        AtomicBoolean wasListUpdated = new AtomicBoolean();
        displayed.addListener((ListChangeListener<Person>) change -> wasListUpdated.set(true));
        Person expected = new PersonBuilder(alice).withName("Updated Alice").withRole("Product Analyst").build();

        CommandResult result = logic.execute("edit 1 n/Updated Alice r/  pRoDuCt AnAlYsT  ");

        assertEquals(String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(expected)),
                result.getFeedbackToUser());
        assertFalse(result.isShowRoleList());
        assertFalse(model.isRoleListDisplayed());
        assertSame(displayed, logic.getFilteredPersonList());
        assertTrue(wasListUpdated.get());
        assertEquals(List.of(expected), displayed);
        ReadOnlyAddressBook reloaded = storage.readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), reloaded);
        assertEquals(List.of(expected), new ModelManager(reloaded, new UserPrefs()).getFilteredPersonList());
        assertEquals(RoleStatus.CLOSED, reloaded.getRoleList().getFirst().getStatus());
        assertEquals(PRODUCT_ANALYST, reloaded.getRoleList().get(1));
    }

    @Test
    public void execute_invalidTransfers_preservesSavedBytesAndFilteredRecords() throws Exception {
        byte[] original = Files.readAllBytes(storage.getAddressBookFilePath());
        ReadOnlyAddressBook expected = storage.readAddressBook().orElseThrow();
        model.updateFilteredPersonList(alice::equals);

        for (String command : List.of("edit 1 r/Unknown Role", "edit 1 r/Product Designer",
                "edit 9 r/Product Analyst", "edit 1 n/Updated Alice r/Product Designer")) {
            assertThrows(CommandException.class, () -> logic.execute(command));
            assertArrayEquals(original, Files.readAllBytes(storage.getAddressBookFilePath()));
            assertEquals(expected, model.getAddressBook());
            assertEquals(List.of(alice), model.getFilteredPersonList());
        }
        assertThrows(ParseException.class, () -> logic.execute("edit 1 r/Product Analyst r/Software Engineer"));
        assertArrayEquals(original, Files.readAllBytes(storage.getAddressBookFilePath()));
        assertEquals(expected, model.getAddressBook());
    }

    @Test
    public void execute_transferThenHire_preventsSecondHireThroughStatusCommand() throws Exception {
        Person bob = new PersonBuilder().withName("Bob").withPhone("91234567").withEmail("bob@example.com")
                .withRole("Product Analyst").build();
        model.addPerson(bob);
        logic.execute("edit 1 r/Product Analyst");
        byte[] saved = Files.readAllBytes(storage.getAddressBookFilePath());

        CommandException exception = assertThrows(CommandException.class, () -> logic.execute("status 2 st/HIRED"));

        assertEquals(String.format(StatusCommand.MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE,
                PRODUCT_ANALYST.getTitle()), exception.getMessage());
        assertArrayEquals(saved, Files.readAllBytes(storage.getAddressBookFilePath()));
        assertEquals(bob, model.getAddressBook().getPersonList().get(1));
    }

    @Test
    public void execute_hiddenHire_preventsTransferAndPreservesSavedBytes() throws Exception {
        Person bob = new PersonBuilder().withName("Bob").withPhone("91234567").withEmail("bob@example.com")
                .withRole("Product Analyst").withStatus(Status.HIRED).build();
        model.addPerson(bob);
        logic.execute("list");
        byte[] saved = Files.readAllBytes(storage.getAddressBookFilePath());
        model.updateFilteredPersonList(alice::equals);

        assertThrows(CommandException.class, () -> logic.execute("edit 1 r/Product Analyst"));

        assertArrayEquals(saved, Files.readAllBytes(storage.getAddressBookFilePath()));
        assertEquals(List.of(alice), model.getFilteredPersonList());
        assertEquals(List.of(alice, bob), model.getAddressBook().getPersonList());
    }
}
