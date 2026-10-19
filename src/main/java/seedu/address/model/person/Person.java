package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.role.RoleTitle;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    /** Status of a newly added candidate, and of a saved candidate that has no status. */
    public static final Status DEFAULT_STATUS = Status.PENDING_RESUME_SCREENING;

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final RoleTitle role;
    private final Note note;
    private final Status status;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, RoleTitle role, Note note,
            Status status, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, role, note, status, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.role = role;
        this.note = note;
        this.status = status;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns the title of the role opening this candidate is applying for.
     */
    public RoleTitle getRole() {
        return role;
    }

    /**
     * Returns a copy of this candidate with {@code newRole} as its role.
     */
    public Person withRole(RoleTitle newRole) {
        return new Person(name, phone, email, address, newRole, note, status, tags);
    }

    public Note getNote() {
        return note;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same email (ignoring case) or the same phone number.
     * Names are not compared, as different candidates can share a name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && (hasSameEmail(otherPerson) || hasSamePhone(otherPerson));
    }

    /**
     * Returns true if both persons have the same email, ignoring case.
     */
    public boolean hasSameEmail(Person otherPerson) {
        requireNonNull(otherPerson);
        return email.isSameEmail(otherPerson.email);
    }

    /**
     * Returns true if both persons have the same phone number.
     * Numbers are compared as saved, so {@code 6591234567} and {@code 91234567} are different numbers.
     */
    public boolean hasSamePhone(Person otherPerson) {
        requireNonNull(otherPerson);
        return phone.equals(otherPerson.phone);
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && role.equals(otherPerson.role)
                && note.equals(otherPerson.note)
                && status == otherPerson.status
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, role, note, status, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("role", role)
                .add("note", note)
                .add("status", status)
                .add("tags", tags)
                .toString();
    }

}
