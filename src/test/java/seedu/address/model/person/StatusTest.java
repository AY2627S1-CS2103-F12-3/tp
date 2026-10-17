package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.StringJoiner;

import org.junit.jupiter.api.Test;

public class StatusTest {

    @Test
    public void isPending_recruitmentStage_returnsTrue() {
        assertTrue(Status.RESUME.isPending());
        assertTrue(Status.INTERVIEW.isPending());
        assertTrue(Status.OFFER.isPending());
        assertTrue(Status.DECISION.isPending());
    }

    @Test
    public void isPending_finalOutcome_returnsFalse() {
        assertFalse(Status.HIRED.isPending());
        assertFalse(Status.REJECTED.isPending());
        assertFalse(Status.WITHDRAWN.isPending());
    }

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
        assertEquals("Pending Resume Screening", Status.RESUME.toString());
        assertEquals("Pending Recruiter Interview", Status.INTERVIEW.toString());
        assertEquals("Pending Offer Extension", Status.OFFER.toString());
        assertEquals("Hired", Status.HIRED.toString());
        assertEquals("Rejected", Status.REJECTED.toString());
    }
}
