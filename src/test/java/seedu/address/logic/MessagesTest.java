package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class MessagesTest {

    private static final String REQUIRED_FIELDS = "Amy Bee; Phone: 85355255; Email: amy@gmail.com; "
            + "Address: 123, Jurong West Ave 6, #08-111; Role: Software Engineer";

    @Test
    public void format_noOptionalFields_omitsOptionalLabels() {
        Person person = new PersonBuilder().build();

        assertEquals(REQUIRED_FIELDS, Messages.format(person));
    }

    @Test
    public void format_allOptionalFields_includesEachOptionalField() {
        Person person = new PersonBuilder()
                .withNote("Prefers mornings")
                .withInterview("2026-11-20 14:00")
                .withTags("referral")
                .build();

        assertEquals(REQUIRED_FIELDS + "; Note: Prefers mornings; Interview: 2026-11-20 14:00; Tags: [referral]",
                Messages.format(person));
    }
}
