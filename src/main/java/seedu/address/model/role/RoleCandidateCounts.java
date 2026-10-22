package seedu.address.model.role;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import seedu.address.model.person.Person;
import seedu.address.model.person.Status;

/**
 * An immutable snapshot of the candidate-status totals for one role.
 * Counts are derived from candidate records and are not stored separately in the data file.
 */
public class RoleCandidateCounts {
    private final Map<Status, Integer> statusCounts;

    /**
     * Counts candidates associated with {@code roleTitle}, ignoring title case.
     * The caller supplies all candidates, not a filtered view of the candidate list.
     */
    public RoleCandidateCounts(RoleTitle roleTitle, Collection<Person> candidates) {
        requireAllNonNull(roleTitle, candidates);
        Map<Status, Integer> counts = new EnumMap<>(Status.class);
        for (Person candidate : candidates) {
            if (roleTitle.isSameTitle(candidate.getRole())) {
                counts.merge(candidate.getStatus(), 1, Integer::sum);
            }
        }
        statusCounts = Map.copyOf(counts);
    }

    public int getPendingCount() {
        return statusCounts.entrySet().stream()
                .filter(entry -> entry.getKey().isPending())
                .mapToInt(Map.Entry::getValue)
                .sum();
    }

    public int getHiredCount() {
        return statusCounts.getOrDefault(Status.HIRED, 0);
    }

    public int getRejectedCount() {
        return statusCounts.getOrDefault(Status.REJECTED, 0);
    }

    public int getWithdrawnCount() {
        return statusCounts.getOrDefault(Status.WITHDRAWN, 0);
    }
}
