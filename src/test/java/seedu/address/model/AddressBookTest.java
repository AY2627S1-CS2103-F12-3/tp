package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalRoles.SOFTWARE_ENGINEER;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.role.Role;
import seedu.address.model.role.exceptions.DuplicateRoleException;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
        assertEquals(List.of(), addressBook.getRoleList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void resetData_withDuplicateRoles_throwsDuplicateRoleException() {
        Role duplicateRole = new RoleBuilder(SOFTWARE_ENGINEER)
                .withTitle("software engineer")
                .build();
        List<Role> newRoles = List.of(SOFTWARE_ENGINEER, duplicateRole);
        AddressBookStub newData = new AddressBookStub(List.of(), newRoles);

        assertThrows(DuplicateRoleException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void hasRole_nullRole_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasRole(null));
    }

    @Test
    public void hasRole_roleNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasRole(SOFTWARE_ENGINEER));
    }

    @Test
    public void hasRole_roleInAddressBook_returnsTrue() {
        addressBook.addRole(SOFTWARE_ENGINEER);
        assertTrue(addressBook.hasRole(SOFTWARE_ENGINEER));
    }

    @Test
    public void hasRole_roleWithSameTitleInAddressBook_returnsTrue() {
        addressBook.addRole(SOFTWARE_ENGINEER);
        Role editedRole = new RoleBuilder(SOFTWARE_ENGINEER)
                .withTitle("software engineer")
                .withExperienceLevel("Senior")
                .build();
        assertTrue(addressBook.hasRole(editedRole));
    }

    @Test
    public void getRoleList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getRoleList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName()
                + "{persons=" + addressBook.getPersonList()
                + ", roles=" + addressBook.getRoleList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final ObservableList<Role> roles = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this(persons, List.of());
        }

        AddressBookStub(Collection<Person> persons, Collection<Role> roles) {
            this.persons.setAll(persons);
            this.roles.setAll(roles);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<Role> getRoleList() {
            return roles;
        }
    }

}
