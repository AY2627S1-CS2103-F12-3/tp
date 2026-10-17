package seedu.address.model.person;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Enum denoting the different possible candidate statuses.
 */
public enum Status {
    RESUME("Pending Resume Screening", true),
    INTERVIEW("Pending Recruiter Interview", true),
    OFFER("Pending Offer Extension", true),
    DECISION("Pending Offer Decision", true),
    HIRED("Hired", false),
    REJECTED("Rejected", false),
    WITHDRAWN("Withdrawn", false);

    public static final String MESSAGE_CONSTRAINTS_START = "Status must be one of the following: ";
    private final String displayName;
    private final boolean isPending;

    Status(String displayName, boolean isPending) {
        this.displayName = displayName;
        this.isPending = isPending;
    }

    /**
     * Returns true if the candidate still needs a final recruitment outcome.
     */
    public boolean isPending() {
        return this.isPending;
    }

    /**
     * Returns an error message listing for when a status does not match any of the existing values.
     *
     * @return String listing values of Status.
     */
    public static String getConstraintsMessage() {
        Status[] values = Status.values();
        String names = Arrays.stream(values)
                .map(Status::name)
                .collect(Collectors.joining(", "));
        return MESSAGE_CONSTRAINTS_START + names;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
