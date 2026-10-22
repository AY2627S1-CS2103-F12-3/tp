package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_ROLE_LIST_NOT_DISPLAYED;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
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
import seedu.address.testutil.PersonBuilder;

public class DeleteRoleCommandTest {
    private final Model model = new ModelManager();
    private final DeleteRoleCommand command = new DeleteRoleCommand(Index.fromOneBased(1));

    @BeforeEach
    public void setUp() {
        model.addRole(SOFTWARE_ENGINEER);
        model.setRoleListDisplayed(true);
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteRoleCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_emptyRole_deletesRoleAndKeepsRoleView() throws Exception {
        CommandResult result = command.execute(model);
        assertTrue(model.getFilteredRoleList().isEmpty());
        assertTrue(model.getAddressBook().getRoleList().isEmpty());
        assertEquals(String.format(DeleteRoleCommand.MESSAGE_SUCCESS, SOFTWARE_ENGINEER.getTitle()),
                result.getFeedbackToUser());
        assertTrue(result.isShowRoleList());
        assertTrue(model.isRoleListDisplayed());
    }

    @Test
    public void execute_closedEmptyRole_deletesRole() throws Exception {
        model.addRole(CLOSED_DESIGNER);
        new DeleteRoleCommand(Index.fromOneBased(2)).execute(model);
        assertEquals(List.of(SOFTWARE_ENGINEER), model.getAddressBook().getRoleList());
    }

    @Test
    public void execute_candidateWithAnyStatus_rejectsDeletion() {
        for (Status status : Status.values()) {
            Person candidate = new PersonBuilder().withStatus(status).build();
            model.addPerson(candidate);
            model.updateFilteredPersonList(person -> false);
            assertCommandFailure(command, model, String.format(DeleteRoleCommand.MESSAGE_ASSOCIATED_CANDIDATES,
                    SOFTWARE_ENGINEER.getTitle(), 1));
            model.deletePerson(candidate);
        }
    }

    @Test
    public void execute_closedRoleWithCandidates_rejectsDeletion() {
        model.addRole(CLOSED_DESIGNER);
        model.addPerson(new PersonBuilder().withRole(CLOSED_DESIGNER.getTitle().value)
                .withStatus(Status.WITHDRAWN).build());
        assertCommandFailure(new DeleteRoleCommand(Index.fromOneBased(2)), model,
                String.format(DeleteRoleCommand.MESSAGE_ASSOCIATED_CANDIDATES, CLOSED_DESIGNER.getTitle(), 1));
    }

    @Test
    public void execute_multipleCandidatesWithDifferentTitleCase_reportsTotal() {
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("First Candidate")
                .withRole("software engineer").build());
        model.addPerson(new PersonBuilder().withNameAndDistinctContacts("Second Candidate")
                .withStatus(Status.HIRED).build());
        assertCommandFailure(command, model, String.format(DeleteRoleCommand.MESSAGE_ASSOCIATED_CANDIDATES,
                SOFTWARE_ENGINEER.getTitle(), 2));
    }

    @Test
    public void execute_candidatesForOtherRole_preservesCandidatesAndRole() throws Exception {
        model.addRole(PRODUCT_ANALYST);
        Person candidate = new PersonBuilder().withRole(PRODUCT_ANALYST.getTitle().value).build();
        model.addPerson(candidate);
        command.execute(model);
        assertEquals(List.of(PRODUCT_ANALYST), model.getFilteredRoleList());
        assertEquals(List.of(candidate), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_filteredRoleList_deletesDisplayedRole() throws Exception {
        model.addRole(PRODUCT_ANALYST);
        model.updateFilteredRoleList(role -> role.isSameRole(PRODUCT_ANALYST));
        command.execute(model);
        assertEquals(List.of(SOFTWARE_ENGINEER), model.getAddressBook().getRoleList());
        assertTrue(model.getFilteredRoleList().isEmpty());
    }

    @Test
    public void execute_candidateListDisplayed_rejectsDeletion() {
        model.setRoleListDisplayed(false);
        assertCommandFailure(command, model, MESSAGE_ROLE_LIST_NOT_DISPLAYED);
    }

    @Test
    public void execute_indexOutsideDisplayedList_rejectsDeletion() {
        assertCommandFailure(new DeleteRoleCommand(Index.fromOneBased(2)), model,
                MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
        model.updateFilteredRoleList(role -> false);
        assertCommandFailure(command, model, MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_deletedRoleTitle_canBeAddedAgain() throws Exception {
        command.execute(model);
        new AddRoleCommand(SOFTWARE_ENGINEER).execute(model);
        assertEquals(List.of(SOFTWARE_ENGINEER), model.getAddressBook().getRoleList());
    }

    @Test
    public void equals() {
        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteRoleCommand(Index.fromOneBased(1))));
        assertFalse(command.equals(new DeleteRoleCommand(Index.fromOneBased(2))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("delete"));
    }

    @Test
    public void toStringMethod() {
        assertEquals(DeleteRoleCommand.class.getCanonicalName() + "{targetIndex=" + Index.fromOneBased(1) + "}",
                command.toString());
    }
}
