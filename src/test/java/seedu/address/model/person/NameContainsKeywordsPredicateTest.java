package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameContainsKeywordsPredicateTest {
    private final Person alice = new PersonBuilder().withName("Alice Bob").withRole("Software Engineer")
            .withStatus(Status.INTERVIEW).build();

    @Test
    public void constructorAndTest_rejectNullInputs() {
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(null));
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(null, List.of(), Set.of()));
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(List.of(), null, Set.of()));
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(List.of(), List.of(), null));
        assertThrows(NullPointerException.class, () -> new NameContainsKeywordsPredicate(List.of()).test(null));
    }

    @Test
    public void test_emptyFilter_matchesAll() {
        assertTrue(new NameContainsKeywordsPredicate(List.of()).test(alice));
    }

    @Test
    public void test_nameKeywords_matchAnyWholeWordIgnoringCase() {
        assertTrue(new NameContainsKeywordsPredicate(List.of("aLIce")).test(alice));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Carol", "bOB")).test(alice));
        assertFalse(new NameContainsKeywordsPredicate(List.of("Ali")).test(alice));
        assertFalse(new NameContainsKeywordsPredicate(List.of("Carol")).test(alice));
        assertFalse(new NameContainsKeywordsPredicate(List.of("Software", "Engineer")).test(alice));
    }

    @Test
    public void test_roleKeywords_matchAssignedTitle() {
        assertTrue(new NameContainsKeywordsPredicate(List.of(), List.of("Designer", "sOFTware"), Set.of()).test(alice));
        assertFalse(new NameContainsKeywordsPredicate(List.of(), List.of("Soft"), Set.of()).test(alice));
        assertFalse(new NameContainsKeywordsPredicate(List.of(), List.of("Designer"), Set.of()).test(alice));
    }

    @Test
    public void test_statusFilter_usesCandidateStatus() {
        for (Status status : Status.values()) {
            Person person = new PersonBuilder(alice).withStatus(status).build();
            assertTrue(new NameContainsKeywordsPredicate(List.of(), List.of(), Set.of(status)).test(person));
            assertEquals(status == Status.INTERVIEW,
                    new NameContainsKeywordsPredicate(List.of(), List.of(), Set.of(Status.INTERVIEW)).test(person));
        }
        assertTrue(new NameContainsKeywordsPredicate(List.of(), List.of(),
                Set.of(Status.HIRED, Status.INTERVIEW)).test(alice));
    }

    @Test
    public void test_mixedFilters_useOrAcrossFields() {
        assertTrue(new NameContainsKeywordsPredicate(List.of("Alice"), List.of("Designer"),
                Set.of(Status.HIRED)).test(alice));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Carol"), List.of("Engineer"),
                Set.of(Status.HIRED)).test(alice));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Carol"), List.of("Designer"),
                Set.of(Status.INTERVIEW)).test(alice));
        assertFalse(new NameContainsKeywordsPredicate(List.of("Carol"), List.of("Designer"),
                Set.of(Status.HIRED)).test(alice));
    }

    @Test
    public void constructor_copiesFilters() {
        List<String> names = new ArrayList<>();
        List<String> roles = new ArrayList<>();
        Set<Status> statuses = new HashSet<>();
        names.add("Carol");
        roles.add("Designer");
        statuses.add(Status.HIRED);
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(names, roles, statuses);
        names.add("Alice");
        roles.add("Engineer");
        statuses.add(Status.INTERVIEW);
        assertFalse(predicate.test(alice));
    }

    @Test
    public void equals_comparesAllFilters() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of("Engineer"), Set.of(Status.INTERVIEW));
        assertTrue(predicate.equals(predicate));
        assertEquals(predicate, new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of("Engineer"), Set.of(Status.INTERVIEW)));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(1));
        assertFalse(predicate.equals(new NameContainsKeywordsPredicate(
                List.of("Carol"), List.of("Engineer"), Set.of(Status.INTERVIEW))));
        assertFalse(predicate.equals(new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of("Designer"), Set.of(Status.INTERVIEW))));
        assertFalse(predicate.equals(new NameContainsKeywordsPredicate(
                List.of("Alice"), List.of("Engineer"), Set.of(Status.HIRED))));
    }

    @Test
    public void getFilterDescription_listsCandidateFilters() {
        assertEquals("", new NameContainsKeywordsPredicate(List.of()).getFilterDescription());
        assertEquals("Name: Alice, Bob; Role: Software Engineer; Candidate status: HIRED, INTERVIEW",
                new NameContainsKeywordsPredicate(List.of("Alice", "Bob"), List.of("Software", "Engineer"),
                        Set.of(Status.INTERVIEW, Status.HIRED)).getFilterDescription());
        assertFalse(new NameContainsKeywordsPredicate(List.of("Alice")).hasFieldFilters());
        assertTrue(new NameContainsKeywordsPredicate(List.of(), List.of("Engineer"), Set.of()).hasFieldFilters());
        assertTrue(new NameContainsKeywordsPredicate(List.of(), List.of(), Set.of(Status.HIRED)).hasFieldFilters());
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        assertEquals(NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}",
                new NameContainsKeywordsPredicate(keywords).toString());
    }
}
