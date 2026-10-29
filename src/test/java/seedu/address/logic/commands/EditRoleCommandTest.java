package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_DUPLICATE_ROLE;
import static seedu.address.logic.Messages.MESSAGE_INVALID_ROLE_DISPLAYED_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.CLOSED_DESIGNER;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.role.ExperienceLevel;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;

public class EditRoleCommandTest {
    private static final Index FIRST_INDEX = Index.fromOneBased(1);
    private static final ExperienceLevel UPDATED_EXPERIENCE = new ExperienceLevel("5 years");

    private final Model model = new ModelManager();
    private final EditRoleCommand command = new EditRoleCommand(FIRST_INDEX, UPDATED_EXPERIENCE);

    @BeforeEach
    public void setUp() {
        model.addRole(SOFTWARE_ENGINEER);
        model.addRole(PRODUCT_ANALYST);
    }

    @Test
    public void constructor_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditRoleCommand(null, UPDATED_EXPERIENCE));
        assertThrows(NullPointerException.class, () -> new EditRoleCommand(FIRST_INDEX, null));
        assertThrows(NullPointerException.class, () -> new EditRoleCommand(null, new RoleTitle("New title"), null));
    }

    @Test
    public void constructor_noFields_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new EditRoleCommand(FIRST_INDEX, null, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_openOrClosedRole_preservesTitleStatusAndAllCandidates() throws Exception {
        for (Role original : List.of(SOFTWARE_ENGINEER, CLOSED_DESIGNER)) {
            Model roleModel = new ModelManager();
            roleModel.addRole(original);
            roleModel.addRole(PRODUCT_ANALYST);
            for (Status status : Status.values()) {
                Person candidate = new PersonBuilder()
                        .withNameAndDistinctContacts("Candidate " + status.ordinal())
                        .withPhone("9123456" + status.ordinal())
                        .withEmail("candidate" + status.ordinal() + "@example.com")
                        .withRole(original.getTitle().value.toLowerCase(Locale.ROOT))
                        .withStatus(status).withNote("Keep recruitment history").withTags("referral").build();
                roleModel.addPerson(candidate);
            }
            List<Person> candidates = List.copyOf(roleModel.getAddressBook().getPersonList());
            var observableRoles = roleModel.getFilteredRoleList();
            Role expected = new RoleBuilder(original).withExperienceLevel(UPDATED_EXPERIENCE.value).build();

            CommandResult result = command.execute(roleModel);

            assertEquals(List.of(expected, PRODUCT_ANALYST), roleModel.getAddressBook().getRoleList());
            assertEquals(candidates, roleModel.getAddressBook().getPersonList());
            assertSame(observableRoles, roleModel.getFilteredRoleList());
            assertEquals(new CommandResult(String.format(EditRoleCommand.MESSAGE_SUCCESS, original.getTitle()),
                    false, false), result);
        }
    }

    @Test
    public void execute_unchangedExperience_succeedsWithoutChangingData() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());

        CommandResult result = new EditRoleCommand(FIRST_INDEX, SOFTWARE_ENGINEER.getExperienceLevel()).execute(model);

        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void execute_renameOpenOrClosedRole_preservesEveryCandidateDetailAndFilter() throws Exception {
        RoleTitle newTitle = new RoleTitle("Updated Role");
        for (Role original : List.of(SOFTWARE_ENGINEER, CLOSED_DESIGNER)) {
            Model roleModel = new ModelManager();
            roleModel.addRole(original);
            roleModel.addRole(PRODUCT_ANALYST);
            for (Status status : Status.values()) {
                if (!original.isClosed() || !status.isPending()) {
                    roleModel.addPerson(new PersonBuilder()
                            .withNameAndDistinctContacts("Candidate " + status.ordinal())
                            .withRole(original.getTitle().value.toLowerCase(Locale.ROOT)).withStatus(status)
                            .withNote("Keep interview details").withTags("referral").build());
                }
            }
            Person unrelated = new PersonBuilder().withNameAndDistinctContacts("Unrelated")
                    .withRole(PRODUCT_ANALYST.getTitle().value).build();
            List<Person> expectedCandidates = roleModel.getAddressBook().getPersonList().stream()
                    .map(person -> person.withRole(newTitle)).toList();
            roleModel.addPerson(unrelated);
            roleModel.updateFilteredPersonList(person -> false);
            var observableCandidates = roleModel.getAddressBook().getPersonList();
            Role expectedRole = new RoleBuilder(original).withTitle(newTitle.value).build();

            CommandResult result = new EditRoleCommand(FIRST_INDEX, newTitle, null).execute(roleModel);

            assertEquals(expectedCandidates, observableCandidates.subList(0, expectedCandidates.size()));
            assertSame(unrelated, observableCandidates.getLast());
            assertSame(observableCandidates, roleModel.getAddressBook().getPersonList());
            assertEquals(List.of(expectedRole, PRODUCT_ANALYST), roleModel.getFilteredRoleList());
            assertTrue(roleModel.getFilteredPersonList().isEmpty());
            assertEquals(new CommandResult("Edited role: Updated Role.", false, false), result);
        }
    }

    @Test
    public void execute_combinedEdit_updatesBothFields() throws Exception {
        RoleTitle title = new RoleTitle("New title");

        new EditRoleCommand(FIRST_INDEX, title, UPDATED_EXPERIENCE).execute(model);

        assertEquals(new RoleBuilder(SOFTWARE_ENGINEER).withTitle(title.value)
                .withExperienceLevel(UPDATED_EXPERIENCE.value).build(), model.getFilteredRoleList().getFirst());
    }

    @Test
    public void execute_duplicateOpenOrClosedTitle_rejectsEntireEdit() {
        model.addRole(CLOSED_DESIGNER);
        model.addPerson(new PersonBuilder().build());
        for (Role destination : List.of(PRODUCT_ANALYST, CLOSED_DESIGNER)) {
            RoleTitle title = new RoleTitle("  " + destination.getTitle().value.toLowerCase(Locale.ROOT) + "  ");
            assertCommandFailure(new EditRoleCommand(FIRST_INDEX, title, UPDATED_EXPERIENCE), model,
                    String.format(MESSAGE_DUPLICATE_ROLE, title));
        }
    }

    @Test
    public void execute_unchangedTitle_succeedsWithoutChangingData() throws Exception {
        model.addPerson(new PersonBuilder().build());
        AddressBook before = new AddressBook(model.getAddressBook());

        new EditRoleCommand(FIRST_INDEX, SOFTWARE_ENGINEER.getTitle(), null).execute(model);

        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void execute_filteredRoleList_editsDisplayedRoleAndPreservesFilters() throws Exception {
        model.addPerson(new PersonBuilder().build());
        model.updateFilteredPersonList(person -> false);
        model.updateFilteredRoleList(role -> role.isSameRole(PRODUCT_ANALYST));
        Role expected = new RoleBuilder(PRODUCT_ANALYST).withExperienceLevel(UPDATED_EXPERIENCE.value).build();

        command.execute(model);

        assertEquals(List.of(SOFTWARE_ENGINEER, expected), model.getAddressBook().getRoleList());
        assertEquals(List.of(expected), model.getFilteredRoleList());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_indexOutsideDisplayedList_rejectsEdit() {
        assertCommandFailure(new EditRoleCommand(Index.fromOneBased(3), UPDATED_EXPERIENCE), model,
                MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
        model.updateFilteredRoleList(role -> false);
        assertCommandFailure(command, model, MESSAGE_INVALID_ROLE_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        assertTrue(command.equals(command));
        assertTrue(command.equals(new EditRoleCommand(FIRST_INDEX, UPDATED_EXPERIENCE)));
        assertFalse(command.equals(new EditRoleCommand(Index.fromOneBased(2), UPDATED_EXPERIENCE)));
        assertFalse(command.equals(new EditRoleCommand(FIRST_INDEX, new ExperienceLevel("Entry level"))));
        EditRoleCommand rename = new EditRoleCommand(FIRST_INDEX, new RoleTitle("New title"), null);
        assertTrue(rename.equals(new EditRoleCommand(FIRST_INDEX, new RoleTitle("New title"), null)));
        assertFalse(rename.equals(command));
        assertFalse(command.equals(rename));
        assertFalse(rename.equals(new EditRoleCommand(FIRST_INDEX, new RoleTitle("Other title"), null)));
        assertFalse(rename.equals(new EditRoleCommand(FIRST_INDEX, new RoleTitle("New title"), UPDATED_EXPERIENCE)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("edit"));
    }

    @Test
    public void toStringMethod() {
        assertEquals(EditRoleCommand.class.getCanonicalName() + "{targetIndex=" + FIRST_INDEX
                + ", title=null, experienceLevel=" + UPDATED_EXPERIENCE + "}", command.toString());
    }
}
