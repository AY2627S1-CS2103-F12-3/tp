package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StatusTest {

    @Test
    public void toStringMethod() {
        assertEquals("Pending resume screening", Status.PENDING_RESUME_SCREENING.toString());
        assertEquals("Pending recruiter interview", Status.PENDING_RECRUITER_INTERVIEW.toString());
        assertEquals("Pending offer extension", Status.PENDING_OFFER_EXTENSION.toString());
        assertEquals("Hired", Status.HIRED.toString());
        assertEquals("Rejected", Status.REJECTED.toString());
    }
}
