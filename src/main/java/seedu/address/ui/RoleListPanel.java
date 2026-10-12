package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.role.Role;

/** Panel containing the list of roles. */
public class RoleListPanel extends UiPart<Region> {
    private static final String FXML = "RoleListPanel.fxml";

    @FXML
    private ListView<Role> roleListView;

    /**
     * Creates a panel containing the given roles.
     *
     * @param roleList roles to display
     */
    public RoleListPanel(ObservableList<Role> roleList) {
        super(FXML);
        roleListView.setItems(roleList);
        roleListView.setCellFactory(listView -> new RoleListViewCell());
    }

    private static class RoleListViewCell extends ListCell<Role> {
        @Override
        protected void updateItem(Role role, boolean empty) {
            super.updateItem(role, empty);
            setGraphic(empty || role == null ? null : new RoleCard(role, getIndex() + 1).getRoot());
            setText(null);
        }
    }
}
