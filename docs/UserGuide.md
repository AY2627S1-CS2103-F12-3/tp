---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# AB-3 User Guide

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.
   * `list role` : Lists all unique roles, with each role shown in its own box.
   * `role add r/Software Engineer x/2 years` : Adds an open role.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `...` can appear zero or more times.<br>
  For example, `[t/TAG]... ` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]... `

* Newly added persons are automatically assigned the status `Pending Resume Screening`.

<box type="tip" seamless>

**Tip:** A person can have any number of tags, including zero.
</box>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Adding a role: `role add`

Adds an open role to the address book. Role titles are unique, ignoring case.

Format: `role add r/ROLE_TITLE x/EXPERIENCE_LEVEL`

Examples:

* `role add r/Software Engineer x/2 years`
* `role add r/Product Manager x/5 years`
* `role add r/UX Designer x/3 years`

New roles are created with status `OPEN`. To view all roles, use `list role`.

### Closing a role: `role close`

Ends recruitment while retaining the role and all its candidate records.

Format: `role close INDEX`

Run `list role` first. `INDEX` must be a positive integer shown in the current role list.
The role must be `OPEN`. Every associated candidate must be `HIRED`, `REJECTED`, or `WITHDRAWN`,
and at most one may be `HIRED`. A role with no candidates, or no hired candidate, may also be closed.
Checks include candidates hidden by filters. Candidates loaded without a saved status receive
`PENDING_RESUME_SCREENING` and must receive a final outcome before closure.

Example: `list role`, followed by `role close 1`.

Success shows `Closed role: ROLE_TITLE.` and changes the displayed status to `CLOSED`.
The role and its candidates are saved automatically; candidate statuses remain unchanged.
New candidates cannot be added to the closed role. A role is never closed automatically.

If closure fails, no records change:

* Pending candidates: `Cannot close role: ROLE_TITLE. N candidate(s) still pending.`
* Multiple hired candidates: `Cannot close role: ROLE_TITLE. More than one candidate is marked HIRED.`
* Already closed: `ROLE_TITLE is already closed.`
* Invalid or missing index: `Role index must refer to a displayed role.`
* Extra arguments: `The role close command takes only a role index.`
* Candidate list displayed: `The role list is not displayed. Use list role first.`

### Deleting a role: `role delete`

Permanently removes a role entered by mistake. Run `list role` first.

Format: `role delete INDEX`

`INDEX` must be a positive integer shown in the current role list. An `OPEN` or `CLOSED` role
can be deleted only when it has no associated candidates. All candidates are checked, including
those hidden by filters and those marked `HIRED`, `REJECTED`, or `WITHDRAWN`.

Example: `list role`, followed by `role delete 1`.

Success shows `Deleted role: ROLE_TITLE.`. The role disappears, remaining displayed indices update,
and the change is saved automatically. Its title can be used again with `role add`.
Deletion is immediate and permanent. It never deletes, detaches, or reassigns candidates.

If deletion fails, no records change:

* Associated candidates: `Cannot delete role: ROLE_TITLE. It has N associated candidate(s).`
* Invalid or missing index: `Role index must refer to a displayed role.`
* Extra arguments: `The role delete command takes only a role index.`
* Candidate list displayed: `The role list is not displayed. Use list role first.`

Use `role close` to finish recruitment and retain its history; `role delete` removes an unused role.

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Listing all roles: `list role`

Shows every unique role in the address book. Each role is displayed in its own box with its title, required experience level, and status.

Format: `list role`

Role indices can be used only while the role list is displayed. If a successful command such as
adding or finding a candidate switches back to the candidate list, run `list role` again before
using `role close` or `role delete`. Failed commands leave the current list displayed.

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]... `

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Adding or clearing a note: `note`

Records a note for an existing person, such as interview details.

Format: `note INDEX [NOTE]`

* Sets the note of the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, ...
* Everything after the index is saved as the note, exactly as typed. The note can contain any text, including text that looks like a prefix, such as `t/` or `n/`.
* A new note replaces the person's existing note.
* To clear a person's note, enter `note INDEX` without a note after it.

Examples:
*  `note 1 Interview 2026-10-20 14:00` Sets the note of the 1st person to `Interview 2026-10-20 14:00`.
*  `note 2 Discuss t/java experience` Sets the note of the 2nd person to `Discuss t/java experience`.
*  `note 3` Clears the note of the 3rd person.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Persons matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Updating a candidate's status: `status`

Updates the recruitment status of the candidate at the specified index.

Format: `status INDEX STATUS`

* Updates the status of the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, ... and cannot exceed the length of the _displayed_ persons list.
* The person's original status is replaced with the one specified in `STATUS`.
* `STATUS` must be one of the following: `PENDING_RESUME_SCREENING`, `PENDING_RECRUITER_INTERVIEW`, `PENDING_OFFER_EXTENSION`, `PENDING_OFFER_DECISION`, `HIRED`, `REJECTED`, or `WITHDRAWN`.
* `STATUS` is not case-sensitive; for example, `hired`, `Hired` and `hiRED` are treated as the same as `HIRED`.
* Each role has one vacancy. Assigning `HIRED` fails if another candidate for the same role is already
  `HIRED`, including candidates hidden by filters. Role titles are compared without case sensitivity.
  The error is `ROLE_TITLE already has a hired candidate.` No candidate data changes.
* Reapplying `HIRED` to the same candidate is allowed when no other candidate for that role is hired.
  Marking a candidate `HIRED` does not close the role; use `role close` explicitly after all candidates
  have final outcomes.
* For an `OPEN` role, a candidate with a final outcome (`HIRED`, `REJECTED`, or `WITHDRAWN`) may return
  to any of the four `PENDING_*` statuses to correct or resume their recruitment process.
* For a `CLOSED` role, assigning any `PENDING_*` status is rejected with
  `Cannot set a pending status for NAME because role ROLE_TITLE is closed.` The candidate, role,
  current filter, and saved records remain unchanged. Corrections between final outcomes are still
  allowed, subject to the one-hired-candidate rule. Reopening a role is not supported in the MVP.

Example:

* `status 1 pending_offer_extension`
* `status 2 PENDING_RECRUITER_INTERVIEW`
* `status 1 HIRED`

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]... ` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]... `<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**List roles** | `list role`
**Note**   | `note INDEX [NOTE]`<br> e.g., `note 1 Interview 2026-10-20 14:00`
**Role add** | `role add r/ROLE_TITLE x/EXPERIENCE_LEVEL`<br> e.g., `role add r/Software Engineer x/2 years`
**Role close** | `role close INDEX`<br> e.g., `list role` followed by `role close 1`
**Role delete** | `role delete INDEX`<br> e.g., `list role` followed by `role delete 1`
**Status** | `status INDEX STATUS`<br> e.g., `status 1 accepted`
**Help**   | `help`
