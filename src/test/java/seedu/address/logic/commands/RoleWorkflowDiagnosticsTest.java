package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.BeforeEach;
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
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.LogCapture;
import seedu.address.testutil.PersonBuilder;

public class RoleWorkflowDiagnosticsTest {
    private static final Person CANDIDATE = new PersonBuilder().withName("Private Applicant")
            .withPhone("91234567").withEmail("private@example.com")
            .withNote("Private interview notes").build();

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addRole(SOFTWARE_ENGINEER);
        model.addRole(PRODUCT_ANALYST);
        model.addRole(CLOSED_DESIGNER);
        model.addPerson(CANDIDATE);
    }

    @Test
    public void execute_addRole_logsSuccessAndDuplicateWithoutChangingRejectionState() throws Exception {
        Role role = new Role(new RoleTitle("QA Engineer"), SOFTWARE_ENGINEER.getExperienceLevel());
        AddRoleCommand command = new AddRoleCommand(role);
        try (LogCapture logs = new LogCapture()) {
            assertEquals(String.format(AddRoleCommand.MESSAGE_SUCCESS, role.getTitle()),
                    command.execute(model).getFeedbackToUser());
            AddressBook savedState = new AddressBook(model.getAddressBook());

            assertThrows(CommandException.class, () -> command.execute(model));

            assertEquals(savedState, model.getAddressBook());
            logs.assertContains(AddRoleCommand.class, Level.INFO, "Added open role in memory.");
            logs.assertContains(AddRoleCommand.class, Level.FINE,
                    "Rejected role addition: title already exists.");
            assertNoCandidateDetails(logs);
        }
    }

    @Test
    public void execute_editRole_logsSuccessAndDuplicateWithHiddenCandidatePreserved() throws Exception {
        model.setRoleListDisplayed(true);
        model.updateFilteredPersonList(person -> false);
        RoleTitle newTitle = new RoleTitle("Platform Engineer");
        try (LogCapture logs = new LogCapture()) {
            new EditRoleCommand(INDEX_FIRST_PERSON, newTitle, null).execute(model);
            assertEquals(CANDIDATE.withRole(newTitle), model.getAddressBook().getPersonList().getFirst());
            assertTrue(model.getFilteredPersonList().isEmpty());
            AddressBook savedState = new AddressBook(model.getAddressBook());

            assertThrows(CommandException.class, () -> new EditRoleCommand(INDEX_FIRST_PERSON,
                    PRODUCT_ANALYST.getTitle(), null).execute(model));

            assertEquals(savedState, model.getAddressBook());
            logs.assertContains(EditRoleCommand.class, Level.INFO,
                    "Updated role details in memory at displayed index 1.");
            logs.assertContains(EditRoleCommand.class, Level.FINE,
                    "Rejected role edit: title already exists.");
            assertNoCandidateDetails(logs);
        }
    }

    @Test
    public void execute_closeRole_logsOutcomesAndRetainsCandidates() throws Exception {
        model.setRoleListDisplayed(true);
        try (LogCapture logs = new LogCapture()) {
            AddressBook savedState = new AddressBook(model.getAddressBook());
            CloseRoleCommand command = new CloseRoleCommand(INDEX_FIRST_PERSON);
            assertThrows(CommandException.class, () -> command.execute(model));
            assertEquals(savedState, model.getAddressBook());

            Person hired = new PersonBuilder(CANDIDATE).withStatus(Status.HIRED).build();
            model.setPerson(CANDIDATE, hired);
            assertEquals(String.format(CloseRoleCommand.MESSAGE_SUCCESS, SOFTWARE_ENGINEER.getTitle()),
                    command.execute(model).getFeedbackToUser());
            savedState = new AddressBook(model.getAddressBook());
            assertThrows(CommandException.class, () -> command.execute(model));

            assertEquals(savedState, model.getAddressBook());
            assertEquals(List.of(hired), model.getAddressBook().getPersonList());
            logs.assertContains(CloseRoleCommand.class, Level.FINE,
                    "Rejected role closure: 1 candidate(s) still pending.");
            logs.assertContains(CloseRoleCommand.class, Level.INFO,
                    "Closed role in memory at displayed index 1.");
            logs.assertContains(CloseRoleCommand.class, Level.FINE,
                    "Rejected role closure: role is already closed.");
            assertNoCandidateDetails(logs);
        }
    }

    @Test
    public void execute_closeRoleWithMultipleHires_logsRejectionAndPreservesState() {
        model.setRoleListDisplayed(true);
        model.setPerson(CANDIDATE, new PersonBuilder(CANDIDATE).withStatus(Status.HIRED).build());
        model.addPerson(new PersonBuilder(CANDIDATE).withNameAndDistinctContacts("Other Applicant")
                .withStatus(Status.HIRED).build());
        AddressBook savedState = new AddressBook(model.getAddressBook());
        try (LogCapture logs = new LogCapture()) {
            CloseRoleCommand command = new CloseRoleCommand(INDEX_FIRST_PERSON);
            assertThrows(CommandException.class, () -> command.execute(model));

            assertEquals(savedState, model.getAddressBook());
            logs.assertContains(CloseRoleCommand.class, Level.FINE,
                    "Rejected role closure: 2 candidates are marked HIRED.");
            assertNoCandidateDetails(logs);
        }
    }

    @Test
    public void execute_deleteRole_logsCandidateGuardAndSuccessfulRemoval() throws Exception {
        model.setRoleListDisplayed(true);
        try (LogCapture logs = new LogCapture()) {
            AddressBook savedState = new AddressBook(model.getAddressBook());
            DeleteRoleCommand command = new DeleteRoleCommand(INDEX_FIRST_PERSON);
            assertThrows(CommandException.class, () -> command.execute(model));
            assertEquals(savedState, model.getAddressBook());

            new DeleteRoleCommand(Index.fromOneBased(2)).execute(model);

            assertFalse(model.hasRole(PRODUCT_ANALYST));
            assertEquals(List.of(CANDIDATE), model.getAddressBook().getPersonList());
            logs.assertContains(DeleteRoleCommand.class, Level.FINE,
                    "Rejected role deletion: 1 associated candidate(s).");
            logs.assertContains(DeleteRoleCommand.class, Level.INFO,
                    "Deleted role in memory at displayed index 2.");
            assertNoCandidateDetails(logs);
        }
    }

    @Test
    public void execute_transferCandidate_logsTransferAndClosedDestinationGuard() throws Exception {
        try (LogCapture logs = new LogCapture()) {
            assertEquals(String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS,
                    Messages.format(CANDIDATE.withRole(PRODUCT_ANALYST.getTitle()))),
                    roleEdit(PRODUCT_ANALYST.getTitle()).execute(model).getFeedbackToUser());
            AddressBook savedState = new AddressBook(model.getAddressBook());

            assertThrows(CommandException.class, () -> roleEdit(CLOSED_DESIGNER.getTitle()).execute(model));

            assertEquals(savedState, model.getAddressBook());
            logs.assertContains(EditCommand.class, Level.INFO,
                    "Updated candidate role in memory at displayed index 1.");
            logs.assertContains(EditCommand.class, Level.FINE,
                    "Rejected candidate role transfer: destination role is closed.");
            assertNoCandidateDetails(logs);
        }
    }

    @Test
    public void execute_transferHiredCandidate_logsHiringGuardAndPreservesState() {
        model.setPerson(CANDIDATE, new PersonBuilder(CANDIDATE).withStatus(Status.HIRED).build());
        model.addPerson(new PersonBuilder().withName("Other Applicant")
                .withRole(PRODUCT_ANALYST.getTitle().value)
                .withStatus(Status.HIRED).build());
        AddressBook savedState = new AddressBook(model.getAddressBook());
        try (LogCapture logs = new LogCapture()) {
            assertThrows(CommandException.class, () -> roleEdit(PRODUCT_ANALYST.getTitle()).execute(model));

            assertEquals(savedState, model.getAddressBook());
            logs.assertContains(CandidateRoleChecks.class, Level.FINE,
                    "Rejected hiring change: destination role already has another hired candidate.");
            assertNoCandidateDetails(logs);
        }
    }

    @Test
    public void verifyHiringAllowed_missingInternalStatus_throwsAssertionErrorWithoutMutation() {
        AddressBook savedState = new AddressBook(model.getAddressBook());

        AssertionError error = assertThrows(AssertionError.class, () ->
                CandidateRoleChecks.verifyHiringAllowed(model, CANDIDATE, SOFTWARE_ENGINEER.getTitle(), null));

        assertEquals("Hiring checks require a candidate status", error.getMessage());
        assertEquals(savedState, model.getAddressBook());
    }

    @Test
    public void execute_brokenRoleLookupContract_throwsAssertionErrorBeforeMutation() {
        Model brokenLookup = new ModelManager(model.getAddressBook(), new UserPrefs()) {
            @Override
            public Optional<Role> findRole(RoleTitle title) {
                return Optional.of(SOFTWARE_ENGINEER);
            }
        };
        AddressBook savedState = new AddressBook(brokenLookup.getAddressBook());

        AssertionError error = assertThrows(AssertionError.class, () ->
                roleEdit(PRODUCT_ANALYST.getTitle()).execute(brokenLookup));

        assertEquals("Role lookup must match the request", error.getMessage());
        assertEquals(savedState, brokenLookup.getAddressBook());
    }

    private EditCommand roleEdit(RoleTitle title) {
        return new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withRole(title.value).build());
    }

    private void assertNoCandidateDetails(LogCapture logs) {
        assertFalse(logs.getRecords().isEmpty());
        for (LogRecord record : logs.getRecords()) {
            for (String detail : List.of(CANDIDATE.getName().toString(), CANDIDATE.getPhone().toString(),
                    CANDIDATE.getEmail().toString(), CANDIDATE.getNote().toString())) {
                assertFalse(record.getMessage().contains(detail), "Diagnostic includes candidate details");
            }
        }
    }
}
