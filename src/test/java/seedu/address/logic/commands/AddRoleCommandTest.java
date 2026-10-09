package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;
import seedu.address.testutil.RoleBuilder;

public class AddRoleCommandTest {

    private final Model model = new ModelManager();

    @Test
    public void constructor_nullRole_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddRoleCommand(null));
    }

    @Test
    public void execute_newRole_addSuccessful() {
        Role role = new RoleBuilder().build();
        AddRoleCommand addRoleCommand = new AddRoleCommand(role);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.addRole(role);

        assertCommandSuccess(addRoleCommand, model,
                String.format(AddRoleCommand.MESSAGE_SUCCESS, role.getTitle()), expectedModel);
        assertEquals(RoleStatus.OPEN, model.getFilteredRoleList().get(0).getStatus());
    }

    @Test
    public void execute_duplicateRole_throwsCommandException() {
        model.addRole(SOFTWARE_ENGINEER);
        Role duplicateRole = new RoleBuilder(SOFTWARE_ENGINEER)
                .withTitle("  software engineer  ")
                .withExperienceLevel("Senior")
                .build();
        AddRoleCommand addRoleCommand = new AddRoleCommand(duplicateRole);

        assertCommandFailure(addRoleCommand, model,
                String.format(AddRoleCommand.MESSAGE_DUPLICATE_ROLE, duplicateRole.getTitle()));
    }

    @Test
    public void equals() {
        Role softwareEngineer = new RoleBuilder().build();
        Role productAnalyst = new RoleBuilder()
                .withTitle("Product Analyst")
                .withExperienceLevel("Entry level")
                .build();
        AddRoleCommand addSoftwareEngineerCommand = new AddRoleCommand(softwareEngineer);
        AddRoleCommand addProductAnalystCommand = new AddRoleCommand(productAnalyst);

        assertTrue(addSoftwareEngineerCommand.equals(addSoftwareEngineerCommand));
        assertTrue(addSoftwareEngineerCommand.equals(new AddRoleCommand(softwareEngineer)));
        assertFalse(addSoftwareEngineerCommand.equals(addProductAnalystCommand));
        assertFalse(addSoftwareEngineerCommand.equals(1));
        assertFalse(addSoftwareEngineerCommand.equals(null));
    }

    @Test
    public void toStringMethod() {
        AddRoleCommand addRoleCommand = new AddRoleCommand(SOFTWARE_ENGINEER);
        String expected = AddRoleCommand.class.getCanonicalName() + "{toAdd=" + SOFTWARE_ENGINEER + "}";
        assertEquals(expected, addRoleCommand.toString());
    }
}
