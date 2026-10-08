package seedu.address.model.role;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.role.exceptions.DuplicateRoleException;
import seedu.address.model.role.exceptions.RoleNotFoundException;

/**
 * Stores roles while enforcing uniqueness by {@link Role#isSameRole(Role)}.
 */
public class UniqueRoleList implements Iterable<Role> {

    private final ObservableList<Role> internalList = FXCollections.observableArrayList();
    private final ObservableList<Role> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains a role with the same identity as {@code role}.
     */
    public boolean contains(Role role) {
        requireNonNull(role);
        return internalList.stream().anyMatch(role::isSameRole);
    }

    /**
     * Adds {@code role} to the list.
     */
    public void add(Role role) {
        requireNonNull(role);
        if (contains(role)) {
            throw new DuplicateRoleException();
        }
        internalList.add(role);
    }

    /**
     * Replaces {@code target} with {@code editedRole}.
     */
    public void setRole(Role target, Role editedRole) {
        requireAllNonNull(target, editedRole);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new RoleNotFoundException();
        }

        if (!target.isSameRole(editedRole) && contains(editedRole)) {
            throw new DuplicateRoleException();
        }

        internalList.set(index, editedRole);
    }

    /**
     * Replaces the list contents with {@code roles}.
     */
    public void setRoles(List<Role> roles) {
        requireAllNonNull(roles);
        if (!rolesAreUnique(roles)) {
            throw new DuplicateRoleException();
        }
        internalList.setAll(roles);
    }

    /**
     * Returns the backing list as an unmodifiable observable list.
     */
    public ObservableList<Role> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Role> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueRoleList otherUniqueRoleList)) {
            return false;
        }

        return internalList.equals(otherUniqueRoleList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    private boolean rolesAreUnique(List<Role> roles) {
        for (int i = 0; i < roles.size() - 1; i++) {
            for (int j = i + 1; j < roles.size(); j++) {
                if (roles.get(i).isSameRole(roles.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
