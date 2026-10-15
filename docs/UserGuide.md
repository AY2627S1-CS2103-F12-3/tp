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

   * `list` : Lists all candidates.
   * `list role` : Lists all unique roles, with each role shown in its own box.
   * `role add r/Software Engineer x/2 years` : Adds an open role.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 r/Data Analyst` : Adds a candidate named `John Doe` who is applying for the sample `Data Analyst` role.

   * `note 1 Interview 2026-10-20 14:00` : Records an interview date and time for the 1st candidate.

   * `delete 3` : Deletes the 3rd candidate shown in the current list.

   * `clear` : Deletes all candidates and roles.

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


### Adding a candidate: `add`

Adds a candidate who is applying for a role.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS r/ROLE [t/TAG]... `

* `ROLE` must be the title of an existing role that is `OPEN`. Use [`role add`](#adding-a-role-role-add) to create the role first.
* `ROLE` is matched ignoring case; for example, `r/software engineer` matches the role `Software Engineer`. The candidate is saved with the role's own spelling of the title.
* Newly added candidates are automatically assigned the status `Pending Resume Screening`.
* The candidate's card shows the role as `Applying for: ROLE`.

<box type="tip" seamless>

**Tip:** A candidate can have any number of tags, including zero.
</box>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 r/Software Engineer`
* `add n/Betsy Crowe t/referral e/betsycrowe@example.com a/Newgate Prison p/1234567 r/product manager t/java`

### Adding a role: `role add`

Adds an open role to the address book. Role titles are unique, ignoring case.

Format: `role add r/ROLE_TITLE x/EXPERIENCE_LEVEL`

Examples:

* `role add r/Software Engineer x/2 years`
* `role add r/Product Manager x/5 years`
* `role add r/UX Designer x/3 years`

New roles are created with status `OPEN`. To view all roles, use `list role`.

### Listing all candidates: `list`

Shows a list of all candidates.

Format: `list`

### Listing all roles: `list role`

Shows every unique role in the address book. Each role is displayed in its own box with its title, required experience level, and status.

Format: `list role`

### Editing a candidate: `edit`

Edits an existing candidate.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]... `

* Edits the candidate at the specified `INDEX`. The index refers to the index number shown in the displayed candidate list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the candidate's existing tags are removed; adding tags is not cumulative.
* To remove all of a candidate's tags, enter `t/` without a tag after it.
* A candidate's role cannot be edited, so `r/` is rejected. The candidate's status and note are also kept; use `status` or `note` to change them.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st candidate to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd candidate to be `Betsy Crower` and clears all existing tags.

### Adding or clearing a note: `note`

Records a note for an existing candidate, such as an interview date and time.

Format: `note INDEX [NOTE]`

* Sets the note of the candidate at the specified `INDEX`. The index refers to the index number shown in the displayed candidate list. The index **must be a positive integer** 1, 2, 3, ...
* Everything after the index is saved as the note, exactly as typed. The note can contain any text, including text that looks like a prefix, such as `t/` or `n/`.
* A new note replaces the candidate's existing note.
* To clear a candidate's note, enter `note INDEX` without a note after it.

Examples:
*  `note 1 Interview 2026-10-20 14:00` Sets the note of the 1st candidate to `Interview 2026-10-20 14:00`.
*  `note 2 Discuss t/java experience` Sets the note of the 2nd candidate to `Discuss t/java experience`.
*  `note 3` Clears the note of the 3rd candidate.

### Locating candidates by name: `find`

Finds candidates whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Candidates matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

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

Example:

* `status 1 pending_offer_extension`
* `status 2 PENDING_RECRUITER_INTERVIEW`

### Deleting a candidate: `delete`

Deletes the specified candidate.

Format: `delete INDEX`

* Deletes the candidate at the specified `INDEX`.
* The index refers to the index number shown in the displayed candidate list.
* The index **must be a positive integer** 1, 2, 3, ...

Examples:
* `list` followed by `delete 2` deletes the 2nd candidate in RecruiterBuddy.
* `find Betsy` followed by `delete 1` deletes the 1st candidate in the results of the `find` command.

### Clearing all entries: `clear`

Clears all candidates and roles from RecruiterBuddy.

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
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS r/ROLE [t/TAG]... ` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 r/Software Engineer t/referral t/java`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]... `<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**List roles** | `list role`
**Note**   | `note INDEX [NOTE]`<br> e.g., `note 1 Interview 2026-10-20 14:00`
**Role add** | `role add r/ROLE_TITLE x/EXPERIENCE_LEVEL`<br> e.g., `role add r/Software Engineer x/2 years`
**Status** | `status INDEX STATUS`<br> e.g., `status 1 accepted`
**Help**   | `help`
