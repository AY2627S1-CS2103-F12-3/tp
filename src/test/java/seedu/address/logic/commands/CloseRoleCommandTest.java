package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_ROLE_LIST_NOT_DISPLAYED;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;

public class CloseRoleCommandTest {
    private final Model model = new ModelManager();
    private final CloseRoleCommand command = new CloseRoleCommand(Index.fromOneBased(1));

    @BeforeEach
    public void setUp() {
        model.addRole(SOFTWARE_ENGINEER);
        model.setRoleListDisplayed(true);
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CloseRoleCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_noCandidates_closesRole() throws Exception {
        assertSuccessfulClosure();
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }

    @Test
    public void execute_finalOutcomes_preservesCandidates() throws Exception {
        Person hired = candidate("Hired Candidate", Status.HIRED);
        Person rejected = candidate("Rejected Candidate", Status.REJECTED);
        Person withdrawn = candidate("Withdrawn Candidate", Status.WITHDRAWN);
        model.addPerson(hired);
        model.addPerson(rejected);
        model.addPerson(withdrawn);
        model.updateFilteredPersonList(person -> false);

        assertSuccessfulClosure();
        assertEquals(List.of(hired, rejected, withdrawn), model.getAddressBook().getPersonList());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_noHiredCandidate_closesRole() throws Exception {
        model.addPerson(candidate("Rejected Candidate", Status.REJECTED));
        assertSuccessfulClosure();
    }

    @Test
    public void execute_eachPendingStatus_rejectsClosure() {
        for (Status status : new Status[]{Status.PENDING_RESUME_SCREENING, Status.PENDING_RECRUITER_INTERVIEW,
            Status.PENDING_OFFER_EXTENSION, Status.PENDING_OFFER_DECISION}) {
            Person person = candidate("Pending Candidate", status);
            model.addPerson(person);
            model.updateFilteredPersonList(candidate -> false);
            assertCommandFailure(command, model, String.format(CloseRoleCommand.MESSAGE_PENDING_CANDIDATES,
                    SOFTWARE_ENGINEER.getTitle(), 1));
            model.deletePerson(person);
        }
    }

    @Test
    public void execute_multiplePendingCandidates_reportsTotal() {
        model.addPerson(candidate("First Candidate", Status.PENDING_OFFER_DECISION));
        model.addPerson(candidate("Second Candidate", Status.PENDING_RESUME_SCREENING));
        assertCommandFailure(command, model, String.format(CloseRoleCommand.MESSAGE_PENDING_CANDIDATES,
                SOFTWARE_ENGINEER.getTitle(), 2));
    }

    @Test
    public void execute_multipleHiredCandidates_rejectsClosure() {
        model.addPerson(candidate("First Candidate", Status.HIRED));
        model.addPerson(candidate("Second Candidate", Status.HIRED));
        assertCommandFailure(command, model, String.format(CloseRoleCommand.MESSAGE_MULTIPLE_HIRED,
                SOFTWARE_ENGINEER.getTitle()));
    }

    @Test
    public void execute_pendingCandidateForOtherRole_closesRole() throws Exception {
        model.addRole(PRODUCT_ANALYST);
        model.addPerson(new PersonBuilder().withRole(PRODUCT_ANALYST.getTitle().value).build());
        assertSuccessfulClosure();
    }

    @Test
    public void execute_roleTitleWithDifferentCase_checksCandidate() {
        model.addPerson(new PersonBuilder().withRole("software engineer").build());
        assertCommandFailure(command, model, String.format(CloseRoleCommand.MESSAGE_PENDING_CANDIDATES,
                SOFTWARE_ENGINEER.getTitle(), 1));
    }

    @Test
    public void execute_candidateListDisplayed_rejectsClosure() {
        model.setRoleListDisplayed(false);
        assertCommandFailure(command, model, MESSAGE_ROLE_LIST_NOT_DISPLAYED);
    }

    @Test
    public void execute_indexOutsideDisplayedList_rejectsClosure() {
        assertCommandFailure(new CloseRoleCommand(Index.fromOneBased(2)), model,
                MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
        model.updateFilteredRoleList(role -> false);
        assertCommandFailure(command, model, MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredRoleList_closesDisplayedRole() throws Exception {
        model.addRole(PRODUCT_ANALYST);
        model.updateFilteredRoleList(role -> role.isSameRole(PRODUCT_ANALYST));
        command.execute(model);
        assertFalse(model.findRole(SOFTWARE_ENGINEER.getTitle()).orElseThrow().isClosed());
        assertTrue(model.findRole(PRODUCT_ANALYST.getTitle()).orElseThrow().isClosed());
    }

    @Test
    public void execute_alreadyClosed_rejectsClosure() throws Exception {
        command.execute(model);
        assertCommandFailure(command, model, String.format(CloseRoleCommand.MESSAGE_ALREADY_CLOSED,
                SOFTWARE_ENGINEER.getTitle()));
    }

    @Test
    public void equals() {
        assertTrue(command.equals(command));
        assertTrue(command.equals(new CloseRoleCommand(Index.fromOneBased(1))));
        assertFalse(command.equals(new CloseRoleCommand(Index.fromOneBased(2))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("close"));
    }

    @Test
    public void toStringMethod() {
        assertEquals(CloseRoleCommand.class.getCanonicalName() + "{targetIndex=" + Index.fromOneBased(1) + "}",
                command.toString());
    }

    private static Person candidate(String name, Status status) {
        return new PersonBuilder().withName(name).withStatus(status).build();
    }

    private void assertSuccessfulClosure() throws Exception {
        CommandResult result = command.execute(model);
        Role expectedRole = new RoleBuilder(SOFTWARE_ENGINEER).withStatus(RoleStatus.CLOSED).build();
        assertEquals(expectedRole, model.getFilteredRoleList().getFirst());
        assertEquals(String.format(CloseRoleCommand.MESSAGE_SUCCESS, SOFTWARE_ENGINEER.getTitle()),
                result.getFeedbackToUser());
        assertTrue(result.isShowRoleList());
        assertTrue(model.isRoleListDisplayed());
    }
}
