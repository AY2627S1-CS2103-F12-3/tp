package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddRoleCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditRoleCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.RoleBuilder;

public class RoleTitlePunctuationIntegrationTest {
    private static final List<String> JOB_TITLES = List.of("C++ Developer", "C# Developer",
            "Engineer (Backend)", "Manager, Sales");

    @TempDir
    public Path testFolder;

    private final Model model = new ModelManager();
    private JsonAddressBookStorage storage;
    private Logic logic;

    @BeforeEach
    public void setUp() {
        storage = new JsonAddressBookStorage(testFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json"))));
    }

    @Test
    public void execute_commonJobTitles_addsOpenRolesAndPersistsExactTitles() throws Exception {
        for (String title : JOB_TITLES) {
            var result = logic.execute("role add r/" + title + " x/2 years");
            assertEquals(String.format(AddRoleCommand.MESSAGE_SUCCESS, title), result.getFeedbackToUser());
        }

        List<Role> expected = JOB_TITLES.stream().map(title -> new RoleBuilder().withTitle(title).build()).toList();
        assertEquals(expected, logic.getFilteredRoleList());
        assertEquals(expected, storage.readAddressBook().orElseThrow().getRoleList());
    }

    @Test
    public void execute_caseInsensitiveDuplicate_preservesDistinctPunctuationAndSavedData() throws Exception {
        logic.execute("role add r/C++ Developer x/2 years");
        logic.execute("role add r/C# Developer x/2 years");
        logic.execute("role add r/C Developer x/2 years");
        AddressBook before = new AddressBook(model.getAddressBook());
        byte[] original = Files.readAllBytes(storage.getAddressBookFilePath());

        assertThrows(CommandException.class, String.format(Messages.MESSAGE_DUPLICATE_ROLE, "c++ developer"), () ->
                logic.execute("role add r/  c++ developer  x/Senior"));
        logic.execute("list role");
        assertThrows(CommandException.class, String.format(Messages.MESSAGE_DUPLICATE_ROLE, "C++ Developer"), () ->
                logic.execute("role edit 2 r/C++ Developer"));

        assertEquals(3, model.getAddressBook().getRoleList().size());
        assertEquals(before, model.getAddressBook());
        assertArrayEquals(original, Files.readAllBytes(storage.getAddressBookFilePath()));
    }

    @Test
    public void execute_candidateReassignment_usesCanonicalPunctuatedTitlesAndPersistsDetails() throws Exception {
        for (String title : JOB_TITLES) {
            logic.execute("role add r/" + title + " x/2 years");
        }
        logic.execute("add n/Alice Tan p/91234567 e/alice@example.com a/Clementi r/c++ developer t/referral");
        logic.execute("note 1 Keep interview details");
        Person original = model.getAddressBook().getPersonList().getFirst();
        assertEquals(new RoleTitle("C++ Developer"), original.getRole());

        for (String title : JOB_TITLES) {
            Person expected = original.withRole(new RoleTitle(title));
            var result = logic.execute("edit 1 r/  " + title.toLowerCase(Locale.ROOT) + "  ");
            assertEquals(String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(expected)),
                    result.getFeedbackToUser());
            assertEquals(List.of(expected), logic.getFilteredPersonList());
            assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        }
    }

    @Test
    public void execute_roleRename_propagatesPunctuationToHiddenCandidateAndSurvivesReload() throws Exception {
        logic.execute("role add r/Software Engineer x/2 years");
        logic.execute("add n/Alice Tan p/91234567 e/alice@example.com a/Clementi r/Software Engineer t/referral");
        logic.execute("note 1 Keep interview details");
        Person original = model.getAddressBook().getPersonList().getFirst();
        logic.execute("find Nobody");
        logic.execute("list role");

        for (String title : JOB_TITLES) {
            var result = logic.execute("role edit 1 r/" + title);
            assertEquals(String.format(EditRoleCommand.MESSAGE_SUCCESS, title), result.getFeedbackToUser());
            assertEquals(new RoleTitle(title), logic.getFilteredRoleList().getFirst().getTitle());
            assertEquals(List.of(original.withRole(new RoleTitle(title))), model.getAddressBook().getPersonList());
            assertEquals(List.of(), logic.getFilteredPersonList());
            Model reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
            assertEquals(model.getAddressBook(), reloaded.getAddressBook());
        }
    }

    @Test
    public void execute_invalidPunctuatedTitle_preservesSavedData() throws Exception {
        logic.execute("role add r/C++ Developer x/2 years");
        logic.execute("list role");
        AddressBook before = new AddressBook(model.getAddressBook());
        byte[] original = Files.readAllBytes(storage.getAddressBookFilePath());

        for (String title : List.of("+#(),", "C++@Developer", "Engineer_(Backend)", "A".repeat(100) + "+")) {
            assertThrows(ParseException.class, RoleTitle.MESSAGE_CONSTRAINTS, () ->
                    logic.execute("role add r/" + title + " x/Senior"));
            assertThrows(ParseException.class, RoleTitle.MESSAGE_CONSTRAINTS, () ->
                    logic.execute("role edit 1 r/" + title));
            assertEquals(before, model.getAddressBook());
            assertArrayEquals(original, Files.readAllBytes(storage.getAddressBookFilePath()));
        }
    }
}
