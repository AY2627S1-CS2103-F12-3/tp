package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class NameContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;
    private final List<String> roleKeywords;
    private final Set<RoleStatus> roleStatuses;
    private final List<Role> roles;

    /** Creates a predicate that matches candidate names against the supplied keywords. */
    public NameContainsKeywordsPredicate(List<String> keywords) {
        this(keywords, List.of(), Set.of(), List.of());
    }

    /** Creates a predicate with name, role, status, and role data filters. */
    public NameContainsKeywordsPredicate(List<String> keywords, List<String> roleKeywords,
            Set<RoleStatus> roleStatuses, List<Role> roles) {
        requireNonNull(keywords);
        requireNonNull(roleKeywords);
        requireNonNull(roleStatuses);
        requireNonNull(roles);
        this.keywords = keywords;
        this.roleKeywords = roleKeywords;
        this.roleStatuses = roleStatuses;
        this.roles = roles;
    }

    public NameContainsKeywordsPredicate withRoles(List<Role> roles) {
        return new NameContainsKeywordsPredicate(keywords, roleKeywords, roleStatuses, roles);
    }

    public boolean hasRoleFilters() {
        return !roleKeywords.isEmpty() || !roleStatuses.isEmpty();
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        boolean nameMatches = !keywords.isEmpty() && keywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(person.getName().fullName, keyword));
        boolean hasAnyFilter = !keywords.isEmpty() || hasRoleFilters();
        boolean roleMatches = roleKeywords.isEmpty() && roleStatuses.isEmpty()
                || roles.stream().anyMatch(role -> roleMatches(person, role));
        return !hasAnyFilter || (nameMatches || keywords.isEmpty()) && roleMatches;
    }

    private boolean roleMatches(Person person, Role role) {
        boolean titleMatches = roleKeywords.isEmpty() || roleKeywords.stream()
                .anyMatch(keyword -> StringUtil.containsWordIgnoreCase(role.getTitle().value, keyword));
        boolean statusMatches = roleStatuses.isEmpty() || roleStatuses.contains(role.getStatus());
        boolean assignedRoleMatches = person.getRole().isSameTitle(role.getTitle());
        return titleMatches && statusMatches && assignedRoleMatches;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameContainsKeywordsPredicate otherNameContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherNameContainsKeywordsPredicate.keywords)
                && roleKeywords.equals(otherNameContainsKeywordsPredicate.roleKeywords)
                && roleStatuses.equals(otherNameContainsKeywordsPredicate.roleStatuses);
    }

    @Override
    public String toString() {
        StringBuilder description = new StringBuilder();
        if (!keywords.isEmpty()) {
            description.append("name: ").append(String.join(", ", keywords));
        }
        if (!roleKeywords.isEmpty()) {
            appendFilter(description, "role: " + String.join(", ", roleKeywords));
        }
        if (!roleStatuses.isEmpty()) {
            appendFilter(description, "role status: " + roleStatuses.stream().map(Enum::name)
                    .sorted().collect(java.util.stream.Collectors.joining(", ")));
        }
        return description.length() == 0 ? "none" : description.toString();
    }

    private static void appendFilter(StringBuilder description, String filter) {
        if (description.length() > 0) {
            description.append("; ");
        }
        description.append(filter);
    }
}
