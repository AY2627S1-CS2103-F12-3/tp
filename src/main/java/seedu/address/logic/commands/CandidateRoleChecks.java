package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Status;
import seedu.address.model.role.RoleTitle;

/**
 * Checks recruitment rules shared by candidate-role transfers and status updates.
 */
final class CandidateRoleChecks {

    static final String MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE = "%1$s already has a hired candidate.";
    static final String MESSAGE_CANDIDATES_HIRED_SAME_ROLE = "Cannot hire two candidates for the %1$s role.";

    private CandidateRoleChecks() {
    }

    /**
     * Checks that the proposed role and status will not fill the role more than once.
     * Excludes the original candidate, even when other fields are being edited at the same time.
     *
     * @throws CommandException if another stored candidate for the destination role is already hired.
     */
    static void verifyHiringAllowed(Model model, Person originalCandidate, RoleTitle destination, Status status)
            throws CommandException {
        if (status != Status.HIRED) {
            return;
        }
        boolean hasOtherHiredCandidate = model.getAddressBook().getPersonList().stream()
                .anyMatch(candidate -> !candidate.isSamePerson(originalCandidate)
                        && candidate.getStatus() == Status.HIRED
                        && candidate.getRole().isSameTitle(destination));
        if (hasOtherHiredCandidate) {
            throw new CommandException(String.format(MESSAGE_ROLE_ALREADY_HAS_HIRED_CANDIDATE, destination));
        }
    }
}
