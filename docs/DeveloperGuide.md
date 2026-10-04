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

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

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
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

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

(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)

## Must-have

UC01 — Create Role Opening
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to create a role opening.
2. RecruiterBuddy requests the role title and required experience level.
3. Recruiter provides the requested details.
4. RecruiterBuddy records the role opening and confirms its creation.
Use case ends.
UC02 — Add Candidate to Role
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to add a candidate to an open role.
2. RecruiterBuddy requests the candidate’s contact information and the role.
3. Recruiter provides the requested information.
4. RecruiterBuddy records the candidate under the selected role.
5. RecruiterBuddy confirms that the candidate has been added.
Extensions:
- 3a. RecruiterBuddy detects a candidate with the same email address or phone number.
  - 3a1. RecruiterBuddy warns the recruiter that a matching candidate record exists.
  - 3a2. Recruiter chooses to cancel adding the candidate.
  - Use case ends.
  - 3a3. Recruiter chooses to proceed with adding the candidate.
  - Use case resumes at step 4.
Use case ends.
UC03 — View Command Guide
System: RecruiterBuddy
Actor: User
MSS:
1. User requests the command guide.
2. RecruiterBuddy displays the commands and their features.
3. User reviews the guide.
Use case ends.
UC04 — Record Interview Details
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to record interview details for a candidate.
2. RecruiterBuddy requests the scheduled time and assigned interviewers.
3. Recruiter provides the interview details.
4. RecruiterBuddy records the details in the candidate’s profile.
5. RecruiterBuddy confirms that the interview details have been recorded.
Use case ends.
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
UC06 — Add Candidate Notes
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to add notes to a candidate’s profile.
2. RecruiterBuddy requests the notes.
3. Recruiter provides the notes.
4. RecruiterBuddy records the notes in the candidate’s profile.
5. RecruiterBuddy confirms that the notes have been added.
Use case ends.
UC07 — Close Role Opening
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to close a role opening.
2. RecruiterBuddy displays the active role openings.
3. Recruiter selects the role opening to close.
4. RecruiterBuddy closes the selected role opening.
5. RecruiterBuddy confirms that the role opening has been closed.
Use case ends.
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
UC09 — Update Number of Openings
System: RecruiterBuddy
Actor: Recruiter
MSS:
1. Recruiter requests to update the number of openings for a role.
2. RecruiterBuddy requests the role and the updated number of openings.
3. Recruiter provides the requested information.
4. RecruiterBuddy updates the number of openings for the role.
5. RecruiterBuddy confirms that the information has been updated.
Use case ends.

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
**Use case: Delete a person**

**Main Success Scenario (MSS)**

1.  User requests to list persons
2.  AddressBook shows a list of persons
3.  User requests to delete a specific person in the list
4.  AddressBook deletes the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. AddressBook shows an error message.

      Use case resumes at step 2.

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
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
