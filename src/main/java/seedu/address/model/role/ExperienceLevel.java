package seedu.address.model.role;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the experience level required for a role.
 * Guarantees: immutable; valid as declared in {@link #isValidExperienceLevel(String)}.
 */
public class ExperienceLevel {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 50;

    public static final String MESSAGE_CONSTRAINTS =
            "Required experience level must be " + MIN_LENGTH + "–" + MAX_LENGTH
                    + " characters and must not contain a new line.";

    public final String value;

    /**
     * Constructs an {@code ExperienceLevel}.
     *
     * @param experienceLevel A valid required experience level.
     */
    public ExperienceLevel(String experienceLevel) {
        requireNonNull(experienceLevel);
        String trimmedExperienceLevel = experienceLevel.strip();
        checkArgument(isValidExperienceLevel(trimmedExperienceLevel), MESSAGE_CONSTRAINTS);
        value = trimmedExperienceLevel;
    }

    /**
     * Returns true if {@code test} is a valid required experience level.
     */
    public static boolean isValidExperienceLevel(String test) {
        requireNonNull(test);
        long length = test.codePoints().count();
        return length >= MIN_LENGTH
                && length <= MAX_LENGTH
                && test.codePoints().allMatch(ExperienceLevel::isPrintableCharacter);
    }

    private static boolean isPrintableCharacter(int codePoint) {
        int characterType = Character.getType(codePoint);
        return characterType != Character.CONTROL
                && characterType != Character.FORMAT
                && characterType != Character.SURROGATE
                && characterType != Character.UNASSIGNED
                && characterType != Character.LINE_SEPARATOR
                && characterType != Character.PARAGRAPH_SEPARATOR;
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
        if (!(other instanceof ExperienceLevel otherExperienceLevel)) {
            return false;
        }

        return value.equals(otherExperienceLevel.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
