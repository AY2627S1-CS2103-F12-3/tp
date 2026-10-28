package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.RoleBuilder;

public class NameContainsKeywordsPredicateTest {

    @Test
    public void constructorAndTest_rejectNullInputs() {
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(null));
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(
                List.of(), null, Set.of(), List.of()));
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(
                List.of(), List.of(), null, List.of()));
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(
                List.of(), List.of(), Set.of(), null));
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(List.of()).test(null));
    }

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // An empty filter matches every person, as used by bare find.
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but do not match name
        predicate = new NameContainsKeywordsPredicate(List.of("12345678", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345678")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void test_noFilters_matchesAllPersons() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }

    @Test
    public void test_roleFilters_matchRoleAndStatusAndTag() {
        Role openRole = new RoleBuilder().withTitle("Software Engineer").withStatus(RoleStatus.OPEN).build();
        Person person = new PersonBuilder().withName("Alice").withTags("SoftwareEngineer").build();
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of("Software", "Engineer"), Set.of(RoleStatus.OPEN), List.of(openRole));
        assertTrue(predicate.test(person));
        assertTrue(predicate.hasRoleFilters());
        assertTrue(predicate.withRoles(List.of(openRole)).test(person));
    }

    @Test
    public void test_roleFilters_eachOptionalFilterPath() {
        Role role = new RoleBuilder().withTitle("Engineer").withStatus(RoleStatus.OPEN).build();
        Person person = new PersonBuilder().withName("Alice").withRole("Engineer").withTags("Engineer").build();
        assertTrue(new NameContainsKeywordsPredicate(List.of("Alice"), List.of(), Set.of(), List.of(role))
                .test(person));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Alice"), List.of("Engineer"), Set.of(), List.of(role))
                .test(person));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Alice"), List.of(),
                Set.of(RoleStatus.OPEN), List.of(role))
                .test(person));
    }

    @Test
    public void equals_roleFiltersDiffer_returnsFalse() {
        NameContainsKeywordsPredicate rolePredicate = new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of("Engineer"), Set.of(RoleStatus.OPEN), List.of());
        NameContainsKeywordsPredicate statusPredicate = new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of("Engineer"), Set.of(RoleStatus.CLOSED), List.of());
        assertFalse(rolePredicate.equals(statusPredicate));
    }

    @Test
    public void test_roleFilters_mismatch() {
        Role closedRole = new RoleBuilder().withTitle("Software Engineer").withStatus(RoleStatus.CLOSED).build();
        Person person = new PersonBuilder().withName("Alice").withTags("OtherRole").build();
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of("Software"), Set.of(RoleStatus.OPEN), List.of(closedRole));
        assertFalse(predicate.test(person));
        assertFalse(new NameContainsKeywordsPredicate(List.of("Alice")).hasRoleFilters());
    }

    @Test
    public void test_roleFilters_withoutName() {
        Role openRole = new RoleBuilder().withTitle("Software Engineer").withStatus(RoleStatus.OPEN).build();
        Person person = new PersonBuilder().withName("Alice").withRole("Software Engineer").build();
        assertTrue(new NameContainsKeywordsPredicate(List.of(), List.of("Software", "Engineer"),
                Set.of(RoleStatus.OPEN), List.of(openRole)).test(person));
    }

    @Test
    public void test_roleFilters_usesAssignedRole() {
        Role openRole = new RoleBuilder().withTitle("Software Engineer").withStatus(RoleStatus.OPEN).build();
        Person person = new PersonBuilder().withName("Alice").withRole("Designer")
                .withTags("SoftwareEngineer").build();
        assertFalse(new NameContainsKeywordsPredicate(List.of(), List.of("Software", "Engineer"),
                Set.of(RoleStatus.OPEN), List.of(openRole)).test(person));
    }

    @Test
    public void test_roleFilters_hasRoleFiltersForRoleAndStatus() {
        Role openRole = new RoleBuilder().withTitle("Software Engineer").withStatus(RoleStatus.OPEN).build();
        Person person = new PersonBuilder().withRole("Software Engineer").build();
        assertTrue(new NameContainsKeywordsPredicate(List.of(), List.of("Software"), Set.of(), List.of(openRole))
                .hasRoleFilters());
        assertTrue(new NameContainsKeywordsPredicate(List.of(), List.of(), Set.of(RoleStatus.OPEN), List.of(openRole))
                .hasRoleFilters());
        assertTrue(new NameContainsKeywordsPredicate(List.of(), List.of("Software"), Set.of(RoleStatus.OPEN),
                List.of(openRole)).test(person));
    }

    @Test
    public void test_roleFilters_mismatchedTitleAndAssignedRole() {
        Role openRole = new RoleBuilder().withTitle("Software Engineer").withStatus(RoleStatus.OPEN).build();
        Person person = new PersonBuilder().withRole("Software Engineer").build();
        assertFalse(new NameContainsKeywordsPredicate(List.of(), List.of("Designer"), Set.of(), List.of(openRole))
                .test(person));
        assertFalse(new NameContainsKeywordsPredicate(List.of(), List.of("Software"), Set.of(),
                List.of(openRole)).test(new PersonBuilder().withRole("Designer").build()));
    }

    @Test
    public void equals_roleKeywordsDiffer_returnsFalse() {
        assertFalse(new NameContainsKeywordsPredicate(List.of("Alice"), List.of("Engineer"), Set.of(), List.of())
                .equals(new NameContainsKeywordsPredicate(List.of("Alice"), List.of("Designer"), Set.of(),
                        List.of())));
    }
}
