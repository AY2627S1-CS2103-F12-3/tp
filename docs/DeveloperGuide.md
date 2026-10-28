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
* Kavin Parthipan (`kpkavin04`) used OpenAI Codex to assist with requirements analysis, implementation, tests, review, and documentation for candidate role reassignment, the persistent role model, role commands, role-card summaries, role workflow diagnostics, associated candidate-status and displayed-list safeguards, and safe startup after recruitment-data loading failures. This acknowledgement covers that contribution, not other team members' tool usage.

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

The focused class diagram below shows parsing for the `role` command group. `AddressBookParser`
creates a `RoleCommandParser`, which selects the parser for `add`, `edit`, `close`, or `delete`.
That parser validates the subcommand arguments and creates its corresponding command.

<puml src="diagrams/RoleParserClassDiagram.puml" width="900"
  alt="Role command parsing: dispatch to add, edit, close and delete parsers and creation of their commands"></puml>

All five role parsers implement <code>Parser&lt;T&gt;</code>, shown by dashed arrows with hollow triangle heads.
`RoleCommandParser` returns `Command`, while each subcommand parser returns its concrete command
type. Dependencies labelled <code>&lt;&lt;create&gt;&gt;</code> show object creation. Type parameters, command inheritance, and
utility dependencies are omitted here to keep the dispatch relationships readable; the generic
parser diagram above shows the shared utilities.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103-F12-3/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="600" alt="Partial model structure showing candidates and roles" />

The partial diagram focuses on role associations. Filtered-view associations and candidate fields are
omitted for clarity; the fields of a candidate are shown in the diagram below.

<puml src="diagrams/CandidateClassDiagram.puml" width="700" alt="Fields of a candidate (Person)" />

A candidate (`Person`) is immutable and always has exactly one of each field except tags:

* Two candidates are the same person if they share an email (ignoring case) or a phone number. Names
  are not compared, as different candidates can share a name.
