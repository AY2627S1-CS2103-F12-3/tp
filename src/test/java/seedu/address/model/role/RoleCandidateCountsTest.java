package seedu.address.model.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.testutil.PersonBuilder;

public class RoleCandidateCountsTest {
    private static final RoleTitle ROLE_TITLE = new RoleTitle("Software Engineer");

    @Test
    public void constructor_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RoleCandidateCounts(null, List.of()));
        assertThrows(NullPointerException.class, () -> new RoleCandidateCounts(ROLE_TITLE, null));
    }

    @Test
    public void constructor_noCandidates_returnsZeros() {
        assertCounts(new RoleCandidateCounts(ROLE_TITLE, List.of()), 0, 0, 0, 0);
    }

    @Test
    public void constructor_eachStatus_countsExactlyOneCategory() {
        for (Status status : Status.values()) {
            Person candidate = new PersonBuilder().withStatus(status).build();
            assertCounts(new RoleCandidateCounts(ROLE_TITLE, List.of(candidate)), status.isPending() ? 1 : 0,
                    status == Status.HIRED ? 1 : 0, status == Status.REJECTED ? 1 : 0,
                    status == Status.WITHDRAWN ? 1 : 0);
        }
    }

    @Test
    public void constructor_mixedRolesAndStatuses_countsOnlyAssociatedCandidates() {
        List<Person> candidates = new ArrayList<>();
        for (Status status : Status.values()) {
            candidates.add(new PersonBuilder().withName("Candidate " + status.ordinal()).withStatus(status).build());
            candidates.add(new PersonBuilder().withName("Other Candidate " + status.ordinal())
                    .withRole("Product Analyst").withStatus(status).build());
        }

        assertCounts(new RoleCandidateCounts(ROLE_TITLE, candidates), 4, 1, 1, 1);
    }

    @Test
    public void constructor_titleCaseAndSurroundingSpaces_matchButInternalSpacesRemainSignificant() {
        List<Person> candidates = List.of(new PersonBuilder().withRole(" software engineer ").build(),
                new PersonBuilder().withRole("SOFTWARE ENGINEER").build(),
                new PersonBuilder().withRole("Software  Engineer").build());

        assertCounts(new RoleCandidateCounts(ROLE_TITLE, candidates), 2, 0, 0, 0);
    }

    @Test
    public void constructor_multipleHires_reportsActualDataWithoutCappingOrMutating() {
        List<Person> candidates = List.of(new PersonBuilder().withName("Alice").withStatus(Status.HIRED).build(),
                new PersonBuilder().withName("Bob").withStatus(Status.HIRED).build());

        assertCounts(new RoleCandidateCounts(ROLE_TITLE, candidates), 0, 2, 0, 0);
        assertEquals(Status.HIRED, candidates.getFirst().getStatus());
        assertEquals(Status.HIRED, candidates.getLast().getStatus());
    }

    @Test
    public void constructor_sourceChanges_doesNotChangeExistingSnapshot() {
        List<Person> candidates = new ArrayList<>();
        candidates.add(new PersonBuilder().build());
        RoleCandidateCounts snapshot = new RoleCandidateCounts(ROLE_TITLE, candidates);

        candidates.clear();
        candidates.add(new PersonBuilder().withStatus(Status.REJECTED).build());

        assertCounts(snapshot, 1, 0, 0, 0);
        assertCounts(new RoleCandidateCounts(ROLE_TITLE, candidates), 0, 0, 1, 0);
    }

    private void assertCounts(RoleCandidateCounts counts, int pending, int hired, int rejected, int withdrawn) {
        assertEquals(pending, counts.getPendingCount());
        assertEquals(hired, counts.getHiredCount());
        assertEquals(rejected, counts.getRejectedCount());
        assertEquals(withdrawn, counts.getWithdrawnCount());
    }
}
