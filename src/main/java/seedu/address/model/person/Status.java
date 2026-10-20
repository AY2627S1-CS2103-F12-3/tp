package seedu.address.model.person;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Enum denoting the different possible candidate statuses.
 */
public enum Status {
    PENDING_RESUME_SCREENING("Pending Resume Screening"),
    PENDING_RECRUITER_INTERVIEW("Pending Recruiter Interview"),
    PENDING_OFFER_EXTENSION("Pending Offer Extension"),
    PENDING_OFFER_DECISION("Pending Offer Decision"),
    HIRED("Hired"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn");

    public static final String MESSAGE_CONSTRAINTS_START = "Status must be one of the following: ";
    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns true if the candidate still needs a final recruitment outcome.
     */
    public boolean isPending() {
        return this != HIRED && this != REJECTED && this != WITHDRAWN;
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
