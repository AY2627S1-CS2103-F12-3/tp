package seedu.address.model.status;

/**
 * Enum denoting the different possible candidate statuses.
 */
public enum Status {
    PENDING_RESUME_SCREENING("Pending resume screening"),
    PENDING_RECRUITER_INTERVIEW("Pending recruiter interview"),
    PENDING_OFFER_EXTENSION("Pending offer extension"),
    HIRED("Hired"),
    REJECTED("Rejected");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
