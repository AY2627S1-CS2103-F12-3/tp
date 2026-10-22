package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ListChangeListener;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleCandidateCounts;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class RoleCandidateCountsIntegrationTest {
    @TempDir
    public Path testFolder;

    private final Model model = new ModelManager();
    private Logic logic;
    private JsonAddressBookStorage addressBookStorage;

    @BeforeEach
    public void setUp() {
        model.addRole(SOFTWARE_ENGINEER);
        model.addRole(PRODUCT_ANALYST);
        model.addRole(CLOSED_DESIGNER);
        model.addPerson(new PersonBuilder().withName("Alice").build());
        model.addPerson(new PersonBuilder().withName("Bob").withPhone("91234562")
                .withEmail("bob@example.com").withStatus(Status.REJECTED).build());
        model.addPerson(new PersonBuilder().withName("Carol").withPhone("91234563")
                .withEmail("carol@example.com").withRole(CLOSED_DESIGNER.getTitle().value)
                .withStatus(Status.WITHDRAWN).build());
        addressBookStorage = new JsonAddressBookStorage(testFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json"))));
    }

    @Test
    public void getPersonList_filteredCandidates_keepsUnmodifiableFullSourceAndDoesNotChangeView() throws Exception {
        var candidates = logic.getPersonList();
        AtomicInteger changes = new AtomicInteger();
        candidates.addListener((ListChangeListener<Person>) change -> changes.incrementAndGet());

        logic.execute("find Alice");

        assertEquals(1, logic.getFilteredPersonList().size());
        assertEquals(3, candidates.size());
        assertEquals(0, changes.get());
        assertCounts(SOFTWARE_ENGINEER, 1, 0, 1, 0);
        assertCounts(PRODUCT_ANALYST, 0, 0, 0, 0);
        assertCounts(CLOSED_DESIGNER, 0, 0, 0, 1);
        assertFalse(model.isRoleListDisplayed());
        assertSame(candidates, logic.getPersonList());
        assertThrows(UnsupportedOperationException.class, () -> candidates.removeFirst());
    }

    @Test
    public void execute_statusAddDeleteAndClear_updatesObservableSourceAndCounts() throws Exception {
        AtomicInteger observedHires = new AtomicInteger();
        AtomicInteger changes = new AtomicInteger();
        logic.getPersonList().addListener((ListChangeListener<Person>) change -> {
            changes.incrementAndGet();
            RoleCandidateCounts counts = new RoleCandidateCounts(SOFTWARE_ENGINEER.getTitle(), logic.getPersonList());
            observedHires.set(counts.getHiredCount());
        });

        logic.execute("status 1 st/HIRED");
        assertEquals(1, observedHires.get());
        assertCounts(SOFTWARE_ENGINEER, 0, 1, 1, 0);

        logic.execute("delete 2");
        assertCounts(SOFTWARE_ENGINEER, 0, 1, 0, 0);

        logic.execute("add n/Dana p/91234564 e/dana@example.com a/4 Test Road r/Software Engineer");
        assertCounts(SOFTWARE_ENGINEER, 1, 1, 0, 0);

        logic.execute("clear");
        assertCounts(SOFTWARE_ENGINEER, 0, 0, 0, 0);
        assertEquals(0, observedHires.get());
        assertTrue(changes.get() >= 4);
        assertTrue(logic.getPersonList().isEmpty());
    }

    @Test
    public void setPerson_reassignsRole_updatesBothRoleCountsDespiteCandidateFilter() throws Exception {
        logic.execute("find Alice");
        Person original = logic.getPersonList().getLast();
        Person moved = new PersonBuilder(original).withRole(PRODUCT_ANALYST.getTitle().value).build();
        AtomicInteger changes = new AtomicInteger();
        logic.getPersonList().addListener((ListChangeListener<Person>) change -> changes.incrementAndGet());

        model.setPerson(original, moved);

        assertTrue(changes.get() > 0);
        assertCounts(CLOSED_DESIGNER, 0, 0, 0, 0);
        assertCounts(PRODUCT_ANALYST, 0, 0, 0, 1);
        assertEquals(original.getStatus(), moved.getStatus());
        assertEquals(List.of(logic.getPersonList().getFirst()), logic.getFilteredPersonList());
    }

    @Test
    public void execute_reassignsCandidateRole_updatesBothCountsAndPersists() throws Exception {
        logic.execute("find Alice");
        var candidates = logic.getPersonList();
        AtomicInteger changes = new AtomicInteger();
        candidates.addListener((ListChangeListener<Person>) change -> changes.incrementAndGet());

        logic.execute("edit 1 r/Product Analyst");

        assertTrue(changes.get() > 0);
        assertSame(candidates, logic.getPersonList());
        assertEquals(3, logic.getFilteredPersonList().size());
        assertCounts(SOFTWARE_ENGINEER, 0, 0, 1, 0);
        assertCounts(PRODUCT_ANALYST, 1, 0, 0, 0);
        assertCounts(CLOSED_DESIGNER, 0, 0, 0, 1);
        assertEquals(model.getAddressBook(), addressBookStorage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_saveAndReload_reconstructsCountsWithoutPersistingSummary() throws Exception {
        logic.execute("status 1 st/HIRED");
        var candidates = logic.getPersonList();
        var saved = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(model.getAddressBook(), saved);

        model.setAddressBook(new AddressBook());
        assertCounts(SOFTWARE_ENGINEER, 0, 0, 0, 0);
        model.setAddressBook(saved);

        assertSame(candidates, logic.getPersonList());
        assertCounts(SOFTWARE_ENGINEER, 0, 1, 1, 0);
        assertCounts(CLOSED_DESIGNER, 0, 0, 0, 1);
        assertEquals(List.of(SOFTWARE_ENGINEER, PRODUCT_ANALYST, CLOSED_DESIGNER), logic.getFilteredRoleList());
    }

    private void assertCounts(Role role, int pending, int hired, int rejected, int withdrawn) {
        RoleCandidateCounts counts = new RoleCandidateCounts(role.getTitle(), logic.getPersonList());
        assertEquals(pending, counts.getPendingCount());
        assertEquals(hired, counts.getHiredCount());
        assertEquals(rejected, counts.getRejectedCount());
        assertEquals(withdrawn, counts.getWithdrawnCount());
    }
}
