package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalRoles.PRODUCT_ANALYST;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
import seedu.address.model.role.exceptions.DuplicateRoleException;
import seedu.address.model.role.exceptions.RoleNotFoundException;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;

public class RoleRenameTest {
    private final AddressBook addressBook = new AddressBook();
    private final Person candidate = new PersonBuilder().withRole("software engineer").build();

    @BeforeEach
    public void setUp() {
        addressBook.addRole(SOFTWARE_ENGINEER);
        addressBook.addRole(PRODUCT_ANALYST);
        addressBook.addPerson(candidate);
    }

    @Test
    public void setRole_nullArguments_rejectsWithoutChanges() {
        AddressBook original = new AddressBook(addressBook);

        assertThrows(NullPointerException.class, () -> addressBook.setRole(null, SOFTWARE_ENGINEER));
        assertThrows(NullPointerException.class, () -> addressBook.setRole(SOFTWARE_ENGINEER, null));

        assertEquals(original, addressBook);
    }

    @Test
    public void setRole_duplicateTitle_rejectsBeforeChangingEitherCollection() {
        AddressBook original = new AddressBook(addressBook);
        Role duplicate = new RoleBuilder(SOFTWARE_ENGINEER).withTitle("product analyst").build();

        assertThrows(DuplicateRoleException.class, () -> addressBook.setRole(SOFTWARE_ENGINEER, duplicate));

        assertEquals(original, addressBook);
        assertSame(candidate, addressBook.getPersonList().getFirst());
    }

    @Test
    public void setRole_missingTarget_rejectsBeforeChangingEitherCollection() {
        AddressBook original = new AddressBook(addressBook);
        Role missing = new RoleBuilder().withTitle("Missing Role").build();

        assertThrows(RoleNotFoundException.class, () -> addressBook.setRole(missing, SOFTWARE_ENGINEER));

        assertEquals(original, addressBook);
    }

    @Test
    public void setRole_caseOnlyRename_updatesCandidateSpelling() {
        Role edited = new RoleBuilder(SOFTWARE_ENGINEER).withTitle("SOFTWARE ENGINEER").build();

        addressBook.setRole(SOFTWARE_ENGINEER, edited);

        assertEquals(List.of(edited, PRODUCT_ANALYST), addressBook.getRoleList());
        assertEquals(List.of(candidate.withRole(new RoleTitle("SOFTWARE ENGINEER"))), addressBook.getPersonList());
    }

    @Test
    public void setRole_experienceOnly_preservesCandidateInstances() {
        Role edited = new RoleBuilder(SOFTWARE_ENGINEER).withExperienceLevel("Senior").build();

        addressBook.setRole(SOFTWARE_ENGINEER, edited);

        assertEquals(edited, addressBook.getRoleList().getFirst());
        assertSame(candidate, addressBook.getPersonList().getFirst());
    }
}
