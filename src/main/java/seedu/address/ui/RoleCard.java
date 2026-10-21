package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleCandidateCounts;

/** A UI component that displays information about a role. */
public class RoleCard extends UiPart<Region> {
    private static final String FXML = "RoleCard.fxml";

    @FXML
    private Label id;
    @FXML
    private Label title;
    @FXML
    private Label experienceLevel;
    @FXML
    private Label status;
    @FXML
    private Label pendingCount;
    @FXML
    private Label hiredCount;
    @FXML
    private Label rejectedCount;
    @FXML
    private Label withdrawnCount;

    /**
     * Creates a role card for the given role.
     *
     * @param role role to display
     * @param displayedIndex index shown on the card
     * @param counts candidate-status totals for this role
     */
    public RoleCard(Role role, int displayedIndex, RoleCandidateCounts counts) {
        super(FXML);
        id.setText(displayedIndex + ". ");
        title.setText(role.getTitle().value);
        experienceLevel.setText("Experience: " + role.getExperienceLevel().value);
        status.setText("Status: " + role.getStatus().toString());
        pendingCount.setText("Pending: " + counts.getPendingCount());
        hiredCount.setText("Hired: " + counts.getHiredCount());
        rejectedCount.setText("Rejected: " + counts.getRejectedCount());
        withdrawnCount.setText("Withdrawn: " + counts.getWithdrawnCount());
    }
}
