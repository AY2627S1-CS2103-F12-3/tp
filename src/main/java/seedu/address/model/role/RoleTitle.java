package seedu.address.model.role;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a role title in RecruiterBuddy.
 * Guarantees: immutable; valid as declared in {@link #isValidRoleTitle(String)}.
 */
public class RoleTitle {

    public static final String MESSAGE_CONSTRAINTS =
            "Role title must be 1–100 characters and contain a letter.";

    private static final int MAX_LENGTH = 100;

    public final String value;

    /**
     * Constructs a {@code RoleTitle}.
     *
     * @param title A valid role title.
     */
    public RoleTitle(String title) {
        requireNonNull(title);
        String trimmedTitle = title.strip();
        checkArgument(isValidRoleTitle(trimmedTitle), MESSAGE_CONSTRAINTS);
        value = trimmedTitle;
    }

    /**
     * Returns true if {@code test} is a valid role title.
     */
    public static boolean isValidRoleTitle(String test) {
        requireNonNull(test);
        long length = test.codePoints().count();
        return length >= 1
                && length <= MAX_LENGTH
                && test.codePoints().anyMatch(Character::isLetter)
                && test.codePoints().allMatch(RoleTitle::isAllowedCharacter);
    }

    private static boolean isAllowedCharacter(int codePoint) {
        return Character.isLetterOrDigit(codePoint)
                || codePoint == ' '
                || codePoint == '\''
                || codePoint == '&'
                || codePoint == '-'
                || codePoint == '/'
                || codePoint == '.';
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RoleTitle otherRoleTitle)) {
            return false;
        }

        return value.equals(otherRoleTitle.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
