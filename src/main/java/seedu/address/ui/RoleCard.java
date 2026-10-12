package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.role.Role;

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

    /**
     * Creates a role card for the given role.
     *
     * @param role role to display
     * @param displayedIndex index shown on the card
     */
    public RoleCard(Role role, int displayedIndex) {
        super(FXML);
        id.setText(displayedIndex + ". ");
        title.setText(role.getTitle().value);
        experienceLevel.setText("Experience: " + role.getExperienceLevel().value);
        status.setText("Status: " + role.getStatus().toString());
    }
}