* The candidate's role is a `RoleTitle`, the same class that names a `Role`.
* An empty `Note` means the candidate has no note.
* `Interview.NONE` means the candidate has no interview. It is shared by every such candidate, so the
  association to `Interview` is not a composition. See [Candidate interviews](#candidate-interviews).
* A new candidate's `Status` is `RESUME`.

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
* derives role-card summaries through `RoleCandidateCounts`, an immutable snapshot of candidate-status
  totals. This helper is omitted from the partial association diagram and is not part of stored role data.
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

### Candidate Statuses

#### Representation and Persistence

The 'Status' enum defines seven candidate statuses: `RESUME`, `INTERVIEW`, `OFFER`, `DECISION`, `HIRED`, `REJECTED`, and `WITHDRAWN`. The first four are _pending_ statuses; the remaining three are _final_ statuses. New candidates are assigned the `RESUME` status by default.

When stored candidate data has no status, loading it defaults to `RESUME`. Upon successful execution of any command, `LogicManager` saves the complete addressbook, and `JsonAdaptedPerson` writes each status using its exact enum name.

An unrecognized stored value causes data loading to fail and invokes the recovery process described in [Protecting unreadable recruitment data](#protecting-unreadable-recruitment-data).

#### Batch Status Updates

Batch status updates of candidate statuses is handled by the `status` command.

`StatusCommandParser` requires exactly one `st/` prefix. It splits the preamble on whitespace, parses each index using `ParserUtil#parseIndex`, and stores the results in a set, removing duplicate indices. `ParserUtil#parseStatus` trims and uppercases the supplied value before matching it to a `Status`.

`StatusCommand` resolves every index against the _currently displayed_ candidate list, and creates replacement `Person` objects with the requested status, updates the model, resets the candidate filter to show all candidates, and returns a success result.

During running of `StatusCommandParser`, `ParseException` will be thrown on any of these conditions:
* No `st/` prefix found.
* Multiple `st/` prefixes found.
* One of the indices is not a nonzero unsigned integer.
* Normalized status does not match one of the `Status` values.

During running of `StatusCommand`, `CommandException` will be thrown on any of these conditions:
* Role list is currently being displayed.
* Index falls outside filtered candidate list.
* A pending status (`RESUME`, `INTERVIEW`, `OFFER` or `DECISION`) is being assigned to a candidate whose role is closed. (This is to ensure that closed role only ever contains candidates with a final status).
* Assigning a `HIRED` status gives a role more than one hired candidate, whether the conflict comes from an existing candidate or another candidate within the batch.

Because these checks precede mutation, failures leave all candidates unchanged.

The activity diagram below summarizes this process.

<puml src="diagrams/BatchStatusActivityDiagram.puml" width="1200"
  alt="Activity flow for parsing and executing a batch status update"></puml>

### Role creation and persistence

#### Role identity and validation

`RoleTitle` strips surrounding whitespace, validates 1–100 Unicode code points and the allowed
characters, and requires at least one letter. Allowed punctuation includes apostrophes, ampersands,
hyphens, slashes, full stops, plus signs, hashes, parentheses and commas, so titles such as
`C++ Developer`, `C# Developer`, `Engineer (Backend)` and `Manager, Sales` are accepted.
The same value object validates role creation, renaming, candidate associations and saved titles;
punctuation is retained and remains significant when comparing titles. `ExperienceLevel` similarly
strips its value and validates 1–50 printable code points, excluding control, format, surrogate, unassigned, line-separator,
and paragraph-separator characters. Their constraint messages use the same length constants as their
validation, avoiding independently hard-coded limits.

`RoleTitle#MESSAGE_CONSTRAINTS` also lists the allowed characters on a second line, so a title
with an unsupported symbol is not explained only by its length and letter requirements.
Construction, command parsing and JSON role/candidate validation reuse this same message.

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

#### Correcting a role's title or required experience

`RoleCommandParser` delegates `edit` to `EditRoleCommandParser`. The parser reads the displayed-role index with `ParserUtil#parseRoleIndex`, rejects repeated `r/` or `x/` prefixes using the existing duplicate-prefix helper, and requires at least one editable field. Supplied values use `ParserUtil#parseRoleTitle` and `ParserUtil#parseExperienceLevel`; omitted fields are left unchanged. An invalid index is reported first, followed by duplicate prefixes, missing fields, title validation and experience validation.

`EditRoleCommand` requires the role view and resolves the index against `Model#getFilteredRoleList`. It constructs the complete replacement role, preserving its status and omitted fields, then checks the full role collection for a different role with the requested title. Duplicate comparison ignores case and surrounding whitespace, including closed roles; capitalization corrections to the selected role are allowed. The shared duplicate-role message is held in `Messages`. Corrections never reopen recruitment or merge two openings.

`Model#setRole` delegates to `AddressBook#setRole`. The model validates the role replacement through `UniqueRoleList` before changing either collection. If the title's exact spelling changes, every candidate whose old title matches case-insensitively is updated to the new title using `Person#withRole`. This includes candidates hidden by the current filter. Their other details and order are unchanged; unrelated candidates retain their existing instances. An experience-only edit does not replace candidate objects. The existing observable lists are retained, so role and candidate cards refresh without new UI components. Filter predicates are retained and reevaluated after list changes.

The result shows `Edited role: TITLE.` with the updated title and keeps the role view. `EditRoleCommand` logs accepted corrections and rejected duplicate titles; `LogicManager` saves both collections through the existing path. Parser or command-validation failures leave records, filters and the saved file unchanged. The existing save-error behavior described below remains unchanged: this is not a transactional filesystem save. Once renamed, a role's old title is available again; no title alias or storage-schema migration is introduced.

The sequence below starts after parser validation. Nested alternatives reject an invalid view,
an invalid index, or a duplicate title before either list changes. Only the valid branch calls
`setRole`, which replaces the role before propagating its new title across the full candidate
list. Candidate copying is shown as a single action to omit unrelated field-construction details.
Saving occurs in `LogicManager` after the command returns successfully, as described below.

<puml src="diagrams/EditRoleSequenceDiagram.puml" width="900"
  alt="Role-edit validation, duplicate-title rejection, role replacement and title propagation to all associated candidates"></puml>

#### Saving and restoring associations

After a command executes successfully, `LogicManager#execute` asks `Storage` to save the complete
address book, including both collections. This also happens for successful read-only commands.
Parsing and command-validation failures do not reach the save call. Save errors become
`CommandException`s; earlier in-memory changes are not rolled back. Normal command saves are not
transactional and do not create backups. Startup recovery below handles unreadable data separately.

`JsonSerializableAddressBook#toModelType` reconstructs roles before candidates. Each `JsonAdaptedRole`
validates its required fields and exact `OPEN`/`CLOSED` enum name. Duplicate titles are rejected using
the same identity rule as the model. Each candidate must then reference an existing role; the loader
replaces the candidate's role title with that role's canonical stored title. Closed roles are valid
references because closure retains candidates.

An absent `roles` array is treated as empty, but loading still fails if a candidate lacks a role or
references a missing one. A missing candidate status defaults to `RESUME`, whereas
a missing role status is invalid. There is no automatic migration from old contact-only records.
Loading validates fields, duplicate records, and references; it does not enforce all command-level
recruitment rules, such as the maximum number of hired candidates in hand-edited data.

#### Protecting unreadable recruitment data

Invalid data is reported as `DataLoadingException`. JSON `null` roots and null candidate, role or tag
entries also fail loading rather than bypassing the failure path with a null-pointer exception.
`JsonUtil#fromJsonString` reads through a managed Jackson parser and checks for end of input after
deserializing the first value. Additional root values or malformed trailing text fail loading;
ordinary surrounding whitespace remains valid. This prevents a later save from silently discarding
content that Jackson would otherwise ignore. Recovery preserves the entire original file, not just
the first object, and does not attempt to combine multiple objects.
`JsonUtil#readJsonFile` returns an empty optional only when the file is known not to exist; an
indeterminate existence check does not silently treat an inaccessible file as missing.

`MainApp#init` attempts recovery after a recruitment-data loading failure. `RecruitmentDataRecovery`
first checks that the source is a regular file without following symbolic links, then moves it to a
sibling `.bak` path. `Files.move` does not use `REPLACE_EXISTING`: an existing backup, including one
created after the naming decision, is not replaced. A local timestamp in `yyyyMMdd-HHmmss-SSS` format
and then a numbered suffix resolve collisions. The format avoids characters forbidden in Windows
filenames. The original bytes are moved, not parsed, repaired or partially imported.

Only after backup succeeds does recovery write an empty `JsonSerializableAddressBook` at the original
path using `CREATE_NEW`. This refuses to overwrite a file that appears during recovery. Persisting
the empty replacement before initializing commands also makes the next startup stay empty, even if
the user exits without executing a command. The existing missing-file sample-data policy is not used
for this recovery path.

`MainApp#start` asks `UiManager` to show a warning on the JavaFX thread before opening the command
window. The warning names the full backup and replacement paths and explains manual restoration.
No automatic merge is attempted: users must preserve new records separately before restoring.
`StartupDataNotice` holds the notification text and fatal/non-fatal outcome independently of JavaFX.
Lifecycle tests capture those real notices through the notification boundary; separate notice tests
check paths and restoration instructions without substituting the message-generation logic.

If backup or replacement creation throws an I/O or security exception, initialization returns before
creating the model, logic or command UI. The startup error names the original path, failure reason,
and backup path if the move succeeded. JavaFX shuts down after dismissal. A directory or symbolic
link is not moved. `MainApp#stop` skips preference saving when no model was initialized. Preference
loading still runs before recruitment data is loaded. Preference files use the same single-root
JSON check; invalid preferences retain the existing default-settings fallback rather than using
recruitment-data backup recovery.

The activity diagram below follows the failed-loading path after preferences and storage have been
initialized. `FILE` denotes the original data filename (normally `addressbook.json`), and `TIMESTAMP`
is captured once for the recovery attempt. A move that reports an existing destination selects
the timestamped name, then `-1`, `-2`, and so on.
The loop retries the move itself rather than relying on an earlier existence check. Source-check
or move failures stop startup with the original at its source path; replacement failures stop
startup with the original in the remembered backup. Only successful preservation and replacement
lead to empty lists, a recovery warning, and an open command window.

<puml src="diagrams/RecruitmentDataStartupActivityDiagram.puml" width="900"
  alt="Startup recovery with non-overwriting backup selection, collision retries, empty startup and fatal failure branches"></puml>

Recovery preserves the source before exposing any command that can reach the normal save path.
If recovery cannot complete safely, startup remains blocked rather than risking the original or its
backup. If the file is genuinely absent instead, startup still uses
`SampleDataUtil#getSampleAddressBook`. A valid empty file loads empty lists, not sample data.

Recovery diagnostics follow the same lifecycle: `MainApp` emits a loading `WARNING`, `RecruitmentDataRecovery` records each completed backup and replacement at `INFO`, and unsafe recovery ends with a `SEVERE` event from `MainApp`. Success messages name the paths actually used and are not emitted for a stage that failed. Existing startup and recovery tests now check these events alongside preserved file contents and the absence of a command UI after failure. The shared `LogCapture` test utility restores logging configuration after each capture. See [Recovery diagnostics](Logging.md#recruitment-data-recovery-diagnostics) for interpretation and privacy limitations; no extra production logging or recovery-policy change is needed.

### Role lifecycle and candidate safeguards

#### Closing recruitment

`RoleCommandParser` delegates `close` to `CloseRoleCommandParser`. Close and delete share
`ParserUtil#parseRoleIndex` to reject missing, invalid, or extra index arguments consistently.
`CloseRoleCommand` first checks `Model#isRoleListDisplayed`, then resolves the index against
`Model#getFilteredRoleList` and rejects an already closed role.

Candidate checks use the complete `ReadOnlyAddressBook#getPersonList`, not the filtered candidate
list, matching role titles without case sensitivity. `Status#isPending` classifies statuses `RESUME`, `INTERVIEW`, `OFFER` and `DECISION` as pending. Closure rejects pending candidates with their
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
or reassigns candidates. Deletion takes effect immediately, but can be undone in the current session.

#### Undoing and redoing role changes

Close and delete inherit `Command#isUndoable` returning `true`. Before execution, `LogicManager`
asks `ModelManager#recordAddressBookState` to retain a complete `AddressBook` snapshot, including
roles and candidates. Successful execution clears redo history through `commitAddressBookState`;
an execution failure discards the pending snapshot instead.

`undo` and `redo` restore these snapshots in reverse and forward order respectively, and
`LogicManager` saves the restored records. Undo can therefore restore a deleted role or reverse a
closure without a dedicated reopen command. Later changes must be undone first. The history stacks
are held in memory only: restarting loads saved records but does not restore undo or redo history.
Run `list role` after undo or redo to display the updated roles; listing does not change history.

#### Status changes and one hire per role

Before assigning `HIRED`, `StatusCommand` checks amongst candidates to be assigned the `HIRED` status, as well as all stored candidates with the `HIRED` status, for another candidate with a case-insensitively matching role title, excluding the selected candidate by candidate identity. Applying or reapplying `HIRED` to that candidate is allowed only if there is no other hire. Moving an existing hire to another permitted status frees the vacancy; hiring a candidate never closes the role automatically.

Before assigning a pending status (`RESUME`, `INTERVIEW`, `OFFER` or `DECISION`), the command looks up the candidate's role and rejects the change if the role is closed. Final-to-pending corrections remain available for open roles. Corrections between final outcomes remain available for closed roles, subject to the hiring guard. Both guards run before replacing a candidate or changing filters. Rejected commands do not trigger saving.

#### Keeping displayed indices aligned with the UI

After successful execution and saving, `LogicManager` sets `Model#isRoleListDisplayed` from
`CommandResult#isShowRoleList`, the same value `MainWindow` uses to select the visible list.
Commands do not update that flag independently. Parsing, execution, and save failures leave the
previous flag unchanged, matching the UI's error path. A save failure does not roll back an earlier
in-memory mutation, as explained in [Saving and restoring associations](#saving-and-restoring-associations).

Listing roles retains the candidate filter. Candidate-index commands use the view flag rather than
an artificial empty candidate list. If a successful candidate addition switches the UI to candidates,
role edit, close, and delete reject indices until `list role` is run again. See the
[role management manual checks](#role-management) for reproducible regression scenarios.

### Candidate-status totals on role cards

`RoleCandidateCounts` scans a supplied candidate collection for a case-insensitively matching
`RoleTitle`, then snapshots counts by `Status`. Pending totals use `Status#isPending` rather than
hard-coded pending-status names. Each candidate contributes once, and an absent category returns
zero. The summary reports actual records, including any inconsistent hand-edited hire counts;
it never changes statuses or enforces command-level recruitment rules.

`Logic#getPersonList` exposes the address book's unmodifiable observable candidate list, not the
filtered list used by the candidate panel. `MainWindow` passes it to `RoleListPanel` alongside the
existing observable role list. A panel-lifetime candidate listener refreshes the role list after
additions, replacements, removals, and resets. List cells construct fresh summaries when rendering
their roles, including when scrolling or returning from the candidate view. Listeners are not
attached to individual recycled cells. Role replacements still use the existing role-list binding.

`RoleCard` displays four explicit labels in a wrapping `FlowPane`. Cards bind their width to the
available list-cell width, so long role details wrap without forcing horizontal scrolling. Role
indices retain their full width beside wrapped titles. Both open and closed roles use the same
calculation. Counts are not cached in `Role` or saved to JSON, so loading data reconstructs
them without a schema migration or a second source of truth. Candidate filters, role order, indices,
and command feedback are unchanged.

Unit tests cover classification, title matching, empty states, snapshots, and mixed-role data.
Logic integration tests check unfiltered observation, status changes, additions, deletion, role
reassignment through the edit command and model API, resets, and reload. The existing Codecov exclusions for
`RoleCard`, `RoleListPanel`, and `MainWindow` remain unchanged; manual packaged-JAR tests cover their
rendering and refresh wiring rather than treating excluded UI code as automatically verified.

### Correcting a candidate's role

`EditCommandParser` accepts `r/` as an optional, non-repeatable edit field. It uses
`ParserUtil#parseRoleTitle` for the existing title constraints and stores the result in
`EditPersonDescriptor`, including its copy, equality and diagnostic-string behaviour.

`EditCommand` resolves an explicitly requested role through `Model#findRole` before replacing any
records or changing filters. A missing role is rejected; a different role must be open. The source
may be open or closed, and selecting the existing role remains a permitted no-op. Resolution uses
case-insensitive matching and the stored role's canonical spelling. Omitting `r/` preserves the role.
The shared `EditCommand#isRoleChanged` predicate keeps the closed-destination and hiring guards
consistent about whether the operation actually transfers the candidate.

The edited immutable `Person` keeps its status and note, and keeps every other field not explicitly
edited. The existing candidate-identity duplicate check is unchanged. For a real role transfer,
`CandidateRoleChecks#verifyHiringAllowed` rejects a `HIRED` candidate if another stored candidate
for the destination is hired. It uses all records, not the filtered list, and excludes the original
candidate by identity rather than the edited name. `StatusCommand` validates batch status changes
separately: before changing any candidate, it checks both existing hires across all stored candidates
and conflicting hires within the selected batch. Both workflows enforce one hired candidate per role;
they share error messages, not the validation method.

After validation succeeds, `Model#setPerson` updates the observable candidate collection and the
candidate filter resets to show all records. The existing `Edited candidate: ...` result formats the
updated candidate, including the new name and canonical role. Normal `LogicManager` saving persists
the association without a JSON schema change. Validation failures leave records and filters
unchanged and do not save. Neither role's status changes automatically.

### Role workflow diagnostics and internal checks

Role commands and candidate-role transfers reuse `LogsCenter`, with `INFO` for successful in-memory changes and `FINE` for expected recruitment-rule rejections. Closure and deletion refusals include relevant candidate counts, while successful indexed operations identify the displayed index. These events precede storage saving and never claim that persistence succeeded. The messages omit candidate names, contact details, notes and raw commands; the pre-existing central command log is not covered by this restriction. See the [logging guide](Logging.md#role-workflow-diagnostics) for levels and limitations.

Assertions check programmer assumptions rather than user input. `CandidateRoleChecks#verifyHiringAllowed` requires a non-null internal status so a caller cannot accidentally bypass the single-hire check by omitting it. `EditCommand#resolveRole` checks that `Model#findRole` returns a title matching the request before any mutation. Candidate input, missing roles, closed destinations and duplicate hires still use normal validation and `CommandException`; disabling assertions does not disable those checks. Existing collection null and uniqueness checks remain unchanged. No essential work is performed inside an assertion.

`RoleWorkflowDiagnosticsTest` checks operation logs and their levels, guards against logging candidate details, and verifies unchanged state after refusal. Focused contract tests inject programmer errors before mutation, while the existing role, transfer, integration and persistence tests exercise valid paths. See the [testing guide](Testing.md#assertions-and-role-workflow-diagnostics) for assertion-enabled and assertion-disabled checks. Command formats, success/error messages, UI behavior and the storage schema are unchanged.

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

### Candidate phone numbers

`Phone` accepts 8 to 15 digits with an optional leading `+`, and removes spaces and hyphens before saving. Two kinds of phone problems are checked when a candidate is added or their phone number is edited:

* **Exact duplicates are rejected.** `Person#isSamePerson` compares phone numbers with `Phone#equals`, which compares the saved digits exactly. `UniquePersonList` and the data-file loader use the same rule.
* **Possible problems only add a note to the success message.** `PhoneChecks#appendPhoneWarnings` runs after `AddCommand` or `EditCommand` has stored the candidate. `EditCommand` calls it only when `p/` is given.
  * `Phone#isPlausibleNumber` treats a number without a `+`, or with `+65`, as a Singapore number, which has 8 digits starting with 3, 6, 8 or 9. Numbers with another country code are not checked, since overseas formats vary.
  * `Phone#isPossiblySameNumber` compares numbers after removing the `+` and, if `65` is followed by exactly 8 digits, the `65`. So `+6591234567`, `6591234567` and `91234567` may be the same number, but are not exact duplicates.

#### Design considerations

**Aspect: What to do with a number that may be another candidate's**

* **Alternative 1 (current choice):** Accept it and add a note naming the candidates whose number may be the same.
  * Pros: A real but unusual number, such as an overseas number written without its `+`, is never blocked.
  * Cons: The user may miss the note and keep a duplicate.
* **Alternative 2:** Reject it, like an exact duplicate.
  * Pros: Duplicates cannot be added by mistake.
  * Cons: Wrongly blocks different numbers that happen to match, such as an overseas number without its country code. The loader would also have to reject such pairs, so data files saved by older versions could stop loading.

The loader must keep using only the exact rule. Otherwise, a pair of candidates accepted with a note could be saved in a file that then fails to load.

### Candidate notes

Each `Person` has a `Note`, a free-text field for details such as a candidate's skills or availability. Interview dates and times have their own field, described in [Candidate interviews](#candidate-interviews). An empty note means the candidate has no note, and the candidate card hides it.

#### Recording a note

`NoteCommandParser` splits the arguments into the first word, which is parsed as the index, and the rest, which is trimmed and becomes the note. It does not use `ArgumentTokenizer`, so the note can contain text that looks like a prefix, such as `t/` or `n/`. An empty remainder gives an empty `Note`, so `note 1` clears the first candidate's note.

`NoteCommand#execute` looks up the candidate in the displayed candidate list and replaces it with a copy that has the new note. Other commands that rebuild a `Person`, such as `edit`, `status`, and `interview`, copy the existing note across, so they never change it.

Notes are stored as the `note` field of each candidate in the JSON data file. A candidate saved without a `note` field loads with an empty note.

#### Design considerations

**Aspect: How the note is entered**

* **Alternative 1 (current choice):** A separate `note INDEX [NOTE]` command that takes everything after the index.
  * Pros: Any text can be stored, including text that looks like a prefix.
  * Cons: A note cannot be given when the candidate is first added, so it takes a second command.
* **Alternative 2:** An `nt/NOTE` prefix on `add` and `edit`.
  * Pros: Consistent with the other candidate fields.
  * Cons: The tokenizer ends the note at the next prefix. For example, `nt/Discuss t/java` would save the note `Discuss` and add a `java` tag, so valid notes are silently changed. This alternative was implemented and then removed for this reason.

### Candidate interviews

Each `Person` has an `Interview`, which holds the date and time of the candidate's interview, to the minute. A candidate with no interview has `Interview.NONE`, so no code needs to handle a `null` interview. The candidate card shows the interview as `Interview: YYYY-MM-DD HH:MM`, and hides the line for `Interview.NONE`.

#### Validating a date and time

`Interview` accepts only zero-padded `YYYY-MM-DD HH:MM` values that are real dates and valid 24-hour times. It checks a value in two steps, so that each kind of mistake gets its own error message:

1. `Interview#isValidFormat` checks the shape with a regular expression. A value such as `2026-10-20 9:00` fails here, and the user is told the expected format.
2. `Interview#isValidDateTime` parses the value with the pattern `uuuu-MM-dd HH:mm` and `ResolverStyle.STRICT`. A value with the right shape but an impossible date or time, such as `2026-02-30` or `24:00`, fails here, and the user is told the value does not exist.

Strict resolving is needed because the default resolver silently adjusts `2026-02-30` to `2026-02-28`. The pattern uses `uuuu` (year) instead of `yyyy` (year of era), because strict resolving rejects every `yyyy` year that has no era. The `Interview` constructor enforces both checks with `checkArgument`, so an invalid interview cannot be created even when assertions are disabled. Past dates are allowed, so recruiters can record interviews that have already happened.

#### Scheduling or clearing an interview

`InterviewCommandParser` tokenizes the arguments with the `d/` prefix. The preamble must be exactly one index. If `d/` is present, `ParserUtil#parseInterview` runs the two checks above and creates the `Interview`. If `d/` is left out, the parser uses `Interview.NONE`, so `interview 1` clears the first candidate's interview. An empty `d/` is rejected with the format error instead of clearing the interview, so a user who forgets to type the date does not lose the existing one.

The sequence diagram below shows how `interview 1 d/2026-10-20 14:00` is executed, including the two date and time checks and the branch that clears the interview when `d/` is left out.

<puml src="diagrams/InterviewSequenceDiagram.puml" alt="Interactions for the interview 1 d/2026-10-20 14:00 command" />

`InterviewCommand#execute` looks up the candidate in the displayed candidate list and replaces it with `Person#withInterview`, a copy that has the new interview. A candidate has only one interview, so scheduling another one replaces the previous interview, and `interview INDEX` replaces it with `Interview.NONE`. Unlike `note`, it does not reset the candidate filter, so a recruiter can schedule interviews for a filtered list by its displayed numbers. Clearing a candidate with no interview, or giving the same date and time again, succeeds without changing the data, as `note` does for notes.

Other commands that rebuild a `Person`, such as `edit`, `note`, and `status`, copy the existing interview across, so they never change it.

#### Saving and loading interviews

Interviews are stored as the `interview` field of each candidate in the JSON data file, in the same `YYYY-MM-DD HH:MM` form. `Interview.NONE` is saved as an empty string. When loading, a missing, `null`, or empty `interview` field gives `Interview.NONE`, so data files saved before interviews existed still load. Any other value goes through the same two checks, and an invalid value stops the file from loading, as an invalid phone number does.

#### Design considerations

**Aspect: Where the interview is stored**

* **Alternative 1 (current choice):** A separate `Interview` field with its own command.
  * Pros: The date and time are checked, so impossible or mistyped slots are rejected. The value can later be used to sort candidates by interview time.
  * Cons: Every place that rebuilds a `Person` must copy the new field, or that command would silently clear the interview.
* **Alternative 2:** Keep interviews in the free-text note.
  * Pros: No new field or command.
  * Cons: Nothing checks the date, and the date cannot be compared or sorted.

**Aspect: How "no interview" is represented**

* **Alternative 1 (current choice):** An `Interview.NONE` value, whose date and time is held in a private nullable field and returned as an `Optional<LocalDateTime>`.
  * Pros: `Person` fields are never `null`, and the `null` never leaves the `Interview` class.
  * Cons: Code that needs the date and time must handle the empty `Optional`.
* **Alternative 2:** A sentinel date, such as `0000-01-01 00:00`.
  * Pros: Every interview has a real `LocalDateTime`.
  * Cons: The sentinel could be shown or sorted as if it were a real interview, and a user could enter the same value.

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
| `* *` | department recruiter | correct a role's title or required experience without recreating it | keep its candidates and recruitment history intact |
| `* * *` | department recruiter | view all role openings and whether they are open or closed | review the roles tracked by my department |
| `* *` | department recruiter | see pending and final-outcome totals for each role | assess recruitment progress without inspecting every candidate |
| `* * *` | department recruiter | close a role opening | prevent new candidates from being added after recruitment ends while retaining existing candidate records |
| `* * *` | department recruiter | delete a role with no associated candidates | remove an erroneous vacancy and reuse its title without losing candidate records |
| `* * *` | department recruiter | add a candidate to a specific open role with their contact details and address | keep the candidate's recruitment information in one place |
| `* * *` | department recruiter | correct a candidate's role without recreating their record | preserve their recruitment progress, notes and contact information |
| `* * *` | department recruiter | be stopped from adding a candidate who is already recorded | avoid creating duplicate candidate profiles |
| `* * *` | department recruiter | view all candidates | review the department's recruitment pipeline |
| `* * *` | department recruiter | assign a candidate one controlled recruitment status | record where the candidate stands in the recruitment process |
| `* * *` | department recruiter | update a candidate's recruitment status | reflect their progress through the recruitment process |
| `* * *` | department recruiter | record a candidate's interview date and time | refer to when each candidate is being interviewed |
| `* * *` | department recruiter | add notes to a candidate's record | retain relevant recruitment and interview information |
| `* * *` | department recruiter | remove a candidate | correct an erroneous entry or remove a record that is no longer required |
| `* * *` | user | have successful changes saved automatically and restored when RecruiterBuddy starts | avoid losing candidate and role information |
| `* * *` | user | access a guide covering all supported commands and their formats | understand and correctly use RecruiterBuddy's features |
| `* *` | department recruiter | be warned when a new candidate's email address or phone number matches an existing candidate | catch the same person entered under a different or misspelled name |
| `* *` | department recruiter | have an interview's date and time checked when I record or reschedule it | avoid saving an impossible or mistyped interview slot |
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
| `*` | department recruiter | search or filter candidates by interview date and time | quickly see who is being interviewed on a given day |

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

#### UC03 — View Command Guide

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

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
* 1b. Recruiter requests to clear the candidate's interview.
  * 1b1. RecruiterBuddy removes the candidate's interview, if any, and confirms that it has been cleared.
  * Use case ends.
* 3a. The date and time are not in the required format.
  * 3a1. RecruiterBuddy reports the required format without recording the interview.
  * Use case resumes at step 2.
* 3b. The date or time does not exist, such as 30 February or 24:00.
  * 3b1. RecruiterBuddy reports the error without recording the interview.
  * Use case resumes at step 2.
* 4a. The candidate already has an interview recorded, and the interview is being rescheduled.
  * 4a1. RecruiterBuddy replaces the earlier date and time with the new ones.
  * Use case resumes at step 5.

#### UC05 — Save and Load Data

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

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

#### UC08 — Delete Role Opening

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

#### UC09 — Delete Candidate Record

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests to delete a candidate selected from the displayed candidate list.
2. RecruiterBuddy deletes the candidate.
3. RecruiterBuddy confirms that the candidate has been deleted.

Use case ends.

**Extensions:**

* 1a. The selected candidate does not exist in the displayed list.
  * 1a1. RecruiterBuddy reports the error without changing records.
  * Use case ends.

## Optional

#### UC10 — View Sample Data

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests to view sample data.
2. RecruiterBuddy displays sample roles and candidate records.
3. User reviews the sample data.

Use case ends.

**Extensions:**

* 2a. User requests to clear the sample data.
  * 2a1. RecruiterBuddy removes all sample records.
  * 2a2. RecruiterBuddy confirms that the sample data has been removed.
  * Use case ends.

#### UC11 — View Key Command Introduction

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests an introduction to key commands.
2. RecruiterBuddy displays an introduction to the main commands.
3. User reviews the introduction.

Use case ends.

#### UC12 — View Status Definitions

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests candidate and role status definitions.
2. RecruiterBuddy displays the status definitions.
3. User reviews the definitions.

Use case ends.

#### UC13 — Get Guidance for Invalid Command

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User enters a command.
2. RecruiterBuddy processes the command.
3. RecruiterBuddy displays the result.

Use case ends.

**Extensions:**

* 1a. User enters an invalid command.
  * 1a1. RecruiterBuddy explains why the command is invalid and provides guidance for correcting it.
  * 1a2. User corrects and resubmits the command.
  * Use case resumes at step 2.

#### UC14 — Undo Changes

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests to undo the most recent n changes.
2. RecruiterBuddy reverses the requested changes.
3. RecruiterBuddy confirms that the changes have been undone.

Use case ends.

#### UC15 — Update Candidate Status

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests to update a candidate’s status.
2. RecruiterBuddy displays the candidate records.
3. Recruiter selects a candidate and provides the new status.
4. RecruiterBuddy updates the candidate’s status.
5. RecruiterBuddy confirms that the status has been updated.

Use case ends.

**Extensions:**

* 3a. Recruiter selects multiple candidates and chooses a status to apply to all of them.
  * 3a1. RecruiterBuddy updates the selected candidates’ statuses.
  * 3a2. RecruiterBuddy confirms that the statuses have been updated.
  * Use case ends.

#### UC16 — Update Candidate Details

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests to update a candidate’s details.
2. RecruiterBuddy displays the candidate records.
3. Recruiter selects a candidate and provides the updated details.
4. RecruiterBuddy updates the candidate’s profile.
5. RecruiterBuddy confirms that the profile has been updated.

Use case ends.

#### UC17 — Filter Candidates

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests to filter candidates.
2. RecruiterBuddy requests the filter criteria.
3. Recruiter provides one or more criteria.
4. RecruiterBuddy displays the candidates matching the criteria.

Use case ends.

**Extensions:**

* 3a. Recruiter includes a role among the filter criteria.
  * 3a1. RecruiterBuddy displays the candidates associated with the selected role.
  * Use case ends.

#### UC18 — Search for Candidate

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests to search for a candidate.
2. RecruiterBuddy requests the search details.
3. User provides the candidate’s name or other relevant details.
4. RecruiterBuddy displays matching candidate records.

Use case ends.

#### UC19 — Create Custom Candidate Status

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests to create a custom candidate status.
2. RecruiterBuddy requests the status details.
3. User provides the details.
4. RecruiterBuddy creates the custom status.
5. RecruiterBuddy confirms that the status has been created.

Use case ends.

#### UC20 — View Past Applicant Contact Information

**System:** RecruiterBuddy<br>
**Actor:** Recruiter

**Main success scenario:**

1. Recruiter requests to view a past applicant’s contact information.
2. RecruiterBuddy displays the past applicant records.
3. Recruiter selects an applicant.
4. RecruiterBuddy displays the applicant’s saved contact information.

Use case ends.

#### UC21 — Add Multiple Candidate Applications

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests to add candidate applications.
2. RecruiterBuddy requests the application details.
3. User provides the details for one or more applications.
4. RecruiterBuddy records the applications.
5. RecruiterBuddy confirms that the applications have been added.

Use case ends.

#### UC22 — Create Shortcut

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests to create a shortcut.
2. RecruiterBuddy requests the task and shortcut details.
3. User provides the details.
4. RecruiterBuddy creates the shortcut.
5. RecruiterBuddy confirms that the shortcut has been created.

Use case ends.

#### UC23 — Export All Data

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests to export role and candidate data.
2. RecruiterBuddy exports the data to a local CSV file.
3. RecruiterBuddy informs the user where the file has been saved.

Use case ends.

**Extensions:**

* 1a. User requests to export only selected candidate records.
  * 1a1. RecruiterBuddy displays the candidate records.
  * 1a2. User selects the records to export.
  * 1a3. RecruiterBuddy exports the selected records to a CSV file.
  * 1a4. RecruiterBuddy informs the user where the file has been saved.
  * Use case ends.

#### UC24 — Import Data

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests to import role and candidate data.
2. RecruiterBuddy requests the local CSV file.
3. User provides the file.
4. RecruiterBuddy imports the data.
5. RecruiterBuddy confirms that the data has been imported.

Use case ends.

#### UC25 — View Candidate Status Audit Trail

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User requests to view a candidate’s status history.
2. RecruiterBuddy displays the candidate records.
3. User selects a candidate.
4. RecruiterBuddy displays the candidate’s status changes and the time spent at each stage.

Use case ends.

#### UC26 — Correct Role Details

**System:** RecruiterBuddy<br>
**Actor:** Recruiter<br>
**Guarantees:** Role status and candidate details are preserved. Rejected input changes no records.

**Main success scenario:**

1. Recruiter requests the list of roles.
1. RecruiterBuddy displays the roles and their indices.
1. Recruiter identifies a displayed role and provides a corrected title, required experience, or both.
1. RecruiterBuddy updates the role and every associated candidate's role title, then saves the records.
1. RecruiterBuddy confirms the update using the role's updated title.

Use case ends.

**Extensions:**

* 3a. The index or field input is invalid, no fields are supplied, or a field is repeated.
  * 3a1. RecruiterBuddy reports the input error without changing records.
  * Use case ends.
* 4a. Another role already has the requested title, ignoring case and surrounding whitespace.
  * 4a1. RecruiterBuddy rejects the complete edit without changing either role fields or candidates.
  * Use case ends.
* 4b. The updated records cannot be saved.
  * 4b1. RecruiterBuddy reports the save error instead of the normal confirmation; changes may remain in the current session.
  * Use case ends.

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
14.  If recruitment data cannot be read or fails validation during startup, preserve its original contents in a non-overwriting sibling backup before creating an empty replacement. If safe recovery fails, do not expose commands; identify the preserved data location and recovery steps.
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

   1. Open a terminal in that folder and run `java -jar addressbook.jar`.<br>
      Expected: The GUI opens with sample candidates and roles. The window size may not be optimal.
      `preferences.json` and `addressbook.log` are created in that folder.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by running the same command from the same folder.<br>
       Expected: The most recent window size and location are retained.

### Role management

Use a **disposable copy of the JAR in a separate empty folder**, with Java 25. Do not use real
recruitment data: `clear` and deletion commands below immediately change the saved records, and
undo history is lost on exit. Run each command
on its own line. Unless stated otherwise, reset to this fixture before each numbered scenario:

```text
clear
role add r/QA Engineer x/2 years
role add r/QA Analyst x/Entry level
add n/Alice Test p/91234561 e/alice@example.com a/1 Test Road r/QA Engineer
add n/Ben Test p/91234562 e/ben@example.com a/2 Test Road r/QA Engineer
```

Expected fixture: `list role` shows `QA Engineer` at index 1 and `QA Analyst` at index 2, both `OPEN`.
`list` shows Alice at index 1 and Ben at index 2, both `Resume`.
Always switch back with `list` or `list role` before using indices for that list.

#### Role-card candidate totals

1. Run `list role` with the fixture above.<br>
   Expected: `QA Engineer` shows `Pending: 2`, `Hired: 0`, `Rejected: 0`, `Withdrawn: 0`.
   `QA Analyst` shows zero in every category. Run `find Alice`, then `list role`; the engineer
   totals remain unchanged because Ben is still included despite the candidate filter.

1. Run `status 1 st/HIRED`, `status 2 st/REJECTED`, then `list role`.<br>
   Expected: The engineer totals are `Pending: 0`, `Hired: 1`, `Rejected: 1`, `Withdrawn: 0`.
   Run `role close 1`; the role becomes `CLOSED` without changing its counts. Run `list`,
   `status 2 st/WITHDRAWN`, then `list role`; rejected becomes zero and withdrawn becomes one.
   Restart and run `list role`; the same totals are reconstructed.

1. Run `delete 2`, then `list role`.<br>
   Expected: The engineer pending total drops to one; other totals remain zero. Run `list` and add
   another candidate to `QA Engineer`; the pending total increases when roles are listed again.
   Run `clear`, then `list role`; no old role cards remain. Adding a new empty role shows four zeros.

1. Scroll through enough roles to recycle list cells, then return to the original roles.<br>
   Expected: Their totals remain associated with the correct titles. Resize the window and verify
   that all four labelled counts wrap without clipping, with the existing title, experience, status,
   command field, and scrolling still usable. Check the course-required screen sizes and scaling.

#### Role creation and validation

1. Run `role add r/Engineer@Platform x/2 years`.<br>
   Expected: No role is created. The first error line states the length and letter requirements;
   the second reads `Use only letters, digits, spaces, or these symbols: ' & - / . + # ( ) ,`.
   The allowed-symbol list excludes `@`, explaining why this title fails despite its valid length
   and letters. Correcting the command to `role add r/Engineer-Platform x/2 years` succeeds.
   Check that both error lines are readable in the result display.

1. In a separate disposable copy, run `role add r/C++ Developer x/2 years`,
   `role add r/C# Developer x/2 years`, `role add r/Engineer (Backend) x/2 years`, and
   `role add r/Manager, Sales x/5 years`, then `list role`.<br>
   Expected: All four roles are added as `OPEN`, with their punctuation unchanged.
   Try `role add r/  c++ developer  x/Senior`; it is rejected as a duplicate, while the
   `C# Developer` role remains distinct. Add a candidate using `r/c++ developer`; the candidate
   uses the stored spelling `C++ Developer`. Run `list role` and rename that role using
   `role edit INDEX r/Engineer (C++), Backend`, replacing `INDEX` with its displayed role index.
   Expected: Both the role and its candidate use the new title. Restart and check that all
   titles and associations remain unchanged. The ordinary length and letter requirements still
   apply: `role add r/+#(), x/Senior` and `role add r/C++@Developer x/Senior` are rejected.

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

#### Role corrections

1. Run `list role`, then `role edit 1 x/5 years`.<br>
   Expected: `Edited role: QA Engineer.` The role card shows `5 years`, with the title and `OPEN`
   status unchanged. Both candidates are retained with their original details and statuses.
   Restart and run `list role`; the updated experience is restored.

1. Run `list role`, `role close 2`, then `role edit 2 x/Senior`.<br>
   Expected: `Edited role: QA Analyst.` Its experience changes to `Senior` but its status stays
   `CLOSED`. Repeating the same edit succeeds without changing the data.

1. Run `find Alice`, then `role edit 1 x/Senior`.<br>
   Expected: The role-view guard asks for `list role`; no data changes. Run `list role`, then
   `role edit 1 x/Senior`. The role card updates. Run `help` to return to the candidate view without
   changing its filter; only Alice is still displayed. Run `list` to restore all candidates.

1. Run `list role` and try `role edit 0 x/Senior`, `role edit 99 x/Senior`, `role edit 1`,
   `role edit 1 x/`, `role edit 1 x/Junior x/Senior`, and `role edit 1 x/Senior r/`.<br>
   Expected: Respectively, invalid-index errors, no editable fields, invalid experience, repeated `x/`,
   and invalid title errors. Records, current filters, and saved data remain unchanged.
   An experience of exactly 50 printable characters succeeds; 51 is rejected. Embedded tabs,
   line separators, and control characters are not valid experience descriptions.

1. Run `find Alice`, `list role`, then `role edit 1 r/Platform Engineer x/Senior`.<br>
   Expected: `Edited role: Platform Engineer.` Both Alice and hidden Ben now refer to Platform Engineer; names, contact details, notes, tags and statuses are unchanged. The role remains OPEN, its experience is Senior, and the role view remains displayed. Run `help` to show candidates without resetting the filter; only Alice is displayed, with the new title. Run `list` to see both candidates. Restart and verify that the role and both associations persist.

1. Restore the fixture, run `list role`, then try `role edit 1 r/qa analyst x/Senior` and `role edit 1 r/QA Engineer r/Other`.<br>
   Expected: The first reports the duplicate QA Analyst title without changing experience or candidates; the second reports repeated `r/`. Repeat the duplicate check after closing the empty analyst role: its title remains reserved. An invalid title or experience in a combined edit must leave both fields and all candidates unchanged.

1. Restore the fixture, run `list role`, then `role edit 1 r/qa ENGINEER`.<br>
   Expected: The role and both candidates use exactly `qa ENGINEER`. Repeating that edit is a successful no-op. Rename the empty analyst role after closing it; it remains CLOSED. Create a new role with its old title: existing candidates, if any, remain with the renamed role rather than the new opening.

#### Closure and candidate outcomes

1. Run `find Alice`, `list role`, then `role close 1`.<br>
   Expected: `Cannot close role: QA Engineer. 2 candidate(s) still pending.` Both candidates count
   even though Ben was hidden by the candidate filter. The role stays `OPEN`, with no record changes.

1. Run `status 1 st/HIRED`, then `find Ben` and `status 1 st/HIRED`.<br>
   Expected: `QA Engineer already has a hired candidate.` Alice remains hired and Ben remains pending.
   Run `find Alice` and `status 1 st/HIRED` again; reapplying the same status succeeds.
   Run `list`, `status 2 st/REJECTED`, `list role`, and `role close 1`.<br>
   Expected: `Closed role: QA Engineer.` The role is `CLOSED`; Alice and Ben are retained with their
   respective final statuses. Restart and verify the same roles and candidates are restored.

1. Run `status 1 st/REJECTED`, `status 2 st/WITHDRAWN`, `list role`, and `role close 1`.<br>
   Expected: Closure succeeds even with no hire. Separately, `list role` followed by `role close 2`
   closes the empty analyst role. No candidate is automatically hired, rejected, or removed.

1. Run `status 1 st/HIRED`, then `status 1 st/INTERVIEW`.<br>
   Expected: Returning to pending succeeds while the role is open. Repeat with each of the four
   pending statuses (`RESUME`, `INTERVIEW`, `OFFER`, `DECISION`) after assigning a final outcome. `list role` followed by `role close 1`
   fails while a pending candidate remains. Run `list`, `status 1 st/HIRED`, `status 2 st/REJECTED`,
   `list role`, and `role close 1`, then run `list` and `status 1 st/INTERVIEW`.<br>
   Expected: `Cannot set a pending status for Alice Test because role QA Engineer is closed.`
   Each of the other pending statuses is rejected too. Final-to-final corrections still succeed,
   provided they do not create a second hire; the role remains closed.

1. Close the empty analyst role using `list role` then `role close 2`. Try
   `add n/Cara Test p/91234563 e/cara@example.com a/3 Test Road r/QA Analyst`.<br>
   Expected: `The role “QA Analyst” is closed, so no new candidates can be added to it.`
   Cara is not added. Run `role close 2` again; it reports `QA Analyst is already closed.`

1. Run `status 1 st/HIRED`, then
   `add n/Cara Test p/91234563 e/cara@example.com a/3 Test Road r/QA Analyst`, and `status 3 st/HIRED`.<br>
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
   No candidate or role changes. Run `list`, `status 1 st/HIRED`, `status 2 st/REJECTED`, `list role`,
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

#### Undo and redo for role closure and deletion

1. With the fixture restored, run `status 1 2 st/REJECTED`, `list role`, `role close 1`,
   `undo`, then `list role`.<br>
   Expected: `Undid the previous change.` The engineer role is `OPEN` again. Both candidates
   retain their details and `REJECTED` status. Run `redo`, then `list role`; the role is `CLOSED`
   again and neither candidate changes. The saved file follows each successful undo or redo.

1. With the fixture restored, run `list role`, `role delete 2`, `undo`, then `list role`.<br>
   Expected: The analyst role returns at index 2 with experience `Entry level` and status `OPEN`.
   Both engineer candidates are unchanged. Run `redo`, then `list role`; only the engineer role
   remains. Repeat after closing the empty analyst role; undoing deletion restores it as `CLOSED`.

1. With the fixture restored, run `list role`, `role close 2`, `role edit 1 x/Senior`,
   `undo`, then `list role`.<br>
   Expected: The engineer's experience returns to `2 years`, but the analyst role remains `CLOSED`.
   Run `undo` and `list role` again; the analyst role is now `OPEN`. Undo reverses the latest
   change, not an arbitrarily selected role change. Read-only listing does not consume history.
   Run `role add r/QA Designer x/Senior`, then `redo`.<br>
   Expected: `There is no change to redo.` The new change has cleared redo history.

1. With the fixture restored, close the empty analyst role, then restart the app and run `undo`.<br>
   Expected: `There is no change to undo.` Run `list role`; the analyst role is still `CLOSED`.
   Repeat with deletion instead of closure: the role remains absent after restart. Saved changes
   survive restart, but undo and redo history do not.

### Adding a candidate

1. Adding a candidate to an existing role

   1. Prerequisites: Start with the sample data, which includes the roles `Software Engineer`, `Data Analyst` and `Product Manager`.

   1. Test case: `add n/Jane Tan p/91234567 e/jane@example.com a/Clementi Ave 2 r/Data Analyst`<br>
      Expected: Jane Tan is added to the end of the candidate list. The new card shows `Applying for: Data Analyst` and the status `Resume`.

   1. Test case: `add n/Ben Lim p/92345678 e/ben@example.com a/Jurong West r/product manager`<br>
      Expected: Ben Lim is added. The new card shows the role with the role's own spelling, `Applying for: Product Manager`.

   1. Test case: `add n/Carl Ng p/93456789 e/carl@example.com a/Bedok r/Designer`<br>
      Expected: No candidate is added. The error message says that no role named `Designer` exists and that the role must be added first.

   1. Test case: `add n/Carl Ng p/93456789 e/carl@example.com a/Bedok`<br>
      Expected: No candidate is added. The error message shows the correct `add` format, which includes `r/ROLE`.

   1. Test case: `add n/Alex Yeoh p/94567890 e/alex2@example.com a/Bedok r/Data Analyst`<br>
      Expected: A second Alex Yeoh is added, because candidates may share a name if their emails and phone numbers differ.

1. Adding a candidate with a duplicate or unusual phone number

   1. Prerequisites: Start with the sample data. Alex Yeoh's phone number is `87438807`.

   1. Test case: `add n/Kim Lee p/8743 8807 e/kim@example.com a/Bedok r/Data Analyst`<br>
      Expected: No candidate is added. The error message says the phone number is already used by Alex Yeoh.

   1. Test case: `add n/Kim Lee p/+65 8743 8807 e/kim@example.com a/Bedok r/Data Analyst`<br>
      Expected: Kim Lee is added with `+6587438807`. The result has a note that the number may belong to `Alex Yeoh (87438807)`.

   1. Test case: `add n/Tom Ong p/12345678 e/tom@example.com a/Bedok r/Data Analyst`<br>
      Expected: Tom Ong is added. The result has a note that the number does not look like a Singapore number.

### Editing a candidate

1. Editing a candidate's details

   1. Prerequisites: Start with the sample data and run `list`. The 1st candidate, Alex Yeoh, has a role, a status, a note and an interview.

   1. Test case: `edit 1 p/91112222`<br>
      Expected: Alex Yeoh's phone number changes. The role, status, note and interview are unchanged.

   1. Test case: `edit 1 r/  data analyst  `<br>
      Expected: The card shows `Data Analyst`, preserving the status and note. The success message
      names Alex Yeoh. Restart and confirm the role remains Data Analyst.

   1. Test case: `edit 1 n/Alex Tan r/Software Engineer`<br>
      Expected: Both fields change; the success message names Alex Tan, not Alex Yeoh.

1. Role-transfer safeguards

   1. Prerequisites: Use the disposable role-management fixture above with Alice and Ben for QA
      Engineer and an empty, open QA Analyst role. Restore the fixture between independent checks.

   1. Run `status 1 st/HIRED`, `status 2 st/REJECTED`, then `list role` and `role close 1`. Run `list`, then
      `edit 1 r/QA Analyst`.<br>
      Expected: Alice transfers from the closed engineer role to the open analyst role and stays hired.
      The engineer role stays closed. Ben remains rejected and associated with the engineer role.

   1. From the same closed-source setup, run `edit 1 n/Alice Corrected r/qa engineer` before transferring.<br>
      Expected: The same-role edit succeeds and uses `QA Engineer` spelling without reopening it.
      Selecting a different closed role instead is rejected with
      `The role “ROLE_TITLE” is closed, so candidates cannot be moved to it.`

   1. From the closed-source setup before transferring Alice, give QA Analyst another candidate with
      status `HIRED`. Alice must still be hired for QA Engineer. Filter to Alice using `find Alice`
      and try `edit 1 r/QA Analyst`.<br>
      Expected: The hidden hire still blocks Alice's transfer. Records, filters and saved bytes stay unchanged.
      A non-hired candidate may transfer to the same open role; their status is preserved.

   1. Try `edit 1 r/Unknown Role`, `edit 1 r/`, and `edit 1 r/QA Analyst r/QA Engineer`.<br>
      Expected: The unknown role reports
      `No role named “Unknown Role” exists. Add the role first, then edit the candidate.`
      Empty and repeated role fields are also rejected. No partial edits are applied.

### Recording a note

1. Setting and clearing a candidate's note

   1. Prerequisites: Start with the sample data and run `list`.

   1. Test case: `note 2 Prefers hybrid work`<br>
      Expected: The 2nd candidate's card shows the note `Prefers hybrid work`.

   1. Test case: `note 2 Discuss t/java experience`<br>
      Expected: The note is replaced with `Discuss t/java experience`. No `java` tag is added.

   1. Test case: `note 2`<br>
      Expected: The 2nd candidate's note is cleared and no longer shown on the card.

   1. Test case: `note 0 Prefers hybrid work`<br>
      Expected: No note is changed. The error message shows the correct `note` format.

   1. Other incorrect note commands to try: `note`, `note x Prefers hybrid work` (where x is larger than the list size)<br>
      Expected: No note is changed and an error message is shown.

### Scheduling an interview

1. Scheduling, rescheduling and clearing an interview

   1. Prerequisites: Start with the sample data and run `list`. The 1st candidate, Alex Yeoh, has an interview on `2026-10-20 14:00`. The 2nd candidate, Bernice Yu, has no interview, and their card has no interview line.

   1. Test case: `interview 2 d/2026-10-21 09:30`<br>
      Expected: Bernice Yu's card shows `Interview: 2026-10-21 09:30`. The result message names Bernice Yu, and the note is unchanged.

   1. Test case: `interview 2 d/2026-10-22 15:00`<br>
      Expected: The interview is replaced with `2026-10-22 15:00`.

   1. Test case: `interview 2`<br>
      Expected: Bernice Yu's interview is cleared, and the card no longer has an interview line.

   1. Test case: `interview 2` again<br>
      Expected: The command succeeds and nothing changes.

   1. Test case: `interview 1 d/2020-01-15 09:30`<br>
      Expected: The past date is accepted and shown on Alex Yeoh's card.

1. Rejecting invalid dates and times

   1. Prerequisites: Start with the sample data and run `list`.

   1. Test case: `interview 1 d/2026-10-20 9:00`<br>
      Expected: No interview is changed. The error message shows the required format `YYYY-MM-DD HH:MM`.

   1. Test case: `interview 1 d/2026-02-30 14:00`<br>
      Expected: No interview is changed. The error message says the date and time must be a real date and a valid 24-hour time.

   1. Other invalid dates and times to try: `2026-02-29 14:00` (not a leap year), `2026-10-20 24:00`, `20/10/2026 14:00`<br>
      Expected: No interview is changed and an error message is shown.

   1. Test case: `interview 1 d/`<br>
      Expected: Alex Yeoh's interview is not cleared. The error message shows the required format.

   1. Other incorrect interview commands to try: `interview`, `interview 0 d/2026-10-20 14:00`, `interview x d/2026-10-20 14:00` (where x is larger than the list size), `interview 1 d/2026-10-20 14:00 d/2026-10-21 14:00`<br>
      Expected: No interview is changed and an error message is shown.

1. Keeping the filter

   1. Prerequisites: Start with the sample data and run `find Bernice`, so only Bernice Yu is shown.

   1. Test case: `interview 1 d/2026-10-21 09:30`<br>
      Expected: Bernice Yu's interview is set, and the list still shows only Bernice Yu.

1. Saving interviews

   1. Prerequisites: Schedule an interview, e.g. `interview 2 d/2026-10-21 09:30`.

   1. Close and relaunch the app.<br>
      Expected: The interview is still shown on Bernice Yu's card.

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
   Expected: A `Recruitment data backed up` warning identifies the full backup and replacement paths
   and restoration steps. The original is renamed to `addressbook.json.bak` with identical bytes.
   Dismiss the warning: the command window opens with empty lists and a valid empty data file.
   Exit without a command and restart: the lists stay empty without another warning. Restore your
   fixture while the app is closed. Repeat separately
   with two valid recruitment JSON objects joined together (both directly and separated by whitespace),
   a valid object followed by `null`, `[]`, or arbitrary text, JSON `null`, null role or candidate records,
   a duplicated role title,
   a role status of `open` instead of `OPEN`, and a candidate whose `role` names a nonexistent role.<br>
   Expected: Each invalid file is backed up before starting empty; valid records are not imported
   separately. Earlier backups remain unchanged. Repeated failures use a timestamped backup name,
   then a numbered suffix if needed. Run `role add r/Recovery Test x/Entry level`, exit and restart:
   the new role persists and the backup stays unchanged. Restore your fixture between attempts.

1. Unreadable path: move the fixture file to your backup location, then create a directory named
   `data/addressbook.json` containing a small marker file. Restart.<br>
   Expected: The startup error identifies the data path and the app closes after dismissal. The
   directory and marker file are unchanged. Remove only the disposable marker and directory, then
   restore the fixture file. With a regular file without read permission, recovery can still succeed
   if the parent folder allows renaming and creating files. Otherwise, the app stops with a specific
   error and the original remains untouched. Test permission failures under an account that cannot
   bypass the restrictions.

1. Backup collision: while the app is closed, retain a marker file at `addressbook.json.bak`, then
   make `addressbook.json` invalid and restart.<br>
   Expected: The marker backup is unchanged; the notice identifies a different timestamped backup
   containing the invalid file's original bytes. The application opens with empty lists.

1. Valid empty file: save a copy of the fixture, replace its contents with
   `{"persons":[],"roles":[]}`, then restart.<br>
   Expected: The normal command window opens with empty lists and no startup error. Close the window
   and restore the fixture.

1. Default candidate status: remove only Alice's `status` field from a valid fixture file, then restart.<br>
   Expected: Alice loads with `Resume`; `list role` then `role close 1` reports two
   pending candidates. Removing a role's `status` or a candidate's `role` field instead prevents loading.

1. Legacy inconsistency guard: in a fixture backup, set both candidate statuses to `HIRED` and restart.<br>
   Expected: The records load, but `list role` then `role close 1` reports
   `Cannot close role: QA Engineer. More than one candidate is marked HIRED.` The role stays open.
   Use a separate fixture copy for this check; do not create inconsistent real recruitment data.

Replacement-write failures are exercised deterministically by `MainAppTest` using a failing recovery
operation. They must block commands and identify the backup containing the original bytes. This
avoids relying on a manual timing race between backup and replacement creation.
