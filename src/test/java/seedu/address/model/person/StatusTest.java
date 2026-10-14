package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.StringJoiner;

import org.junit.jupiter.api.Test;

public class StatusTest {

    @Test
    public void getConstraintsMessage_allStatuses_returnsFormattedMessage() {
        StringJoiner statusNames = new StringJoiner(", ");
        for (Status status : Status.values()) {
            statusNames.add(status.name());
        }
        String expectedMessage = Status.MESSAGE_CONSTRAINTS_START + statusNames;

        assertEquals(expectedMessage, Status.getConstraintsMessage());
    }

    @Test
    public void toStringMethod() {
        assertEquals("Pending Resume Screening", Status.PENDING_RESUME_SCREENING.toString());
        assertEquals("Pending Recruiter Interview", Status.PENDING_RECRUITER_INTERVIEW.toString());
        assertEquals("Pending Offer Extension", Status.PENDING_OFFER_EXTENSION.toString());
        assertEquals("Hired", Status.HIRED.toString());
        assertEquals("Rejected", Status.REJECTED.toString());
    }
}
