package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_1;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_2;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;
import static seedu.address.testutil.TypicalRoles.getTypicalRoleAddressBook;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code StatusCommand}.
 */
public class StatusCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_finalToPendingForOpenRole_allowsAllTransitions() throws Exception {
        for (Status original : new Status[]{Status.HIRED, Status.REJECTED, Status.WITHDRAWN}) {
            for (Status pending : new Status[]{Status.RESUME, Status.INTERVIEW,
                Status.OFFER, Status.DECISION}) {
                Person candidate = new PersonBuilder().withStatus(original).build();
                setHiringCandidates(candidate);
                new StatusCommand(Set.of(INDEX_FIRST_PERSON), pending).execute(model);
                Person expected = new PersonBuilder(candidate).withStatus(pending).build();
                assertEquals(expected, model.getFilteredPersonList().getFirst());
                assertFalse(model.findRole(candidate.getRole()).orElseThrow().isClosed());
            }
        }
    }

    @Test
    public void execute_finalToPendingForClosedRole_rejectsAllTransitions() {
        for (Status original : new Status[]{Status.HIRED, Status.REJECTED, Status.WITHDRAWN}) {
            Person candidate = new PersonBuilder().withRole(CLOSED_DESIGNER.getTitle().value)
                    .withStatus(original).withNote("Retain history").withTags("referral").build();
            setHiringCandidates(new PersonBuilder().withName("Other Candidate").build(), candidate);
            model.updateFilteredPersonList(person -> person.isSamePerson(candidate));
            for (Status pending : new Status[]{Status.RESUME, Status.INTERVIEW,
                Status.OFFER, Status.DECISION}) {
                assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON), pending), model,
                        String.format(StatusCommand.MESSAGE_PENDING_STATUS_FOR_CLOSED_ROLE,
                                candidate.getName(), candidate.getRole()));
            }
        }
    }

    @Test
    public void execute_closedRoleTitleWithDifferentCase_rejectsPendingStatus() {
        Person candidate = new PersonBuilder().withRole("product designer").withStatus(Status.REJECTED).build();
        setHiringCandidates(candidate);
        assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON), Status.RESUME), model,
                String.format(StatusCommand.MESSAGE_PENDING_STATUS_FOR_CLOSED_ROLE,
                        candidate.getName(), candidate.getRole()));
    }

    @Test
    public void execute_finalToFinalForClosedRole_allowsCorrections() throws Exception {
        for (Status original : new Status[]{Status.HIRED, Status.REJECTED, Status.WITHDRAWN}) {
            for (Status outcome : new Status[]{Status.HIRED, Status.REJECTED, Status.WITHDRAWN}) {
                Person candidate = new PersonBuilder().withRole(CLOSED_DESIGNER.getTitle().value)
                        .withStatus(original).build();
                setHiringCandidates(candidate);
                new StatusCommand(Set.of(INDEX_FIRST_PERSON), outcome).execute(model);
                assertEquals(new PersonBuilder(candidate).withStatus(outcome).build(),
                        model.getFilteredPersonList().getFirst());
                assertTrue(model.findRole(candidate.getRole()).orElseThrow().isClosed());
            }
        }
    }

    @Test
    public void execute_secondHireForClosedRole_rejectsChange() {
        Person hired = new PersonBuilder().withName("Hired Candidate").withRole(CLOSED_DESIGNER.getTitle().value)
                .withStatus(Status.HIRED).build();
        Person rejected = new PersonBuilder().withRole(CLOSED_DESIGNER.getTitle().value)
                .withStatus(Status.REJECTED).build();
        setHiringCandidates(rejected, hired);
        assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON), Status.HIRED), model,
                String.format(StatusCommand.MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE, rejected.getRole()));
    }

    @Test
    public void execute_firstHire_preservesOtherFieldsAndKeepsRoleOpen() {
        Person candidate = new PersonBuilder().withNote("Strong Java portfolio").withTags("referral").build();
        setHiringCandidates(candidate);
        assertSuccessfulHire(candidate);
        assertFalse(model.findRole(SOFTWARE_ENGINEER.getTitle()).orElseThrow().isClosed());
    }

    @Test
    public void execute_otherHiredCandidateHidden_rejectsSecondHire() {
        Person hired = new PersonBuilder().withName("Hired Candidate").withStatus(Status.HIRED).build();
        Person pending = new PersonBuilder().withName("Pending Candidate").build();
        setHiringCandidates(hired, pending);
        model.updateFilteredPersonList(person -> person.isSamePerson(pending));

        assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON), Status.HIRED), model,
                String.format(StatusCommand.MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE, pending.getRole()));
    }

    @Test
    public void execute_sameRoleInDifferentCase_rejectsSecondHire() {
        Person hired = new PersonBuilder().withName("Hired Candidate").withRole("software engineer")
                .withStatus(Status.HIRED).build();
        Person pending = new PersonBuilder().build();
        setHiringCandidates(pending, hired);

        assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON), Status.HIRED), model,
                String.format(StatusCommand.MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE, pending.getRole()));
    }

    @Test
    public void execute_hiredCandidateForOtherRole_allowsHire() {
        Person pending = new PersonBuilder().build();
        Person otherRoleHired = new PersonBuilder().withName("Other Hired Candidate")
                .withRole(PRODUCT_ANALYST.getTitle().value).withStatus(Status.HIRED).build();
        setHiringCandidates(pending, otherRoleHired);
        assertSuccessfulHire(pending);
    }

    @Test
    public void execute_sameCandidateAlreadyHired_allowsReapplyingHired() {
        Person hired = new PersonBuilder().withStatus(Status.HIRED).build();
        Person rejected = new PersonBuilder().withName("Rejected Candidate").withStatus(Status.REJECTED).build();
        setHiringCandidates(hired, rejected);
        assertSuccessfulHire(hired);
    }

    @Test
    public void execute_nonHiredStatusWithExistingHire_allowsUpdate() throws Exception {
        Person hired = new PersonBuilder().withName("Hired Candidate").withStatus(Status.HIRED).build();
        Person pending = new PersonBuilder().build();
        setHiringCandidates(hired, pending);
        for (Status status : Status.values()) {
            if (status == Status.HIRED) {
                continue;
            }
            new StatusCommand(Set.of(INDEX_SECOND_PERSON), status).execute(model);
            assertEquals(status, model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased()).getStatus());
            assertEquals(hired, model.getFilteredPersonList().getFirst());
        }
    }

    @Test
    public void execute_previousHireRejected_allowsAnotherHire() throws Exception {
        Person hired = new PersonBuilder().withName("Hired Candidate").withStatus(Status.HIRED).build();
        Person pending = new PersonBuilder().build();
        setHiringCandidates(hired, pending);
        new StatusCommand(Set.of(INDEX_FIRST_PERSON), Status.REJECTED).execute(model);
        new StatusCommand(Set.of(INDEX_SECOND_PERSON), Status.HIRED).execute(model);
        assertEquals(Status.REJECTED, model.getFilteredPersonList().getFirst().getStatus());
        assertEquals(Status.HIRED, model.getFilteredPersonList().get(1).getStatus());
    }

    /**
     * Updating a person's status preserves all other fields, including notes and tags.
     */
    @Test
    public void execute_specifiedUnfilteredList_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withStatus(VALID_STATUS_1).build();
        StatusCommand statusCommand = new StatusCommand(Set.of(INDEX_FIRST_PERSON), VALID_STATUS_1);

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
        StatusCommand statusCommand = new StatusCommand(Set.of(INDEX_FIRST_PERSON), VALID_STATUS_2);
        String expectedMessage = String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, VALID_STATUS_2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithStatus, editedPerson);

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sameStatus_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        StatusCommand statusCommand = new StatusCommand(Set.of(INDEX_FIRST_PERSON), firstPerson.getStatus());
        String expectedMessage = String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, firstPerson.getStatus());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        Person editedPerson = new PersonBuilder(personInFilteredList).withStatus(VALID_STATUS_1).build();

        StatusCommand statusCommand = new StatusCommand(Set.of(INDEX_FIRST_PERSON), VALID_STATUS_1);

        String expectedMessage = String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, VALID_STATUS_1);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personInFilteredList, editedPerson);

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        StatusCommand statusCommand = new StatusCommand(Set.of(outOfBoundIndex), VALID_STATUS_1);

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

        StatusCommand statusCommand = new StatusCommand(Set.of(outOfBoundIndex), VALID_STATUS_1);

        assertCommandFailure(statusCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_batchNonHiredStatuses_preservesOtherFieldsAndUnselectedCandidates() {
        Person first = new PersonBuilder().withName("First Candidate").withStatus(Status.HIRED)
                .withNote("Keep first note").withTags("referral").build();
        Person second = new PersonBuilder().withName("Second Candidate").withStatus(Status.WITHDRAWN)
                .withNote("Keep second note").withTags("experienced").build();
        Person unselected = new PersonBuilder().withName("Unselected Candidate").build();
        for (Status newStatus : Status.values()) {
            if (newStatus == Status.HIRED) {
                continue;
            }
            setHiringCandidates(first, unselected, second);
            assertSuccessfulBatch(newStatus, Set.of(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON), first, second);
        }
    }

    @Test
    public void execute_batchDifferentRoles_allowsHiringAndReapplyingHired() {
        Person first = new PersonBuilder().withName("First Candidate").withStatus(Status.HIRED).build();
        Person second = new PersonBuilder().withName("Second Candidate")
                .withRole(PRODUCT_ANALYST.getTitle().value).build();
        setHiringCandidates(first, second);

        assertSuccessfulBatch(Status.HIRED, Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), first, second);
        assertFalse(model.findRole(first.getRole()).orElseThrow().isClosed());
        assertFalse(model.findRole(second.getRole()).orElseThrow().isClosed());
    }

    @Test
    public void execute_batchSameRoleHire_rejectsEntireBatch() {
        Person first = new PersonBuilder().withName("First Candidate").build();
        Person second = new PersonBuilder().withName("Second Candidate").build();
        setHiringCandidates(first, second);

        assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), Status.HIRED), model,
                String.format(StatusCommand.MESSAGE_CANDIDATES_HIRED_SAME_ROLE, first.getRole()));
    }

    @Test
    public void execute_batchSameRoleDifferentCase_rejectsEntireBatch() {
        Person first = new PersonBuilder().withName("First Candidate").build();
        Person second = new PersonBuilder().withName("Second Candidate").withRole("software engineer").build();
        setHiringCandidates(first, second);
        AddressBook before = new AddressBook(model.getAddressBook());
        List<Person> beforeFiltered = List.copyOf(model.getFilteredPersonList());

        CommandException exception = assertThrows(CommandException.class, () ->
                new StatusCommand(Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), Status.HIRED).execute(model));

        // Either candidate may be checked first; both role spellings describe the same conflict.
        assertTrue(Set.of(
                String.format(StatusCommand.MESSAGE_CANDIDATES_HIRED_SAME_ROLE, first.getRole()),
                String.format(StatusCommand.MESSAGE_CANDIDATES_HIRED_SAME_ROLE, second.getRole()))
                .contains(exception.getMessage()));
        assertEquals(before, model.getAddressBook());
        assertEquals(beforeFiltered, model.getFilteredPersonList());
    }

    @Test
    public void execute_batchWithHiddenExistingHire_rejectsEntireBatch() {
        Person first = new PersonBuilder().withName("First Candidate")
                .withRole(PRODUCT_ANALYST.getTitle().value).build();
        Person second = new PersonBuilder().withName("Second Candidate").build();
        Person hired = new PersonBuilder().withName("Hidden Candidate").withStatus(Status.HIRED).build();
        setHiringCandidates(first, second, hired);
        model.updateFilteredPersonList(person -> person.getStatus() != Status.HIRED);

        assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), Status.HIRED), model,
                String.format(StatusCommand.MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE, second.getRole()));
    }

    @Test
    public void execute_batchPendingStatusWithClosedRole_rejectsEntireBatch() {
        Person openRoleCandidate = new PersonBuilder().withName("Open Role Candidate").build();
        Person closedRoleCandidate = new PersonBuilder().withName("Closed Role Candidate")
                .withRole(CLOSED_DESIGNER.getTitle().value).withStatus(Status.REJECTED).build();
        setHiringCandidates(openRoleCandidate, closedRoleCandidate);

        for (Status pending : Status.values()) {
            if (!pending.isPending()) {
                continue;
            }
            assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), pending), model,
                    String.format(StatusCommand.MESSAGE_PENDING_STATUS_FOR_CLOSED_ROLE,
                            closedRoleCandidate.getName(), closedRoleCandidate.getRole()));
        }
    }

    @Test
    public void execute_batchFinalStatusWithClosedRole_success() {
        Person first = new PersonBuilder().withName("Open Role Candidate").build();
        Person second = new PersonBuilder().withName("Closed Role Candidate")
                .withRole(CLOSED_DESIGNER.getTitle().value).withStatus(Status.REJECTED).build();
        setHiringCandidates(first, second);

        assertSuccessfulBatch(Status.WITHDRAWN, Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), first, second);
        assertTrue(model.findRole(second.getRole()).orElseThrow().isClosed());
    }

    @Test
    public void execute_batchInvalidIndex_rejectsEntireBatch() {
        Person first = new PersonBuilder().withName("First Candidate").build();
        Person second = new PersonBuilder().withName("Second Candidate").build();
        setHiringCandidates(first, second);
        // Visit a valid index first to detect implementations that mutate before checking the whole batch.
        Set<Index> indices = new LinkedHashSet<>(List.of(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON));

        assertCommandFailure(new StatusCommand(indices, Status.REJECTED), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_batchIndexOutsideFilteredList_rejectsEntireBatch() {
        Person hidden = new PersonBuilder().withName("Hidden Candidate").build();
        Person first = new PersonBuilder().withName("First Candidate").build();
        Person second = new PersonBuilder().withName("Second Candidate").build();
        setHiringCandidates(hidden, first, second);
        model.updateFilteredPersonList(person -> !person.isSamePerson(hidden));

        assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON), Status.REJECTED), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_batchFilteredList_usesDisplayedIndicesAndShowsAllAfterwards() {
        Person hidden = new PersonBuilder().withName("Hidden Candidate").withStatus(Status.WITHDRAWN).build();
        Person first = new PersonBuilder().withName("First Candidate").build();
        Person second = new PersonBuilder().withName("Second Candidate").build();
        setHiringCandidates(hidden, first, second);
        // Updating a selected candidate makes it leave this filter; the other target must not shift.
        model.updateFilteredPersonList(person -> person.getStatus().isPending());

        assertSuccessfulBatch(Status.REJECTED, Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), first, second);
        assertEquals(List.of(hidden, new PersonBuilder(first).withStatus(Status.REJECTED).build(),
                new PersonBuilder(second).withStatus(Status.REJECTED).build()), model.getFilteredPersonList());
    }

    @Test
    public void execute_batchWhileRoleListDisplayed_rejectsEntireBatch() {
        model.setRoleListDisplayed(true);

        assertCommandFailure(new StatusCommand(Set.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), Status.REJECTED),
                model, Messages.MESSAGE_PERSON_LIST_NOT_DISPLAYED);
        assertTrue(model.isRoleListDisplayed());
    }

    @Test
    public void equals_batchOrderAndIndexInstances_doNotAffectEquality() {
        Set<Index> firstOrder = new LinkedHashSet<>(List.of(Index.fromOneBased(1), Index.fromOneBased(2)));
        Set<Index> reverseOrder = new LinkedHashSet<>(List.of(Index.fromOneBased(2), Index.fromOneBased(1)));
        StatusCommand command = new StatusCommand(firstOrder, Status.REJECTED);

        assertEquals(command, new StatusCommand(reverseOrder, Status.REJECTED));
        assertNotEquals(command, new StatusCommand(Set.of(INDEX_FIRST_PERSON), Status.REJECTED));
        assertNotEquals(command, new StatusCommand(firstOrder, Status.WITHDRAWN));
    }

    @Test
    public void equals() {
        final StatusCommand standardCommand = new StatusCommand(Set.of(INDEX_FIRST_PERSON), VALID_STATUS_1);

        // same values -> returns true
        StatusCommand commandWithSameValues = new StatusCommand(Set.of(INDEX_FIRST_PERSON), VALID_STATUS_1);
        assertEquals(standardCommand, commandWithSameValues);

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertNotEquals(null, standardCommand);

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertNotEquals(standardCommand, new StatusCommand(Set.of(INDEX_SECOND_PERSON), VALID_STATUS_1));

        // different status -> returns false
        assertNotEquals(standardCommand, new StatusCommand(Set.of(INDEX_FIRST_PERSON), VALID_STATUS_2));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        StatusCommand statusCommand = new StatusCommand(Set.of(index), VALID_STATUS_1);
        String expected = StatusCommand.class.getCanonicalName() + "{indices=" + Set.of(index) + ", status="
                + VALID_STATUS_1 + "}";
        assertEquals(expected, statusCommand.toString());
    }

    private void setHiringCandidates(Person... candidates) {
        model = new ModelManager(getTypicalRoleAddressBook(), new UserPrefs());
        for (Person candidate : candidates) {
            model.addPerson(candidate);
        }
    }

    private void assertSuccessfulHire(Person candidate) {
        Person hired = new PersonBuilder(candidate).withStatus(Status.HIRED).build();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(candidate, hired);
        assertCommandSuccess(new StatusCommand(Set.of(INDEX_FIRST_PERSON), Status.HIRED), model,
                String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, Status.HIRED), expectedModel);
    }

    private void assertSuccessfulBatch(Status newStatus, Set<Index> indices, Person... candidates) {
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        for (Person candidate : candidates) {
            expectedModel.setPerson(candidate, new PersonBuilder(candidate).withStatus(newStatus).build());
        }
        assertCommandSuccess(new StatusCommand(indices, newStatus), model,
                String.format(StatusCommand.MESSAGE_ADD_STATUS_SUCCESS, newStatus), expectedModel);
    }

}
