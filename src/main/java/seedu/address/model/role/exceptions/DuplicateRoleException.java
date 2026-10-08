package seedu.address.model.role.exceptions;

/**
 * Signals that an operation would create a duplicate role.
 */
public class DuplicateRoleException extends RuntimeException {
    public DuplicateRoleException() {
        super("Operation would result in duplicate roles");
    }
}
