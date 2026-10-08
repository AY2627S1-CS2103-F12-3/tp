package seedu.address.model.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.role.exceptions.DuplicateRoleException;
import seedu.address.model.role.exceptions.RoleNotFoundException;
import seedu.address.testutil.RoleBuilder;

public class UniqueRoleListTest {

    private final UniqueRoleList uniqueRoleList = new UniqueRoleList();

    @Test
    public void contains_nullRole_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueRoleList.contains(null));
    }

    @Test
    public void contains_roleNotInList_returnsFalse() {
        assertFalse(uniqueRoleList.contains(SOFTWARE_ENGINEER));
    }

    @Test
    public void contains_roleInList_returnsTrue() {
        uniqueRoleList.add(SOFTWARE_ENGINEER);
        assertTrue(uniqueRoleList.contains(SOFTWARE_ENGINEER));
    }

    @Test
    public void contains_roleWithSameTitleIgnoringCase_returnsTrue() {
        uniqueRoleList.add(SOFTWARE_ENGINEER);
        Role duplicateRole = new RoleBuilder(SOFTWARE_ENGINEER)
                .withTitle("software engineer")
                .withExperienceLevel("Entry level")
                .build();
        assertTrue(uniqueRoleList.contains(duplicateRole));
    }

    @Test
    public void add_duplicateRole_throwsDuplicateRoleException() {
        uniqueRoleList.add(SOFTWARE_ENGINEER);
        Role duplicateRole = new RoleBuilder(SOFTWARE_ENGINEER)
                .withTitle("SOFTWARE ENGINEER")
                .build();
        assertThrows(DuplicateRoleException.class, () -> uniqueRoleList.add(duplicateRole));
    }

    @Test
    public void setRole_targetNotInList_throwsRoleNotFoundException() {
        assertThrows(RoleNotFoundException.class, () -> uniqueRoleList.setRole(
                SOFTWARE_ENGINEER, PRODUCT_ANALYST));
    }

    @Test
    public void setRole_editedRoleHasDuplicateIdentity_throwsDuplicateRoleException() {
        uniqueRoleList.add(SOFTWARE_ENGINEER);
        uniqueRoleList.add(PRODUCT_ANALYST);
        Role duplicateRole = new RoleBuilder(SOFTWARE_ENGINEER)
                .withTitle("product analyst")
                .build();
        assertThrows(DuplicateRoleException.class, () -> uniqueRoleList.setRole(
                SOFTWARE_ENGINEER, duplicateRole));
    }

    @Test
    public void setRole_validEditedRole_replacesRole() {
        uniqueRoleList.add(SOFTWARE_ENGINEER);
        Role closedRole = new RoleBuilder(SOFTWARE_ENGINEER)
                .withStatus(RoleStatus.CLOSED)
                .build();
        uniqueRoleList.setRole(SOFTWARE_ENGINEER, closedRole);
        assertEquals(List.of(closedRole), uniqueRoleList.asUnmodifiableObservableList());
    }

    @Test
    public void setRoles_listWithDuplicateRoles_throwsDuplicateRoleException() {
        Role duplicateRole = new RoleBuilder(SOFTWARE_ENGINEER)
                .withTitle("software engineer")
                .build();
        assertThrows(DuplicateRoleException.class, () -> uniqueRoleList.setRoles(
                List.of(SOFTWARE_ENGINEER, duplicateRole)));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> uniqueRoleList
                .asUnmodifiableObservableList().add(SOFTWARE_ENGINEER));
    }
}
