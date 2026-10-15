---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# RecruiterBuddy Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project builds on [AddressBook-Level3](https://github.com/se-edu/addressbook-level3) by the
  [SE-EDU initiative](https://se-education.org). Its existing architecture and documentation form the
  starting point for RecruiterBuddy.
* Kavin Parthipan (`kpkavin04`) used OpenAI Codex to assist with requirements analysis, implementation,
  tests, review, and documentation for the persistent role model, role commands, and associated
  candidate-status and displayed-list safeguards. This acknowledgement covers that contribution,
  not other team members' tool usage.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103-F12-3/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="600" alt="Partial model structure showing candidates and roles" />

The partial diagram focuses on role associations. Filtered-view associations and unrelated candidate
fields are omitted for clarity.

The `Model` component,

* stores candidates (`Person`) in `UniquePersonList` and roles (`Role`) in `UniqueRoleList`, both owned
  by `AddressBook`. `ReadOnlyAddressBook` exposes unmodifiable observable views of both collections.
* maintains filtered candidate and role lists in `ModelManager`, exposed through
  `getFilteredPersonList()` and `getFilteredRoleList()`. The UI observes these lists so additions,
  replacements, and removals update the relevant list panel.
* represents each role as an immutable title, required experience level, and `RoleStatus` (`OPEN` or
  `CLOSED`). One role is one vacancy; no opening count is stored.
* stores each candidate's association as a `RoleTitle`, not a separate copy of a `Role`. Role lookups
  match titles without case sensitivity.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103-F12-3/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* saves candidates and roles together in the address book JSON file, and user preferences in a
  separate JSON file. It reads them back into corresponding model objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* uses `JsonSerializableAddressBook` with `JsonAdaptedPerson`, `JsonAdaptedTag`, and `JsonAdaptedRole`
  to convert between JSON-compatible values and model objects. A role's saved fields are `title`,
  `experienceLevel`, and `status`; the status is the exact enum name.
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Role creation and persistence

#### Role identity and validation

`RoleTitle` strips surrounding whitespace, validates 1–100 Unicode code points and the allowed
characters, and requires at least one letter. `ExperienceLevel` similarly strips its value and
validates 1–50 printable code points, excluding control, format, surrogate, unassigned, line-separator,
and paragraph-separator characters. Their constraint messages use the same length constants as their
validation, avoiding independently hard-coded limits.

`Role#isSameRole` compares only titles through `RoleTitle#isSameTitle`, ignoring case. `UniqueRoleList`
uses that identity to reject duplicates, irrespective of experience level or open/closed status.
Full `Role#equals` also compares experience level and status; it is not the duplicate rule. Internal
spaces are not collapsed. The immutable `Role` constructor without a status creates an `OPEN` role.

#### Adding an opening

`AddressBookParser` routes `role` to `RoleCommandParser`, which delegates `add` to
`AddRoleCommandParser`. The parser tokenises `r/` and `x/`, checks missing fields, reuses
`ArgumentMultimap#verifyNoDuplicatePrefixesFor`, rejects a non-empty preamble, and validates both
values through `ParserUtil`. Missing-prefix errors therefore take precedence over repeated-prefix
or invalid-value errors when several problems occur together.

`AddRoleCommand` checks `Model#hasRole` before calling `Model#addRole`. The model updates the underlying
observable collection and resets the role filter to show all roles. The command returns
`Added open role: TITLE.` with the default candidate-list view; `list role` displays the updated roles.
Parsing or duplicate rejection leaves the stored records unchanged.

#### Saving and restoring associations

After a command executes successfully, `LogicManager#execute` asks `Storage` to save the complete
address book, including both collections. This also happens for successful read-only commands.
Parsing and command-validation failures do not reach the save call. Save errors become
`CommandException`s; earlier in-memory changes are not rolled back. This is not a transactional save
or an automatic backup mechanism.

`JsonSerializableAddressBook#toModelType` reconstructs roles before candidates. Each `JsonAdaptedRole`
validates its required fields and exact `OPEN`/`CLOSED` enum name. Duplicate titles are rejected using
the same identity rule as the model. Each candidate must then reference an existing role; the loader
replaces the candidate's role title with that role's canonical stored title. Closed roles are valid
references because closure retains candidates.

An absent `roles` array is treated as empty, but loading still fails if a candidate lacks a role or
references a missing one. A missing candidate status defaults to `PENDING_RESUME_SCREENING`, whereas
a missing role status is invalid. There is no automatic migration from old contact-only records.
Loading validates fields, duplicate records, and references; it does not enforce all command-level
recruitment rules, such as the maximum number of hired candidates in hand-edited data.

Invalid data is reported as `DataLoadingException`. `MainApp` starts with an empty `AddressBook`
after a load failure, without partially importing valid records or immediately overwriting the file.
If the file is absent instead, it uses `SampleDataUtil#getSampleAddressBook`. The next successfully
executed command saves the current state, so users must recover invalid files before issuing commands.

### Role lifecycle and candidate safeguards

#### Closing recruitment

`RoleCommandParser` delegates `close` to `CloseRoleCommandParser`. Close and delete share
`ParserUtil#parseRoleIndex` to reject missing, invalid, or extra index arguments consistently.
`CloseRoleCommand` first checks `Model#isRoleListDisplayed`, then resolves the index against
`Model#getFilteredRoleList` and rejects an already closed role.

Candidate checks use the complete `ReadOnlyAddressBook#getPersonList`, not the filtered candidate
list, matching role titles without case sensitivity. `Status#isPending` classifies every status
except `HIRED`, `REJECTED`, and `WITHDRAWN` as pending. Closure rejects pending candidates with their
count, then rejects more than one hired candidate. No candidates, or no hired candidate, is allowed.
After validation, `Model#setRole` replaces the immutable role with a `CLOSED` copy. Candidate records
and statuses remain unchanged. The observable role list refreshes, the command result keeps it
displayed, and `LogicManager` saves the change. `AddCommand` rejects new candidates for closed roles.

#### Deleting an unused role

`RoleCommandParser` delegates `delete` to `DeleteRoleCommandParser`. `DeleteRoleCommand` applies the
same displayed-list and index guards, then counts associated candidates across all stored records.
Any associated candidate blocks deletion, irrespective of status or visibility; the error includes
their count. An empty role may be deleted whether `OPEN` or `CLOSED`.

Deletion goes through `Model#deleteRole`, `AddressBook#removeRole`, and `UniqueRoleList#remove`.
The observable list refreshes and its indices update; the result keeps the role list displayed.
`LogicManager` saves the removal. Deletion releases the title for reuse but never deletes, detaches,
or reassigns candidates. It is immediate and has no undo operation in the MVP.

#### Status changes and one hire per role

Before assigning `HIRED`, `StatusCommand` checks all stored candidates for another hire with a
case-insensitively matching role title, excluding the selected candidate by candidate identity.
Reapplying `HIRED` to that candidate is allowed if there is no other hire. Moving an existing hire to
another permitted status frees the vacancy; hiring a candidate never closes the role automatically.

Before assigning a pending status, the command looks up the candidate's role and rejects the change
if the role is closed. Final-to-pending corrections remain available for open roles. Corrections
between final outcomes remain available for closed roles, subject to the hiring guard. Both guards
run before replacing a candidate or changing filters. Rejected commands do not trigger saving.

#### Keeping displayed indices aligned with the UI

After successful execution and saving, `LogicManager` sets `Model#isRoleListDisplayed` from
`CommandResult#isShowRoleList`, the same value `MainWindow` uses to select the visible list.
Commands do not update that flag independently. Parsing, execution, and save failures leave the
previous flag unchanged, matching the UI's error path. A save failure does not roll back an earlier
in-memory mutation, as explained in [Saving and restoring associations](#saving-and-restoring-associations).

Listing roles retains the candidate filter. Candidate-index commands use the view flag rather than
an artificial empty candidate list. If a successful candidate addition switches the UI to candidates,
both role commands reject indices until `list role` is run again. See the
[role management manual checks](#role-management) for reproducible regression scenarios.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### Candidate notes

Each `Person` has a `Note`, a free-text field for details such as an interview date and time. An empty note means the candidate has no note, and the candidate card hides it.

#### Recording a note

`NoteCommandParser` splits the arguments into the first word, which is parsed as the index, and the rest, which is trimmed and becomes the note. It does not use `ArgumentTokenizer`, so the note can contain text that looks like a prefix, such as `t/` or `n/`. An empty remainder gives an empty `Note`, so `note 1` clears the first candidate's note.

`NoteCommand#execute` looks up the candidate in the displayed candidate list and replaces it with a copy that has the new note. Other commands that rebuild a `Person`, such as `edit` and `status`, copy the existing note across, so they never change it.

Notes are stored as the `note` field of each candidate in the JSON data file. A candidate saved without a `note` field loads with an empty note.

#### Design considerations

**Aspect: How the note is entered**

* **Alternative 1 (current choice):** A separate `note INDEX [NOTE]` command that takes everything after the index.
  * Pros: Any text can be stored, including text that looks like a prefix.
  * Cons: A note cannot be given when the candidate is first added, so it takes a second command.
* **Alternative 2:** An `nt/NOTE` prefix on `add` and `edit`.
  * Pros: Consistent with the other candidate fields.
  * Cons: The tokenizer ends the note at the next prefix. For example, `nt/Discuss t/java` would save the note `Discuss` and add a `java` tag, so valid notes are silently changed. This alternative was implemented and then removed for this reason.

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

Department recruiters who:

* are responsible for filling a small number of vacancies within one department
* independently maintain candidate contact details and recruitment records
* track candidates through a simple recruitment process
* frequently need to locate a candidate, identify candidates requiring attention, and update records after interviews
* need to track the open roles available within their department
* prefer using desktop applications
* can type quickly
* prefer keyboard commands to mouse-driven navigation
* are reasonably comfortable using command-driven applications

**Value proposition**: Help a department's recruiter keep track of candidates applying for their team's open roles, including candidate details and where each candidate stands in the interview process. RecruiterBuddy is optimised for recruiters who prefer typing commands to navigating a primarily mouse-driven interface, enabling them to retrieve and update recruitment records quickly.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a … | I want to … | So that I can … |
|----------|--------|-------------|-----------------|
| `* * *` | department recruiter | add a role opening with its title and required experience level | track an active recruitment need in my department |
| `* * *` | department recruiter | view all role openings and whether they are open or closed | review the roles tracked by my department |
| `* * *` | department recruiter | close a role opening | prevent new candidates from being added after recruitment ends while retaining existing candidate records |
| `* * *` | department recruiter | delete a role with no associated candidates | remove an erroneous vacancy and reuse its title without losing candidate records |
| `* * *` | department recruiter | add a candidate to a specific open role with their contact details and address | keep the candidate's recruitment information in one place |
| `* * *` | department recruiter | be warned when a candidate's email address or phone number is already recorded | avoid creating duplicate candidate profiles |
| `* * *` | department recruiter | view all candidates | review the department's recruitment pipeline |
| `* * *` | department recruiter | assign a candidate one controlled recruitment status | record where the candidate stands in the recruitment process |
| `* * *` | department recruiter | remove a candidate's existing status and assign a new one | update the candidate as they progress through the recruitment process |
| `* * *` | department recruiter | record or reschedule a candidate's recruiter interview date and time | keep track of upcoming interviews |
| `* * *` | department recruiter | add notes to a candidate's record | retain relevant recruitment and interview information |
| `* * *` | department recruiter | remove a candidate | correct an erroneous entry or remove a record that is no longer required |
| `* * *` | user | have successful changes saved automatically and restored when RecruiterBuddy starts | avoid losing candidate and role information |
| `* * *` | user | access a guide covering all supported commands and their formats | understand and correctly use RecruiterBuddy's features |
| `* *` | potential user | see sample roles and candidates | understand how RecruiterBuddy looks when populated with department-specific information |
| `* *` | first-time user | be greeted with a brief guide to key commands | begin using the most important features without feeling lost or overwhelmed |
| `* *` | first-time user | display definitions of all candidate and role statuses with one command | understand what each recruitment stage represents |
| `* *` | user ready to use RecruiterBuddy with real data | purge all sample candidates and roles with one command | begin with a clean data set |
| `* *` | inexperienced user | receive an error message that explains how to correct an invalid command | recover from input mistakes |
| `* *` | user | undo recent changes | recover quickly from an accidental update or deletion |
| `* *` | department recruiter | replace a candidate's status with a new status using one command | move the candidate to another recruitment stage without an intermediate status-removal step |
| `* *` | department recruiter | update other candidate details | keep the record accurate when a candidate's circumstances change |
| `* *` | department recruiter | record assigned interviewers, interview locations, and multiple interview sessions | retain more complete interview information |
| `* *` | busy department recruiter | filter candidates by recruitment status, interview date, or other recorded details | identify candidates who require attention |
| `* *` | department recruiter | search for a candidate by name or other recorded details | locate a candidate without reviewing the entire list |
| `* *` | department recruiter | filter candidates by applied role | focus on one role's recruitment pipeline |
| `* *` | department recruiter re-engaging a past candidate | view the candidate's saved contact information | contact them about a new opportunity |
| `* *` | experienced user | remove multiple candidates at once | clear candidate records that are no longer required |
| `* *` | experienced user | add multiple candidate applications at once | record a batch of applications efficiently |
| `* *` | experienced user | update the statuses of multiple candidates at once | move a group of candidates to another stage or reject them together |
| `* *` | expert user | create shortcuts for frequently used commands | complete repetitive tasks more quickly |
| `* *` | expert user | export all role and candidate information to a CSV file | back up the information or use it in an external reporting tool |
| `* *` | expert user | import role and candidate information from a CSV file | restore a backup or transfer the information from another device |
| `* *` | expert user | export selected candidate information to a CSV file | share only the records needed by another authorised system or user |
| `* *` | expert user | view an audit trail of a candidate's status changes | understand how long the candidate spent at each recruitment stage |
| `* *` | department recruiter | sort candidates by name, interview time, or status | review candidates in a useful order |
| `* *` | department recruiter | filter roles by whether they are open or closed | focus on roles that require attention |
| `* *` | department recruiter | reopen a closed role | resume recruitment when a vacancy becomes available again |

### Use cases

(For all use cases below, the **System** is `RecruiterBuddy` and the **Actor** is the `Recruiter`, unless specified otherwise)

## Must-have

#### UC01 — Create Role Opening

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests a role opening with its title and required experience level.
2. RecruiterBuddy validates the supplied details and checks for an existing title.
3. RecruiterBuddy records one open vacancy, saves the data, and confirms creation.

Use case ends.

**Extensions:**

* 2a. A required field is absent, repeated, or invalid, or the title already exists.
  * 2a1. RecruiterBuddy reports the error without adding a role.
  * Use case ends.

#### UC02 — Add Candidate to Role

**System:** RecruiterBuddy<br>
**Actor:** Recruiter<br>
**Guarantees:** A candidate is recorded only under an existing open role.

**Main success scenario:**

1. Recruiter requests to add a candidate to an open role.
2. RecruiterBuddy requests the candidate’s contact information and the role.
3. Recruiter provides the requested information.
4. RecruiterBuddy records the candidate under the selected role.
5. RecruiterBuddy confirms that the candidate has been added.

Use case ends.

**Extensions:**

* 3a. Some contact information or the role is missing or invalid.
  * 3a1. RecruiterBuddy reports the error without adding the candidate.
  * Use case resumes at step 2.
* 3b. No role with the given title exists.
  * 3b1. RecruiterBuddy reports that the role must be created first (UC01).
  * Use case ends.
* 3c. The role is closed.
  * 3c1. RecruiterBuddy reports that no new candidates can be added to the role.
  * Use case ends.
* 3d. RecruiterBuddy detects a candidate with the same email address or phone number.
  * 3d1. RecruiterBuddy warns the recruiter that a matching candidate record exists.
  * 3d2. Recruiter chooses to proceed with adding the candidate.
  * Use case resumes at step 4.
  * 3d2a. Recruiter chooses to cancel adding the candidate.
    * Use case ends.

UC03 — View Command Guide
System: RecruiterBuddy
Actor: User
MSS:
1. User requests the command guide.
2. RecruiterBuddy displays the commands and their features.
3. User reviews the guide.
Use case ends.
#### UC04 — Record Interview Details

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests to record an interview for a candidate.
2. RecruiterBuddy requests the interview date and time.
3. Recruiter provides the interview date and time.
4. RecruiterBuddy records the interview in the candidate’s profile.
5. RecruiterBuddy confirms that the interview has been recorded.

Use case ends.

**Extensions:**

* 1a. The candidate does not exist.
  * 1a1. RecruiterBuddy reports the error.
  * Use case ends.
* 3a. The date or time is invalid.
  * 3a1. RecruiterBuddy reports the error without recording the interview.
  * Use case resumes at step 2.
* 3b. Recruiter also provides the assigned interviewers or the interview location.
  * 3b1. RecruiterBuddy includes them in the interview record.
  * Use case resumes at step 4.
* 4a. The candidate already has an interview recorded, and the interview is being rescheduled.
  * 4a1. RecruiterBuddy replaces the earlier date and time with the new ones.
  * Use case resumes at step 5.

UC05 — Save and Load Data
System: RecruiterBuddy
Actor: User
MSS:
1. User changes RecruiterBuddy data.
2. RecruiterBuddy saves the updated data.
3. User starts RecruiterBuddy.
4. RecruiterBuddy loads the saved data.
5. RecruiterBuddy displays the saved data to the user.
Use case ends.
#### UC06 — Add Candidate Notes

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests to add a note to a candidate’s profile.
2. RecruiterBuddy requests the note.
3. Recruiter provides the note.
4. RecruiterBuddy adds the note to the candidate’s profile.
5. RecruiterBuddy confirms that the note has been added.

Use case ends.

**Extensions:**

* 1a. The candidate does not exist.
  * 1a1. RecruiterBuddy reports the error.
  * Use case ends.
* 1b. Recruiter requests to remove the candidate’s existing notes.
  * 1b1. RecruiterBuddy removes the notes and confirms that they have been removed.
  * Use case ends.
* 3a. Recruiter provides an empty note.
  * 3a1. RecruiterBuddy reports that the note is empty, without changing the candidate’s profile.
  * Use case resumes at step 2.

#### UC07 — Close Role Opening

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests closure of a role selected from the displayed role list.
2. RecruiterBuddy checks that the role is open, all associated candidates have final outcomes,
   and at most one candidate is hired.
3. RecruiterBuddy closes the role, retaining all candidate records and their statuses.
4. RecruiterBuddy saves the change and confirms closure.

Use case ends.

**Extensions:**

* 1a. The role list is not displayed, or the selection is missing, invalid, or accompanied by extra text.
  * 1a1. RecruiterBuddy reports the error without changing records.
  * Use case ends.
* 2a. The role is already closed, candidates are pending, or multiple candidates are hired.
  * 2a1. RecruiterBuddy reports the error, including the pending count where applicable, without changing records.
  * Use case ends.

#### UC07a — Delete Role Opening

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests deletion of a mistaken role selected from the displayed role list.
2. RecruiterBuddy checks that the role has no associated candidates.
3. RecruiterBuddy removes the role, saves the data, and confirms deletion.

Use case ends.

**Extensions:**

* 1a. The role list is not displayed, or the selection is missing, invalid, or accompanied by extra text.
  * 1a1. RecruiterBuddy reports the error without changing records.
  * Use case ends.
* 2a. The role has associated candidates, regardless of their statuses or visibility.
  * 2a1. RecruiterBuddy reports their count without changing records.
  * Use case ends.

UC08 — Delete Candidate Record
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to delete candidate record(s).
2. RecruiterBuddy displays the candidate records.
3. User selects one or more records to delete.
4. RecruiterBuddy deletes the selected records.
5. RecruiterBuddy confirms that the records have been deleted.
Use case ends.

**MVP scope:** Each role represents exactly one vacancy. Updating an opening count is not supported.

## Optional

UC10 — View Sample Data
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to view sample data.
2. RecruiterBuddy displays sample roles and candidate records.
3. User reviews the sample data.
Extensions:
- 2a. User requests to clear the sample data.
  - 2a1. RecruiterBuddy removes all sample records.
  - 2a2. RecruiterBuddy confirms that the sample data has been removed.
  - Use case ends.
Use case ends.
UC11 — View Key Command Introduction
System: RecruiterBuddy
Actor: User
MSS:
1. User requests an introduction to key commands.
2. RecruiterBuddy displays an introduction to the main commands.
3. User reviews the introduction.
Use case ends.
UC12 — View Status Definitions
System: RecruiterBuddy
Actor: User
MSS:
1. User requests candidate and role status definitions.
2. RecruiterBuddy displays the status definitions.
3. User reviews the definitions.
Use case ends.
UC13 — Get Guidance for Invalid Command
System: RecruiterBuddy
Actor: User
MSS:
1. User enters a command.
2. RecruiterBuddy processes the command.
3. RecruiterBuddy displays the result.
Extensions:
- 1a. User enters an invalid command.
  - 1a1. RecruiterBuddy explains why the command is invalid and provides guidance for correcting it.
  - 1a2. User corrects and resubmits the command.
  - Use case resumes at step 2.
Use case ends.
UC14 — Undo Changes
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to undo the most recent n changes.
2. RecruiterBuddy reverses the requested changes.
3. RecruiterBuddy confirms that the changes have been undone.
Use case ends.
UC15 — Update Candidate Status
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to update a candidate’s status.
2. RecruiterBuddy displays the candidate records.
3. Recruiter selects a candidate and provides the new status.
4. RecruiterBuddy updates the candidate’s status.
5. RecruiterBuddy confirms that the status has been updated.
Extensions:
- 3a. Recruiter selects multiple candidates and chooses a status to apply to all of them.
  - 3a1. RecruiterBuddy updates the selected candidates’ statuses.
  - 3a2. RecruiterBuddy confirms that the statuses have been updated.
  - Use case ends.
Use case ends.
UC16 — Update Candidate Details
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to update a candidate’s details.
2. RecruiterBuddy displays the candidate records.
3. Recruiter selects a candidate and provides the updated details.
4. RecruiterBuddy updates the candidate’s profile.
5. RecruiterBuddy confirms that the profile has been updated.
Use case ends.
UC17 — Filter Candidates
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to filter candidates.
2. RecruiterBuddy requests the filter criteria.
3. Recruiter provides one or more criteria.
4. RecruiterBuddy displays the candidates matching the criteria.
Extensions:
- 3a. Recruiter includes a role among the filter criteria.
  - 3a1. RecruiterBuddy displays the candidates associated with the selected role.
  - Use case ends.
Use case ends.
UC18 — Search for Candidate
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to search for a candidate.
2. RecruiterBuddy requests the search details.
3. User provides the candidate’s name or other relevant details.
4. RecruiterBuddy displays matching candidate records.
Use case ends.
UC19 — Create Custom Candidate Status
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to create a custom candidate status.
2. RecruiterBuddy requests the status details.
3. User provides the details.
4. RecruiterBuddy creates the custom status.
5. RecruiterBuddy confirms that the status has been created.
Use case ends.
UC20 — View Past Applicant Contact Information
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to view a past applicant’s contact information.
2. RecruiterBuddy displays the past applicant records.
3. Recruiter selects an applicant.
4. RecruiterBuddy displays the applicant’s saved contact information.
Use case ends.
UC21 — Add Multiple Candidate Applications
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to add candidate applications.
2. RecruiterBuddy requests the application details.
3. User provides the details for one or more applications.
4. RecruiterBuddy records the applications.
5. RecruiterBuddy confirms that the applications have been added.
Use case ends.
UC22 — Create Shortcut
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to create a shortcut.
2. RecruiterBuddy requests the task and shortcut details.
3. User provides the details.
4. RecruiterBuddy creates the shortcut.
5. RecruiterBuddy confirms that the shortcut has been created.
Use case ends.
UC23 — Export All Data
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to export role and candidate data.
2. RecruiterBuddy exports the data to a local CSV file.
3. RecruiterBuddy informs the user where the file has been saved.
Extensions:
- 1a. User requests to export only selected candidate records.
  - 1a1. RecruiterBuddy displays the candidate records.
  - 1a2. User selects the records to export.
  - 1a3. RecruiterBuddy exports the selected records to a CSV file.
  - 1a4. RecruiterBuddy informs the user where the file has been saved.
  - Use case ends.
Use case ends.
UC24 — Import Data
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to import role and candidate data.
2. RecruiterBuddy requests the local CSV file.
3. User provides the file.
4. RecruiterBuddy imports the data.
5. RecruiterBuddy confirms that the data has been imported.
Use case ends.
UC25 — View Candidate Status Audit Trail
System: RecruiterBuddy
Actor: User
MSS:
1. User requests to view a candidate’s status history.
2. RecruiterBuddy displays the candidate records.
3. User selects a candidate.
4. RecruiterBuddy displays the candidate’s status changes and the time spent at each stage.
Use case ends.
*{More to be added}*

### Non-Functional Requirements

1.  RecruiterBuddy should support all its documented functions on Windows, macOS, and Linux with Java 25 as the only installed Java version.
2.  Should be able to hold up to 1000 candidates and 1000 roles without noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  With Java 25 installed, a user should be able to launch RecruiterBuddy without running an application installer or separately installing additional runtime dependencies. Extracting a ZIP file is permitted.
5.  RecruiterBuddy should be distributed as a single JAR file or, if additional files are necessary, a single ZIP file containing the JAR and those files. The distribution should not exceed 100 MB.
6.  All documented functions that operate on locally stored application data should remain usable without an Internet connection. Following links to external websites is excluded.
3.  GUI elements should be fully visible and work well on screen resolutions 1920x1080 and higher, and screen scales 100% - 125%.
4.  GUI elements should be usable on screen resolutions 1280x720 and higher, and screen scales 150%.
8.  After application data has been successfully saved, closing RecruiterBuddy normally and reopening it with the same unchanged data files should restore the saved data, including all saved additions, modifications, and deletions.
9.  A new user should be able to learn all basic commands (adding, updating and deleting candidates and roles) within 30 minutes 
10.  The application should carry out a user action and give visual confirmation within 1 second of input.
11.  A command rejected because of invalid syntax or invalid input values should leave recruitment records and user-defined configuration unchanged, both in memory and in saved data file.
12.  Command syntax should be simple and intuitive, such that an experienced user (has spent > 1 hour using the application) can memorize the syntax for all basic commands.
13.  Upon rejecting a command, the issue and its fixes should be identified, such that the user can achieve a valid input within n successive tries, where n is the number of errors in the initial input.
14.  If an application-data file cannot be read or fails validation during startup, the startup process should not modify or delete that file.
15.  All persistent RecruiterBuddy data should be stored locally in human-readable text files that can be inspected and modified using a plain-text editor. This includes recruitment records, saved candidate status history, and user-defined configuration such as command shortcuts.
16.  If writing updated data fails while the previously saved file remains accessible, RecruiterBuddy should leave that file's contents unchanged.

*{More to be added}*

### Glossary

* **Candidate**: A person recorded in RecruiterBuddy who has applied for a role opening, regardless of the current stage or outcome of their job application.
* **Candidate status**: The recorded stage or outcome of a candidate's recruitment process, as distinct from the status of a role opening.
* **Candidate status history**: A chronological record of changes to a candidate's status.
* **Command shortcut**: A user-defined abbreviation that represents a command or sequence of commands, rather than a keyboard key combination.
* **Recruitment record**: Stored information about a candidate or role opening, including any associated interview details and candidate notes.
* **Role opening**: A job vacancy in the recruiter's department tracked in RecruiterBuddy. Here, "role" refers to a job position.
* **Role status**: The recorded state of recruitment for a role opening, as distinct from an individual candidate's progress.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with sample candidates and roles. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

### Role management

Use a **disposable copy of the JAR in a separate empty folder**, with Java 25. Do not use real
recruitment data: `clear` and deletion commands below permanently remove records. Run each command
on its own line. Unless stated otherwise, reset to this fixture before each numbered scenario:

```text
clear
role add r/QA Engineer x/2 years
role add r/QA Analyst x/Entry level
add n/Alice Test p/91234561 e/alice@example.com a/1 Test Road r/QA Engineer
add n/Ben Test p/91234562 e/ben@example.com a/2 Test Road r/QA Engineer
```

Expected fixture: `list role` shows `QA Engineer` at index 1 and `QA Analyst` at index 2, both `OPEN`.
`list` shows Alice at index 1 and Ben at index 2, both `Pending Resume Screening`.
Always switch back with `list` or `list role` before using indices for that list.

#### Role creation and validation

1. Run `role add x/Entry level r/QA Designer`.<br>
   Expected: `Added open role: QA Designer.` Run `list role` to see it at index 3 with experience
   `Entry level` and status `OPEN`. Restart the app and run `list role`; the role is unchanged.

1. Run `role add r/  qa engineer  x/Senior`.<br>
   Expected: `A role named “qa engineer” already exists.` There are still two roles.
   Run `list role`, `role close 2`, then `role add r/qa analyst x/Senior`.<br>
   Expected: The empty analyst role closes successfully, but its title is still rejected as a duplicate.

1. Try the following rejected commands. Check that the role list still has only the fixture's two roles:

   * `role add r/QA Designer` → `Missing required field: x/.`
   * `role add x/Senior` → `Missing required field: r/.`
   * `role add r/QA Designer x/Senior x/Senior` → repeated-prefix error naming `x/`.
   * `role add r/123 x/Senior` → role-title constraint error.
   * `role add r/QA@Designer x/Senior` → role-title constraint error.
   * `role add r/QA Designer x/` → experience-level constraint error.
   * `role add extra r/QA Designer x/Senior` → invalid-format error with usage and example.

   Also check a title of exactly 100 letters versus 101, and an experience level of exactly 50
   printable characters versus 51. The boundary values succeed; the longer values fail.
   Use different valid titles for the experience checks so duplicate rejection does not mask validation.

#### Closure and candidate outcomes

1. Run `find Alice`, `list role`, then `role close 1`.<br>
   Expected: `Cannot close role: QA Engineer. 2 candidate(s) still pending.` Both candidates count
   even though Ben was hidden by the candidate filter. The role stays `OPEN`, with no record changes.

1. Run `status 1 HIRED`, then `find Ben` and `status 1 HIRED`.<br>
   Expected: `QA Engineer already has a hired candidate.` Alice remains hired and Ben remains pending.
   Run `find Alice` and `status 1 HIRED` again; reapplying the same status succeeds.
   Run `list`, `status 2 REJECTED`, `list role`, and `role close 1`.<br>
   Expected: `Closed role: QA Engineer.` The role is `CLOSED`; Alice and Ben are retained with their
   respective final statuses. Restart and verify the same roles and candidates are restored.

1. Run `status 1 REJECTED`, `status 2 WITHDRAWN`, `list role`, and `role close 1`.<br>
   Expected: Closure succeeds even with no hire. Separately, `list role` followed by `role close 2`
   closes the empty analyst role. No candidate is automatically hired, rejected, or removed.

1. Run `status 1 HIRED`, then `status 1 PENDING_RECRUITER_INTERVIEW`.<br>
   Expected: Returning to pending succeeds while the role is open. Repeat with each of the four
   `PENDING_*` statuses after assigning a final outcome. `list role` followed by `role close 1`
   fails while a pending candidate remains. Run `list`, `status 1 HIRED`, `status 2 REJECTED`,
   `list role`, and `role close 1`, then run `list` and `status 1 PENDING_RECRUITER_INTERVIEW`.<br>
   Expected: `Cannot set a pending status for Alice Test because role QA Engineer is closed.`
   Each of the other pending statuses is rejected too. Final-to-final corrections still succeed,
   provided they do not create a second hire; the role remains closed.

1. Close the empty analyst role using `list role` then `role close 2`. Try
   `add n/Cara Test p/91234563 e/cara@example.com a/3 Test Road r/QA Analyst`.<br>
   Expected: `The role “QA Analyst” is closed, so no new candidates can be added to it.`
   Cara is not added. Run `role close 2` again; it reports `QA Analyst is already closed.`

1. Run `status 1 HIRED`, then
   `add n/Cara Test p/91234563 e/cara@example.com a/3 Test Road r/QA Analyst`, and `status 3 HIRED`.<br>
   Expected: Both Alice and Cara may be hired because they apply to different roles. `list role`
   still shows both roles as `OPEN`; hiring does not close either role automatically.

#### Deletion and displayed-index safeguards

1. Add a third role with `role add r/QA Designer x/Senior`. Run `list role` then `role delete 2`.<br>
   Expected: `Deleted role: QA Analyst.` The engineer role remains at index 1; the designer moves
   from index 3 to index 2. Restart and verify the deletion persists. Run
   `role add r/qa analyst x/Senior`; the released title can be used again.
   With the fixture restored, also close the empty analyst role and delete it; this succeeds too.

1. Run `find Alice`, `list role`, then `role delete 1`.<br>
   Expected: `Cannot delete role: QA Engineer. It has 2 associated candidate(s).`
   No candidate or role changes. Run `list`, `status 1 HIRED`, `status 2 REJECTED`, `list role`,
   `role close 1`, then `role delete 1`; deletion remains blocked even with a closed role and final
   candidate outcomes. It never deletes candidates to make the role eligible.

1. Run `list role`, then
   `add n/Cara Test p/91234563 e/cara@example.com a/3 Test Road r/QA Analyst`.<br>
   Expected: The successful addition displays candidates. Try `role close 1` and `role delete 1`.<br>
   Expected for each: `The role list is not displayed. Use list role first.` No role changes.
   Run `list role`; the same commands can now resolve role indices, but both still fail their
   candidate checks for the engineer role.

1. Run `list role`. Try `role close`, `role close 0`, `role close -1`, `role close x`, and
   `role close 999`.<br>
   Expected: `Role index must refer to a displayed role.` Repeat with `role delete` instead of
   `role close`; the same error appears. `role close 1 extra` and `role delete 1 extra` report their
   command-specific "takes only a role index" errors. Rejected commands preserve the displayed list
   and all records. After a successful `role close 2`, a repeated closure reports that it is already closed.

### Adding a candidate

1. Adding a candidate to an existing role

   1. Prerequisites: Start with the sample data, which includes the roles `Software Engineer`, `Data Analyst` and `Product Manager`.

   1. Test case: `add n/Jane Tan p/91234567 e/jane@example.com a/Clementi Ave 2 r/Data Analyst`<br>
      Expected: Jane Tan is added to the end of the candidate list. The new card shows `Applying for: Data Analyst` and the status `Pending Resume Screening`.

   1. Test case: `add n/Ben Lim p/92345678 e/ben@example.com a/Jurong West r/product manager`<br>
      Expected: Ben Lim is added. The new card shows the role with the role's own spelling, `Applying for: Product Manager`.

   1. Test case: `add n/Carl Ng p/93456789 e/carl@example.com a/Bedok r/Designer`<br>
      Expected: No candidate is added. The error message says that no role named `Designer` exists and that the role must be added first.

   1. Test case: `add n/Carl Ng p/93456789 e/carl@example.com a/Bedok`<br>
      Expected: No candidate is added. The error message shows the correct `add` format, which includes `r/ROLE`.

   1. Test case: `add n/Alex Yeoh p/94567890 e/alex2@example.com a/Bedok r/Data Analyst`<br>
      Expected: No candidate is added, because a candidate named Alex Yeoh already exists.

### Editing a candidate

1. Editing a candidate's details

   1. Prerequisites: Start with the sample data and run `list`. The 1st candidate, Alex Yeoh, has a role, a status and a note.

   1. Test case: `edit 1 p/91112222`<br>
      Expected: Alex Yeoh's phone number changes. The role, status and note are unchanged.

   1. Test case: `edit 1 r/Data Analyst`<br>
      Expected: The candidate is not changed. The error message says that a candidate's role cannot be edited.

### Recording a note

1. Setting and clearing a candidate's note

   1. Prerequisites: Start with the sample data and run `list`.

   1. Test case: `note 2 Interview 2026-10-20 14:00`<br>
      Expected: The 2nd candidate's card shows the note `Interview 2026-10-20 14:00`.

   1. Test case: `note 2 Discuss t/java experience`<br>
      Expected: The note is replaced with `Discuss t/java experience`. No `java` tag is added.

   1. Test case: `note 2`<br>
      Expected: The 2nd candidate's note is cleared and no longer shown on the card.

   1. Test case: `note 0 Interview tomorrow`<br>
      Expected: No note is changed. The error message shows the correct `note` format.

   1. Other incorrect note commands to try: `note`, `note x Interview` (where x is larger than the list size)<br>
      Expected: No note is changed and an error message is shown.

### Deleting a candidate

1. Deleting a candidate while all candidates are being shown

   1. Prerequisites: List all candidates using the `list` command, with multiple candidates in the list.

   1. Test case: `delete 1`<br>
      Expected: The first candidate is deleted from the list. The status message shows the deleted candidate's details.

   1. Test case: `delete 0`<br>
      Expected: No candidate is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

### Saving data

Use the disposable test folder above and restore the fixture before each numbered scenario.
Close the application window before editing or moving data files.

1. Save/load round trip: create the fixture, close the empty analyst role, and delete it. Restart.<br>
   Expected: Only the open engineer role and its two pending candidates remain. Recreate the analyst
   role and restart again; the new role is restored with its new experience level and `OPEN` status.

1. Missing file: while the app is closed, move `data/addressbook.json` to a backup location within the
   test folder. Restart.<br>
   Expected: Sample roles and candidates appear, not empty lists. Close the window and restore the
   backup before continuing with the other scenarios.

1. Invalid file: back up the fixture file while the app is closed. Replace the JSON with `{` and restart.<br>
   Expected: Empty lists; the invalid file is unchanged by startup. Close the window without entering
   commands, including `exit`. Restore the backup. Repeat separately with a duplicated role title,
   a role status of `open` instead of `OPEN`, and a candidate whose `role` names a nonexistent role.<br>
   Expected: Each invalid file prevents the whole data set from loading; valid records are not imported
   separately. Recover from the backup between attempts. A successful command such as `list` would
   overwrite the invalid file, so do not issue commands before recovering it.

1. Default candidate status: remove only Alice's `status` field from a valid fixture file, then restart.<br>
   Expected: Alice loads with `Pending Resume Screening`; `list role` then `role close 1` reports two
   pending candidates. Removing a role's `status` or a candidate's `role` field instead prevents loading.

1. Legacy inconsistency guard: in a fixture backup, set both candidate statuses to `HIRED` and restart.<br>
   Expected: The records load, but `list role` then `role close 1` reports
   `Cannot close role: QA Engineer. More than one candidate is marked HIRED.` The role stays open.
   Use a separate fixture copy for this check; do not create inconsistent real recruitment data.
