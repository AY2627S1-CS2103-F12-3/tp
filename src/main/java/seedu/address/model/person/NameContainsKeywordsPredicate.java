package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Matches any supplied candidate-name keyword, role-title keyword, or candidate status.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;
    private final List<String> roleKeywords;
    private final Set<Status> statuses;

    /** Creates a predicate that matches candidate names against the supplied keywords. */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        this(keywords, List.of(), Set.of());
    }

    /** Creates a predicate combining name, role-title and candidate-status searches with OR. */
    public NameContainsKeywordsPredicate(List<String> keywords, List<String> roleKeywords,
            Set<Status> statuses) {
        this.keywords = List.copyOf(requireNonNull(keywords));
        this.roleKeywords = List.copyOf(requireNonNull(roleKeywords));
        this.statuses = Set.copyOf(requireNonNull(statuses));
    }

    public boolean hasFieldFilters() {
        return !roleKeywords.isEmpty() || !statuses.isEmpty();
    }

    /** Returns a user-facing description of the filters applied by this predicate. */
    public String getFilterDescription() {
        List<String> filters = new java.util.ArrayList<>();
        if (!keywords.isEmpty()) {
            filters.add("Name: " + String.join(", ", keywords));
        }
        if (!roleKeywords.isEmpty()) {
            filters.add("Role: " + String.join(" ", roleKeywords));
        }
        if (!statuses.isEmpty()) {
            filters.add("Candidate status: " + statuses.stream()
                    .map(Status::name)
                    .sorted()
                    .collect(Collectors.joining(", ")));
        }
        return String.join("; ", filters);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        if (keywords.isEmpty() && !hasFieldFilters()) {
            return true;
        }
        return keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword))
                || roleKeywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getRole().value, keyword))
                || statuses.contains(person.getStatus());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof NameContainsKeywordsPredicate otherPredicate)) {
            return false;
        }
        return keywords.equals(otherPredicate.keywords)
                && roleKeywords.equals(otherPredicate.roleKeywords)
                && statuses.equals(otherPredicate.statuses);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
