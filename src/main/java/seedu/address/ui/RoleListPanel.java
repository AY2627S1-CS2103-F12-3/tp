package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleCandidateCounts;

/** Panel containing the list of roles. */
public class RoleListPanel extends UiPart<Region> {
    private static final String FXML = "RoleListPanel.fxml";

    @FXML
    private ListView<Role> roleListView;

    private final ObservableList<Person> candidates;

    /**
     * Creates a panel containing the given roles.
     *
     * @param roleList roles to display
     * @param candidates unfiltered, observable candidate records
     */
    public RoleListPanel(ObservableList<Role> roleList, ObservableList<Person> candidates) {
        super(FXML);
        this.candidates = requireNonNull(candidates);
        roleListView.setItems(roleList);
        roleListView.setCellFactory(listView -> new RoleListViewCell());
        // One panel-lifetime listener avoids retaining listeners for recycled list cells.
        candidates.addListener((ListChangeListener<Person>) change -> roleListView.refresh());
    }

    private class RoleListViewCell extends ListCell<Role> {
        @Override
        protected void updateItem(Role role, boolean empty) {
            super.updateItem(role, empty);
            setGraphic(empty || role == null ? null : createRoleCard(role));
            setText(null);
        }

        private Region createRoleCard(Role role) {
            Region card = new RoleCard(role, getIndex() + 1,
                    new RoleCandidateCounts(role.getTitle(), candidates)).getRoot();
            card.prefWidthProperty().bind(Bindings.createDoubleBinding(this::getAvailableCardWidth,
                    widthProperty(), insetsProperty()));
            card.maxWidthProperty().bind(card.prefWidthProperty());
            return card;
        }

        private double getAvailableCardWidth() {
            return Math.max(0, getWidth() - getInsets().getLeft() - getInsets().getRight());
        }
    }
}
