package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;
import seedu.address.model.role.UniqueRoleList;

/**
 * Wraps all data at the address-book level.
 * Duplicate persons and roles are not allowed.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final UniqueRoleList roles = new UniqueRoleList();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Replaces the contents of the role list with {@code roles}.
     * {@code roles} must not contain duplicate roles.
     */
    public void setRoles(List<Role> roles) {
        this.roles.setRoles(roles);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setRoles(newData.getRoleList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    //// role-level operations

    /**
     * Returns true if a role with the same identity as {@code role} exists in the address book.
     */
    public boolean hasRole(Role role) {
        requireNonNull(role);
        return roles.contains(role);
    }

    /**
     * Returns the role whose title matches {@code title}, ignoring case, if there is one.
     */
    public Optional<Role> findRole(RoleTitle title) {
        return roles.findByTitle(title);
    }

    /**
     * Adds {@code role} to the address book.
     * The role must not already exist in the address book.
     */
    public void addRole(Role role) {
        roles.add(role);
    }

    /**
     * Replaces {@code target} with {@code editedRole} and updates all associated candidate titles.
     * If the title changes, every candidate with the old title is updated to the new title;
     * their other details are unchanged. Role validation precedes all changes.
     */
    public void setRole(Role target, Role editedRole) {
        requireAllNonNull(target, editedRole);
        roles.setRole(target, editedRole);
        if (!target.getTitle().equals(editedRole.getTitle())) {
            List<Person> updatedPersons = persons.asUnmodifiableObservableList().stream()
                    .map(person -> person.getRole().isSameTitle(target.getTitle())
                            ? person.withRole(editedRole.getTitle()) : person)
                    .toList();
            // The existing list is unique and only role titles change, so duplicate checks still pass.
            persons.setPersons(updatedPersons);
        }
    }

    /**
     * Removes {@code role} from the address book.
     * The role must exist and have no associated candidates.
     */
    public void removeRole(Role role) {
        roles.remove(role);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("roles", roles)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Role> getRoleList() {
        return roles.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && roles.equals(otherAddressBook.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, roles);
    }
}
