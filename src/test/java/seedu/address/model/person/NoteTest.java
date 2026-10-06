package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void constructor_anyText_success() {
        assertEquals("", new Note("").value); // empty note
        assertEquals("   ", new Note("   ").value); // spaces only
        assertEquals("Interview 2026-10-20 14:00, scored 8/10!",
                new Note("Interview 2026-10-20 14:00, scored 8/10!").value); // symbols and digits
    }

    @Test
    public void equals() {
        Note note = new Note("Strong Java skills");

        // same values -> returns true
        assertTrue(note.equals(new Note("Strong Java skills")));

        // same object -> returns true
        assertTrue(note.equals(note));

        // null -> returns false
        assertFalse(note.equals(null));

        // different types -> returns false
        assertFalse(note.equals("Strong Java skills"));

        // different values -> returns false
        assertFalse(note.equals(new Note("Weak Java skills")));

        // different case -> returns false
        assertFalse(note.equals(new Note("strong java skills")));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new Note("Strong Java skills").hashCode(), new Note("Strong Java skills").hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("Strong Java skills", new Note("Strong Java skills").toString());
    }

}
