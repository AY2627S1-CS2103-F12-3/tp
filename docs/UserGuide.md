---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# RecruiterBuddy User Guide

RecruiterBuddy is a **desktop application for department recruiters who fill a small number of vacancies within one department**. It helps you keep candidate details, track the role each candidate is applying for, record upcoming interviews, and see where each candidate stands in the recruitment process.

RecruiterBuddy is optimised for use through a Command Line Interface (CLI) while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, RecruiterBuddy can help you update recruitment records faster than traditional GUI applications.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103-F12-3/tp/releases).

1. Copy the file to an empty folder you want to use for RecruiterBuddy.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to read the offline command guide in the result area.<br>
   Some example commands you can try:

   * `list` : Lists all candidates.
   * `list role` : Lists all unique roles, with each role shown in its own box.
   * `undo` : Undoes the most recent data-changing command in this session.
   * `redo` : Redoes the most recently undone data-changing command.
   * `role add r/Software Engineer x/2 years` : Adds an open role.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 r/Data Analyst` : Adds a candidate named `John Doe` who is applying for the sample `Data Analyst` role.

   * `interview 1 d/2026-10-20 14:00` : Schedules an interview for the 1st candidate on 20 October 2026 at 2pm.

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

* A parameter may contain text such as `s/o` or `c/o`, as long as it is not one of the command's prefixes.<br>
  For example, in `n/Ravi s/o Kumar` the name is `Ravi s/o Kumar`, because `s/` is not a prefix of `add`.

* Extraneous parameters for `exit` and `clear` are ignored. `help` accepts at most one command name.
  `list` accepts only `list` or `list role`; anything else is rejected.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help: `help`

Shows every supported command's purpose, syntax and example in the result area. No internet
connection is needed. The result area expands for help, wraps long lines and can be scrolled
vertically. The candidate and role lists remain visible.

Format: `help [COMMAND]`

* `help` shows the complete guide; `help COMMAND` shows only that command's syntax, purpose and examples.
* Supported topics: `add`, `edit`, `delete`, `note`, `interview`, `status`, `find`, `list`, `role`,
  `clear`, `undo`, `redo`, `exit` and `help`.
* Help keywords are case-insensitive: `HELP ADD` and `help add` show the same instructions. This does
  not change the casing accepted when executing other commands.
* `help list` explains both `list` and `list role`. `help role` explains `role add`, `role edit`,
  `role close` and `role delete`. Subcommands are grouped under these single-word topics;
  `help list role` is not supported.
* An unknown topic shows: `No help is available for “COMMAND”. Use help to view all commands.`
* More than one argument shows: `Help accepts at most one command name.`

Examples: `help`, `help add`, `help role`.

The **Help** menu and **F1** show the same overview. Viewing help does not change candidates,
roles, either list's filter, the data file or undo/redo history. The next command replaces the
guide with its usual result message.

### Adding a candidate: `add`

Adds a candidate who is applying for a role.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS r/ROLE [t/TAG]... `

* `NAME`:
  * Is 1 to 100 characters long.
  * Starts with a letter, which can be from any language, such as `José` or `山田太郎`.
  * Can contain letters, digits, spaces and the symbols `'` `-` `.` `,` `/` `(` `)`.

  Capitalisation is kept as you type it.
* `PHONE_NUMBER`:
  * Has 8 to 15 digits, with an optional `+` at the start.
  * Can include spaces and hyphens, such as `+65 9123-4567`.

  Spaces and hyphens are ignored and removed when the number is saved, so `+65 9123-4567` is shown as `+6591234567`.

  A number without a country code is treated as a Singapore number. If it does not look like one (8 digits
  starting with 3, 6, 8 or 9), such as `12345678`, the candidate is still added, and the result includes a warning
  reminding you to include the country code for an overseas number, such as `+60 12-345 6789`.
