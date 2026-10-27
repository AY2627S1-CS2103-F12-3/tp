package seedu.address.model;

import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleTitle;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;
    /** {@code Predicate} that always evaluates to true */
    Predicate<Role> PREDICATE_SHOW_ALL_ROLES = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Records a snapshot before a mutating command is executed. */
    void recordAddressBookState();

    /** Clears redo history after a mutating command has executed successfully. */
    default void commitAddressBookState() {
        // Models that support history may override this method.
    }

    /** Restores the previous recorded address book state. */
    boolean undoAddressBook();

    /** Restores the next address book state after an undo. */
    boolean redoAddressBook();

    /** Removes the most recently recorded snapshot when command execution fails. */
    void discardAddressBookState();

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Returns a person in the address book with the same identity as {@code person}, if any.
     */
    Optional<Person> findSamePerson(Person person);

    /**
     * Returns a person in the address book, other than {@code excludedPerson}, with the same identity as
     * {@code person}, if any.
     */
    Optional<Person> findSamePersonExcluding(Person person, Person excludedPerson);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    /**
     * Returns true if a role with the same identity as {@code role} exists in the address book.
     */
    boolean hasRole(Role role);

    /**
     * Returns the role whose title matches {@code title}, ignoring case, if there is one.
     */
    Optional<Role> findRole(RoleTitle title);

    /**
     * Adds {@code role} to the address book.
     */
    void addRole(Role role);

    /**
     * Deletes {@code role} from the address book.
     * The role must exist and have no associated candidates.
     */
    void deleteRole(Role role);

    /**
     * Replaces {@code target} with {@code editedRole}, updating every associated candidate's role title.
     */
    void setRole(Role target, Role editedRole);

    /**
     * Returns an unmodifiable view of the filtered role list.
     */
    ObservableList<Role> getFilteredRoleList();

    /**
     * Updates the filter of the filtered role list to filter by {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredRoleList(Predicate<Role> predicate);
}
