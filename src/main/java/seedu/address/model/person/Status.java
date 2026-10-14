package seedu.address.model.person;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Enum denoting the different possible candidate statuses.
 */
public enum Status {
    PENDING_RESUME_SCREENING("Pending resume screening"),
    PENDING_RECRUITER_INTERVIEW("Pending recruiter interview"),
    PENDING_OFFER_EXTENSION("Pending offer extension"),
    HIRED("Hired"),
    REJECTED("Rejected");

    public static final String MESSAGE_CONSTRAINTS_START = "Status must be one of the following: ";
    private final String name;

    Status(String name) {
        this.name = name;
    }

    public static String getConstraintsMessage() {
        Status[] values = Status.values();
        String names = Arrays.stream(values)
                .map(Status::toString)
                .collect(Collectors.joining(", "));
        return MESSAGE_CONSTRAINTS_START + names;
    }

    @Override
    public String toString() {
        return name;
    }
}