* `EMAIL`:
  * Looks like `name@example.com`.
  * Before the `@`, can contain letters, digits and dots, plus:
    * common symbols `_` `-` `+` `'`, such as in `o'brien@example.com`, which are accepted as usual.
    * less common symbols `` ! # $ % & * / = ? ^ ` { | } ~ ``, such as in `john#doe@example.com`. These are also accepted, but the result includes a warning that the email may be a typo. If the symbol is intended, you can ignore the warning.
  * A dot cannot come first, last, or twice in a row before the `@`.
  * If the part after the `@` has no dot, such as in `john@gmailcom`, the email is also accepted, with a warning that it may be a typo.
  * Emails with spaces inside them or with quotation marks, such as `"john doe"@example.com`, and emails with non-English letters, such as `josé@example.com`, are not supported.
  * If the email is not valid, the error message names the problem, such as `Email cannot contain “,” before the @.`
* `ROLE` must be the title of an existing role that is `OPEN`. Use [`role add`](#adding-a-role-role-add) to create the role first.
* `ROLE` is matched ignoring case; for example, `r/software engineer` matches the role `Software Engineer`. The candidate is saved with the role's own spelling of the title.
* Each candidate needs their own email and phone number. A candidate cannot be added if another candidate already uses the same email or the same phone number, and the error message names that candidate.
  * Emails are compared ignoring case, so `Alex@Example.com` and `alex@example.com` are the same email.
  * Phone numbers are compared as saved, so `9123 4567` and `91234567` are the same number and cannot both be used.
  * A number that only differs from another candidate's by a `+` or the Singapore country code `65`, such as
    `+65 9123 4567`, `6591234567` and `91234567`, is still accepted, since it may be a different number.
    The result includes a warning naming every candidate whose number may be the same, so you can check.
  * Overseas numbers written with and without their country code, such as `+60 12-345 6789` and
    `012-345 6789`, are not recognised as possibly the same number.
* Candidates may share a name, as long as their emails and phone numbers are different.
* Each candidate applies for one role. Since their email and phone number stay the same, the same person cannot be added again for a second role.
* Newly added candidates are automatically assigned the status `RESUME`, displayed as `Resume`.
* The candidate's card shows the role as `Applying for: ROLE`.

<box type="tip" seamless>

**Tip:** A candidate can have any number of tags, including zero.
A tag typed more than once with the same capitalisation is saved only once, so `t/java t/java` gives the candidate a single `java` tag.
</box>

<box type="tip" seamless>

**Tip:** You can type `s/o` and `d/o` in names as usual. For `a/l` and `a/p`, type `A/L` and `A/P` instead, since a lowercase `a/` starts the address.
</box>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 r/Software Engineer`
* `add n/Betsy Crowe t/referral e/betsycrowe@example.com a/Newgate Prison p/12345678 r/product manager t/java`
* `add n/Ravi s/o Kumar p/+65 9123-4567 e/ravi@example.com a/Bedok North r/Software Engineer` Adds `Ravi s/o Kumar` with the phone number `+6591234567`.

### Adding a role: `role add`

Records a vacancy with its title and required experience level. Each role represents exactly one
vacancy; there is no opening-count field.

Format: `role add r/ROLE_TITLE x/EXPERIENCE_LEVEL`

Both `r/` and `x/` are required, must appear exactly once, and may appear in either order.
Leading and trailing whitespace is removed from each value before validation and storage.

* `ROLE_TITLE`: 1–100 characters after trimming, with at least one letter. It may contain letters,
  digits, spaces, apostrophes (`'`), ampersands (`&`), hyphens (`-`), slashes (`/`), full stops (`.`),
  plus signs (`+`), hashes (`#`), parentheses (`(` and `)`), and commas (`,`).
* `EXPERIENCE_LEVEL`: 1–50 printable characters after trimming, with no embedded newlines, tabs,
  or other control characters. Examples include `Entry level`, `2 years`, and `Senior`.
* Role titles must be unique across both `OPEN` and `CLOSED` roles, ignoring case and surrounding
  whitespace. Changing the experience level does not make a title distinct. Internal spaces are
  significant: a title with one space between words differs from one with two consecutive spaces.

Examples:

* `role add r/Software Engineer x/2 years`
* `role add x/Entry level r/Product Analyst`
* `role add r/C++ Developer x/2 years`
* `role add r/C# Developer x/2 years`
* `role add r/Engineer (Backend) x/2 years`
* `role add r/Manager, Sales x/5 years`

On success, RecruiterBuddy shows `Added open role: ROLE_TITLE.` and saves the role with status `OPEN`.
Run `list role` to display it; `role add` itself returns to the candidate list.

If validation fails, no role is created:

* Missing title prefix: `Missing required field: r/.`
* Missing experience prefix: `Missing required field: x/.`
* Repeated prefix: `Multiple values specified for the following single-valued field(s): PREFIX`.
  For example, repeating `x/` reports that prefix even if both values are identical.
* Empty or invalid title:

  ```text
  Role title must be 1–100 characters and contain a letter.
  Use only letters, digits, spaces, or these symbols: ' & - / . + # ( ) ,
  ```

* Empty or invalid experience level:
  `Required experience level must be 1–50 characters and must not contain a new line.`
* Duplicate title: `A role named “ROLE_TITLE” already exists.`
* Text before the first prefix: `Invalid command format!`, followed by the command's usage and example.

Closing a role does not release its title. An unused role can instead be removed with `role delete`
before creating another role with that title.

### Editing a role: `role edit`

Corrects a role's title or required experience level without removing the role or losing its recruitment history.

Format: `role edit INDEX [r/ROLE_TITLE] [x/EXPERIENCE_LEVEL]`

* Run `list role` first. `INDEX` must be a positive integer shown in the current role list.
* Supply at least one field, with each supplied prefix appearing only once. Either field order is accepted.
* Values use the same title and experience constraints as [`role add`](#adding-a-role-role-add). Surrounding whitespace is removed. Titles remain unique across OPEN and CLOSED roles, ignoring case but not internal spaces.
* Both OPEN and CLOSED roles may be corrected. Editing a closed role does not reopen it or transfer candidates to another opening.
* Renaming updates every associated candidate, including candidates hidden by a filter. Candidate statuses, contact details, notes and tags are preserved. Omitted role fields remain unchanged.
* Capitalization-only corrections update the role and all its candidates to the supplied spelling. Supplying unchanged values succeeds without changing the stored data.
* After a rename, the old title is available for a new role. Existing candidates remain associated with the renamed role; the old title is not an alias.

Examples, after running `list role`:

* `role edit 1 x/5 years`
* `role edit 1 r/Platform Engineer`
* `role edit 1 r/Platform Engineer x/Senior`

Success shows `Edited role: ROLE_TITLE.`, using the updated title. The role card refreshes, the role list stays displayed, and the complete change is saved automatically. Role and candidate filter predicates are preserved and reevaluated against the updated records.

Validation failures leave all records, filters, and the saved data unchanged:

* Invalid or missing index: `Role index must refer to a displayed role.`
* Candidate list displayed: `The role list is not displayed. Use list role first.`
* No fields supplied: `At least one field to edit must be provided.`
* Repeated prefix: `Multiple values specified for the following single-valued field(s): PREFIX`. If both prefixes repeat, the message names both.
* Empty or invalid title: the same two-line role-title constraint message shown under
  [`role add`](#adding-a-role-role-add), including the allowed characters.
* Empty or invalid experience: `Required experience level must be 1–50 characters and must not contain a new line.`
* Another role has the requested title: `A role named “ROLE_TITLE” already exists.` This also applies if the other role is closed.
* Extra text before the first prefix: `Invalid command format!`, followed by usage and an example.

### Closing a role: `role close`

Ends recruitment while retaining the role and all its candidate records.

Format: `role close INDEX`

Run `list role` first. `INDEX` must be a positive integer shown in the current role list.
The role must be `OPEN`. Every associated candidate must be `HIRED`, `REJECTED`, or `WITHDRAWN`,
and at most one may be `HIRED`. A role with no candidates, or no hired candidate, may also be closed.
Checks include candidates hidden by filters. Candidates loaded without a saved status receive
`RESUME` and must receive a final outcome before closure.

Example: `list role`, followed by `role close 1`.

Success shows `Closed role: ROLE_TITLE.` and changes the displayed status to `CLOSED`.
The role and its candidates are saved automatically; candidate statuses remain unchanged.
New candidates cannot be added to the closed role. A role is never closed automatically.
To reverse a closure in the current session, use [`undo`](#undoing-a-change-undo).
There is no dedicated command to reopen an arbitrary closed role.

If closure fails, no records change:

* Pending candidates: `Cannot close role: ROLE_TITLE. N candidate(s) still pending.`
* Multiple hired candidates: `Cannot close role: ROLE_TITLE. More than one candidate is marked HIRED.`
* Already closed: `ROLE_TITLE is already closed.`
* Invalid or missing index: `Role index must refer to a displayed role.`
* Extra arguments: `The role close command takes only a role index.`
* Candidate list displayed: `The role list is not displayed. Use list role first.`

### Deleting a role: `role delete`

Removes a role entered by mistake. Run `list role` first.

Format: `role delete INDEX`

`INDEX` must be a positive integer shown in the current role list. An `OPEN` or `CLOSED` role
can be deleted only when it has no associated candidates. All candidates are checked, including
those hidden by filters and those marked `HIRED`, `REJECTED`, or `WITHDRAWN`.

Example: `list role`, followed by `role delete 1`.

Success shows `Deleted role: ROLE_TITLE.`. The role disappears, remaining displayed indices update,
and the change is saved automatically. Its title can be used again with `role add`.
Deletion is immediate, but [`undo`](#undoing-a-change-undo) can restore the role in the current
session. It never deletes, detaches, or reassigns candidates.

If deletion fails, no records change:

* Associated candidates: `Cannot delete role: ROLE_TITLE. It has N associated candidate(s).`
* Invalid or missing index: `Role index must refer to a displayed role.`
* Extra arguments: `The role delete command takes only a role index.`
* Candidate list displayed: `The role list is not displayed. Use list role first.`

Use `role close` to finish recruitment and retain its history; `role delete` removes an unused role.

### Listing all candidates: `list`

Shows a list of all candidates.

Format: `list`

### Listing all roles: `list role`

Shows every unique role in RecruiterBuddy. Each role is displayed in its own box with its title, required experience level, and status.

Format: `list role`

### Undoing a change: `undo`

Undoes the most recent successful command that changed the address book. Undo can be used repeatedly to return to the address-book state at the beginning of the current session.

Format: `undo`

Examples:

* `add ...` followed by `undo` removes the newly added person.
* `edit 1 p/91234567` followed by `undo` restores the person's previous phone number.
* `delete 1` followed by `undo` restores the deleted person.
* `list role`, `role close 1`, then `undo` restores the role's `OPEN` status without changing its candidates.
* `list role`, `role delete 1`, then `undo` restores the role, including its title, experience level,
  status, and position in the role list.

Undo reverses changes in order, not a selected role or candidate. If other changes were made after
a closure or deletion, undo those changes first. After undoing a role change, run `list role` to
view the restored roles; this does not affect undo history.

The `list` (including `list role`), `find`, and `help` commands do not affect undo history.
An undo also updates the saved role and candidate data automatically. Undo and redo history exist
only in the current session: exiting and restarting preserves saved records, but clears both
histories. A deletion or closure from an earlier session cannot be reversed with `undo`.

### Redoing a change: `redo`

Redoes the most recently undone change.

Format: `redo`

Redo is available only after an `undo`. Executing a new address-book change after an undo clears the redo history. Like all address-book changes, a redo is saved automatically.
For example, after undoing `role delete 1` or `role close 1`, `redo` deletes or closes the role again.
Run `list role` to view the result.

### Editing a person: `edit`
Each role card also shows `Pending`, `Hired`, `Rejected`, and `Withdrawn` candidate totals, including
zeros. For example: `Pending: 3`, `Hired: 1`, `Rejected: 2`, `Withdrawn: 0`.

* Totals include every candidate associated with that role, even when a candidate-name filter hides
  some records. Filtering candidates does not change the totals.
* `Pending` combines all recruitment stages before a final outcome. `Hired`, `Rejected`, and
  `Withdrawn` count their respective final outcomes; each candidate contributes to exactly one total.
* Both `OPEN` and `CLOSED` roles show totals. An empty role shows zero for all four categories.
* Totals refresh from the current records after additions, removals, and status changes, and are
  reconstructed when data is loaded. They do not change candidate statuses or close roles automatically.

Role indices can be used only while the role list is displayed. If a successful command such as
adding or finding a candidate switches back to the candidate list, run `list role` again before
using `role edit`, `role close`, or `role delete`. Failed commands leave the current list displayed.

### Editing a candidate: `edit`

Edits an existing candidate.

Format: `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [r/ROLE_TITLE] [t/TAG]...`

* Edits the candidate at the specified `INDEX`. The index refers to the index number shown in the displayed candidate list. The index **must be a positive integer** 1, 2, 3, ...
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* `NAME`, `PHONE_NUMBER` and `EMAIL` follow the same rules as in [`add`](#adding-a-candidate-add), including typing `a/l` and `a/p` in names as `A/L` and `A/P`.
  The email warnings appear only when you change the email to a different address. Changing only its capitals does not count.
* When editing tags, all of the candidate's existing tags are removed; adding tags is not cumulative.
* As in [`add`](#adding-a-candidate-add), a tag typed more than once with the same capitalisation is saved only once.
* To remove all of a candidate's tags, enter `t/` without a tag after it.
* Use `r/ROLE_TITLE` to correct the candidate's role, on its own or with other edits. The title must
  match an existing role, ignoring case and surrounding spaces; the saved title uses that role's spelling.
* A transfer can leave an open or closed role, but the destination must be **OPEN**. Selecting the
  existing role is allowed without transferring, even when it is closed.
* The candidate's status, note, and interview are preserved. Use `status`, `note`, or `interview` to change them
  separately.
  A `HIRED` candidate cannot transfer to a role that already has another hired candidate, including
  one hidden by the current candidate filter. Non-hired candidates can transfer to an open role
  even when it already has a hire. A transfer never opens or closes either role automatically.
* Each field except `t/` may appear only once. Empty or invalid role titles are rejected using the
  same [role-title rules](#adding-a-role-role-add) as role creation.
* The new email and phone number must not be used by another candidate, following the same rules as in
  [`add`](#adding-a-candidate-add). Keeping a candidate's own email or phone number is fine.
* When `p/` changes the phone number, the result includes the same phone number notes as `add`, if any apply.
* Success shows `Edited candidate: ...` with the updated name and details, refreshes the candidate
  list to show all candidates, and saves the change. Validation failures leave records and filters unchanged.

Role-related errors:

* Unknown role: `No role named “ROLE_TITLE” exists. Add the role first, then edit the candidate.`
* Closed destination: `The role “ROLE_TITLE” is closed, so candidates cannot be moved to it.`
* Destination already has a hire: `ROLE_TITLE already has a hired candidate.`

Examples:

*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st candidate to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd candidate to be `Betsy Crower` and clears all existing tags.
*  `edit 1 r/Data Analyst` Moves the 1st candidate to the existing open Data Analyst role, preserving
   their status, note and other details.
*  `edit 1 n/Alex Tan r/Data Analyst` Corrects the name and role together; the success message names Alex Tan.

### Adding or clearing a note: `note`

Records a note for an existing candidate, such as their skills or availability.

Format: `note INDEX [NOTE]`

* Sets the note of the candidate at the specified `INDEX`. The index refers to the index number shown in the displayed candidate list. The index **must be a positive integer** 1, 2, 3, ...
* Everything after the index is saved as the note, with leading and trailing spaces removed. The note can contain any text, including text that looks like a prefix, such as `t/` or `n/`.
* A new note replaces the candidate's existing note.
* To clear a candidate's note, enter `note INDEX` without a note after it.
* To record an interview date and time, use [`interview`](#scheduling-or-clearing-an-interview-interview) instead, which checks that the date and time are valid.

Examples:
*  `note 1 Prefers hybrid work` Sets the note of the 1st candidate to `Prefers hybrid work`.
*  `note 2 Discuss t/java experience` Sets the note of the 2nd candidate to `Discuss t/java experience`.
*  `note 3` Clears the note of the 3rd candidate.

### Scheduling or clearing an interview: `interview`

Schedules an interview for an existing candidate, or clears it.

Format: `interview INDEX [d/YYYY-MM-DD HH:MM]`

* Sets the interview of the candidate at the specified `INDEX`. The index refers to the index number shown in the displayed candidate list. The index **must be a positive integer** 1, 2, 3, ...
* The date and time must be in the format `YYYY-MM-DD HH:MM`, with leading zeros and a 24-hour time, e.g. `2026-10-20 09:00`, not `2026-10-20 9:00`.
* The date and time must be a real date and a valid 24-hour time, so values such as `2026-02-30` or `24:00` are rejected. 29 February is accepted only in leap years.
* Past dates are accepted, so you can record interviews that have already happened.
* A new interview replaces the candidate's existing interview. Each candidate has at most one interview.
* To clear a candidate's interview, enter `interview INDEX` without `d/`. An empty `d/` is rejected, so a mistyped date never clears an interview by accident.
* Clearing a candidate who has no interview, or entering the same date and time again, still succeeds. The candidate's details stay the same.
* The interview is shown on the candidate's card. The candidate's note and other details are not changed, and the displayed list stays as it is, so a filtered list remains filtered.
* If the index is missing, is not a positive whole number, is past the end of the displayed list, or is followed by extra text before `d/`, the error message says which of these happened, so you can tell what to correct.
* An interview that might be a mistake is still saved, but the result message adds a line about it, in this order:
  * The interview is today or later, but the candidate's status is already `HIRED`, `REJECTED` or `WITHDRAWN`, for example `Warning: candidate is Rejected.` If they are back in the process, update their status with [`status`](#updating-a-candidate-s-status-status).
  * The date is before today, which may mean the date was mistyped. Historical interviews are still saved.
  * The date is more than a year from today, which may mean the year was mistyped. Today's date is taken from your computer.
  * Other candidates have an interview at the same date and time. The line `Also at this time:` names up to two of them, including candidates hidden by the current filter. Longer names are shortened, and any remaining candidates are shown as a count, for example `(+3 others)`.
* Sorting and filtering candidates by interview date are not supported yet, and are planned for a future version.

Examples:
*  `interview 1 d/2026-10-20 14:00` Schedules an interview for the 1st candidate on 20 October 2026 at 2pm.
*  `interview 1 d/2026-10-22 09:30` Reschedules the 1st candidate's interview to 22 October 2026 at 9:30am.
*  `interview 2` Clears the interview of the 2nd candidate.

### Locating candidates by name: `find`

Finds candidates whose names contain any of the given keywords.

Format: `find [KEYWORD ...] [r/ ROLE ...] [st/ STATUS ...]`

`r/` filters by role title and `st/` filters by role status (`OPEN` or `CLOSED`).
For example, `find r/Software Engineer st/OPEN` shows candidates in open Software
Engineer roles. The displayed feedback identifies the filter used.

When a candidate is updated and no longer matches the current filter, they disappear
from the list and the displayed numbers shift. For updating many candidates, use the
batch `status` command instead of relying on numbers one at a time.

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

Format: `status INDEX... st/STATUS`

* Updates the status of the candidates at the specified indices. Each index refers to the index number shown in the displayed candidate list. The index **must be a positive integer** 1, 2, 3, ... and cannot exceed the length of the _displayed_ candidate list.
* Each candidate's original status is replaced with the one specified in `STATUS`.
* `STATUS` must be one of the following: `RESUME`, `INTERVIEW`, `OFFER`, `DECISION`, `HIRED`, `REJECTED`, or `WITHDRAWN`.
* `STATUS` is not case-sensitive; for example, `hired`, `Hired` and `hiRED` are treated as the same as `HIRED`.
* Each role has exactly one vacancy. If another candidate for the same role is already `HIRED`, assigning the `HIRED` status fails with error message `ROLE_TITLE already has a hired candidate`. Candidates hidden by filters are considered for this, and role titles are compared without case sensitivity.
  * If two candidates for the same role are assigned `HIRED` in a single command, the command fails with error message `Cannot hire two candidates for the ROLE_TITLE role`. Role titles are compared without case sensitivity.
  * In both of the above failure cases, no candidate data changes, even for other candidates specified in the same command whose roles have a vacancy.
* Reapplying `HIRED` to the same candidate is allowed when no other candidate for that role is hired. Marking a candidate `HIRED` does not close the role; use `role close` explicitly after all candidates have final outcomes.
* For an `OPEN` role, a candidate with a final outcome (`HIRED`, `REJECTED`, or `WITHDRAWN`) may return to any of the four pending statuses (`RESUME`, `INTERVIEW`, `OFFER`, or `DECISION`) to correct or resume their recruitment process.
* For a `CLOSED` role, assigning any of the pending statuses is rejected with `Cannot set a pending status for NAME because role ROLE_TITLE is closed.` The candidate, role, current filter, and saved records remain unchanged. Corrections between final outcomes are still allowed, subject to the one-hired-candidate rule. There is no dedicated reopen command, but [`undo`](#undoing-a-change-undo) can reverse a closure in the current session after any later changes have been undone.

Example:

* `status 1 st/offer`
* `status 2 4 6 st/INTERVIEW`

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

RecruiterBuddy automatically stores `data/addressbook.json` (containing candidate and role data),
`preferences.json`, and log files in the folder containing the JAR file, regardless of the terminal's
working directory. Candidate and role data is saved automatically after every successful command
except `help`, including commands such as `list` that do not change records. You do not need to save
manually. Viewing help, including through the Help menu or F1, does not save recruitment data.
Commands rejected during parsing or validation do not trigger a save.

Upon startup, RecruiterBuddy automatically loads saved data from the folder where the JAR file is located. Saved roles retain their titles, required experience levels, and `OPEN` or `CLOSED` statuses after
restarting. Candidates associated with closed roles are retained too. If no data file exists,
RecruiterBuddy starts with sample roles and candidates, not an empty data set.

If saving fails, an error reports the file-access problem instead of the normal success message.
Changes already made may remain in the current session without being saved. Resolve the file-access
problem and run `list` successfully to retry saving before exiting. Viewing help does not retry saving.

**Caution:** due to changes in Status names, save files from v1.3 and earlier are incompatible with later versions and will fail to load. Please backup obsolete save files and manually change any invalid Status names to valid ones (see below for information on editing the save file).

### Editing the data file

RecruiterBuddy saves data in `data/addressbook.json`, relative to the folder containing the JAR file, regardless of the terminal's working directory.
If no data file exists on startup, the first successful command other than `help` creates it.
Viewing help does not create the recruitment data file.
Advanced users may edit this JSON file while the application is closed. Back it up first.

The file contains a `persons` array for candidates and a `roles` array for role openings.
For example, this complete file contains one role and no candidates:

```json
{
  "persons": [],
  "roles": [
    {
      "title": "Software Engineer",
      "experienceLevel": "2 years",
      "status": "OPEN"
    }
  ]
}
```

When editing candidates, roles and their associations:

* Candidate names, phone numbers and emails must follow the same rules as in [`add`](#adding-a-candidate-add).
  Files from older versions may contain phone numbers with fewer than 8 digits, which need updating before the file can load.
* Every role needs `title`, `experienceLevel`, and `status`. Title and experience values must satisfy
  the same constraints as `role add`; role status must be exactly `OPEN` or `CLOSED` in uppercase.
* Do not repeat role titles, even with different capitalisation or experience levels.
* No two candidates may share an email (ignoring case) or a phone number, following the same rules as in
  [`add`](#adding-a-candidate-add). Numbers that `add` accepts with a warning, such as `+6591234567` and
  `91234567`, load normally.
* Each candidate's `role` field must name an existing role, ignoring case. It may name a closed role,
  because closing preserves candidates. On loading, the role's stored capitalisation is used for
  its candidates. If you rename a role in the file, update every associated candidate's `role` too.
* A candidate's saved `interview` uses the format `YYYY-MM-DD HH:MM`, such as `2026-10-20 14:00`. An
  empty or missing `interview` means the candidate has no interview.
* A candidate's saved `status` uses an uppercase status name, such as `HIRED`. If absent, it defaults
  to `RESUME`. A missing candidate `role` has no default and prevents loading;
  older contact-only files therefore need role information added before they can be used.
* Keep recruitment outcomes consistent: at most one candidate per role should be `HIRED`, and closed
  roles should have no pending candidates. The status and close commands enforce these rules during
  normal use; do not rely on the loader to detect every inconsistent outcome introduced by hand.

<box type="warning" seamless>

**Caution:**
The data file must contain exactly one JSON object. Keep the `persons` and `roles` lists inside
that object instead of appending another object. Whitespace before or after it is allowed.
Additional JSON values or other trailing text,
malformed JSON, invalid role fields, duplicate role titles, candidates sharing an email or phone
number, or a candidate referring to a nonexistent role prevent the whole file from loading; valid
records are not loaded separately. If two objects are accidentally joined together, neither is
imported and the entire file is preserved in the backup.
If a regular data file cannot be read or validated, RecruiterBuddy first renames it to
`addressbook.json.bak` in the same folder, preserving its contents. Existing backups are never
overwritten: if that name is taken, a timestamp is appended, for example
`addressbook.json.bak.20261018-160000-123`. A numbered suffix resolves further collisions.

After preserving the original, RecruiterBuddy saves a new file containing empty candidate and role
lists. A **Recruitment data backed up** warning shows the backup's full path and restoration
instructions. Dismiss it to continue using the app with empty lists. Restarting without entering
any commands also keeps those lists empty; it does not load sample data.

To restore your records, close RecruiterBuddy and save a separate copy of any new data you entered.
Correct the backup's JSON or file permissions, then copy the corrected file to the original
`data/addressbook.json` path and restart. Backups are not repaired, imported or merged automatically.
Keep your backup until you have checked that restoration succeeded.

If the backup or replacement cannot be created, an **Unable to load recruitment data** error
identifies the data path, failure reason and backup path if one was created. The application then
closes without opening commands. If backup failed, the original remains at its original path; if
replacement failed after backup, the original remains in the named backup. Resolve the access problem
or restore corrected data before restarting. Directories and symbolic links are not renamed as
data-file backups.

A genuinely missing file still starts the application with sample data; a valid file containing
empty lists stays empty. There is no automatic migration of old contact-only files.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with `data/addressbook.json` from the folder containing your previous RecruiterBuddy JAR file.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS r/ROLE [t/TAG]... ` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 r/Software Engineer t/referral t/java`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [r/ROLE_TITLE] [t/TAG]...`<br> e.g.,`edit 2 n/James Lee r/Data Analyst`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**List roles** | `list role`
**Undo**   | `undo`
**Redo**   | `redo`
**Interview** | `interview INDEX [d/YYYY-MM-DD HH:MM]`<br> e.g., `interview 1 d/2026-10-20 14:00`, `interview 2`
**Note**   | `note INDEX [NOTE]`<br> e.g., `note 1 Prefers hybrid work`
**Role add** | `role add r/ROLE_TITLE x/EXPERIENCE_LEVEL`<br> e.g., `role add r/Software Engineer x/2 years`
**Role edit** | `role edit INDEX [r/ROLE_TITLE] [x/EXPERIENCE_LEVEL]`<br> e.g., `list role` followed by `role edit 1 r/Platform Engineer x/5 years`
**Role close** | `role close INDEX`<br> e.g., `list role` followed by `role close 1`
**Role delete** | `role delete INDEX`<br> e.g., `list role` followed by `role delete 1`
**Status** | `status INDEX... st/STATUS`<br> e.g., `status 1 2 3 st/HIRED`
**Help**   | `help [COMMAND]`<br> e.g., `help role`
