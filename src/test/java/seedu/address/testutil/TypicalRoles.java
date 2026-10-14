package seedu.address.testutil;

import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.role.Role;
import seedu.address.model.role.RoleStatus;

/**
 * Provides typical roles for tests.
 */
public class TypicalRoles {

    public static final Role SOFTWARE_ENGINEER = new RoleBuilder().build();
    public static final Role PRODUCT_ANALYST = new RoleBuilder()
            .withTitle("Product Analyst")
            .withExperienceLevel("Entry level")
            .build();
    public static final Role CLOSED_DESIGNER = new RoleBuilder()
            .withTitle("Product Designer")
            .withExperienceLevel("Senior")
            .withStatus(RoleStatus.CLOSED)
            .build();

    private TypicalRoles() {
    }

    /**
     * Returns the typical roles.
     */
    public static List<Role> getTypicalRoles() {
        return List.of(SOFTWARE_ENGINEER, PRODUCT_ANALYST, CLOSED_DESIGNER);
    }

    /**
     * Returns an address book containing typical roles.
     */
    public static AddressBook getTypicalRoleAddressBook() {
        return new AddressBookBuilder()
                .withRole(SOFTWARE_ENGINEER)
                .withRole(PRODUCT_ANALYST)
                .withRole(CLOSED_DESIGNER)
                .build();
    }
}
