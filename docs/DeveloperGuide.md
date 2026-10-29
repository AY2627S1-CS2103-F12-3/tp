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
* Kavin Parthipan (`kpkavin04`) used OpenAI Codex to assist with requirements analysis, implementation, tests, review, and documentation for candidate role reassignment, the persistent role model, and related onboarding features.

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

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) and [**`Commons`**](#common-classes) are the core startup and shared utilities.

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

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete implementation where possible.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml).

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

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`).
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser then parses the user command and returns a concrete `Command` object.
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

Address values are normalized at the model boundary: leading and trailing Unicode whitespace
is removed, consecutive whitespace is collapsed to one space, and an address containing only
Unicode whitespace (including U+2002 en spaces) is rejected.

**API** : [`Model.java`](https://github.com/AY2627S1-CS2103-F12-3/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="600" alt="Partial model structure showing candidates and roles" />

The partial diagram focuses on role associations. Filtered-view associations and candidate fields are
omitted for clarity; the fields of a candidate are shown in the diagram below.

<puml src="diagrams/CandidateClassDiagram.puml" width="700" alt="Fields of a candidate (Person)" />

A candidate (`Person`) is immutable and always has exactly one of each field except tags:

* Two candidates are the same person if they share an email (ignoring case) or a phone number. Names
  are not compared, as different candidates can share a name.
* The candidate's role is a `RoleTitle`, the same class that names a `Role`.
* An empty `Note` means the candidate has no note. New candidates share the empty `Note.NONE`, so the
  association to `Note` is not a composition.
* `Interview.NONE` means the candidate has no interview, and is the only `Interview` without a date
  and time. It is shared by every such candidate, so the association to `Interview` is not a
  composition. See [Candidate interviews](#candidate-interviews).
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

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` manage the set of tags without scattering tag data across all candidates.

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

### Offline command guide

`HelpCommandParser` accepts zero or one command name. More than one token is rejected with the
dedicated extra-argument message. `AddressBookParser` recognizes the help keyword without case
sensitivity, without changing dispatch for other commands.

`HelpCommand` returns a non-undoable `CommandResult` containing `CommandGuide#getOverview()` when
no topic is supplied, or `CommandGuide#findHelp()` for a single topic. Lookup uses `Locale.ROOT` so
case-insensitive matching is independent of the machine's language settings. Unknown topics produce
a `CommandException` naming the original input; neither failure path changes the model.
`CommandGuide` keeps instructions in a stable command order and reuses existing `MESSAGE_USAGE`
definitions where available. This avoids a second source of truth for command parameters and examples.

`MainWindow` displays help in the existing `ResultDisplay`, with additional height, wrapped text and
vertical scrolling. The Help menu and F1 use the same overview without opening a separate window.
Both recruitment lists remain visible; no filter or recruitment record changes.

`LogicManager` skips the normal save specifically for `HelpCommand`. Reading instructions therefore
works even when saving recruitment data would fail, and does not create an undo snapshot or discard redo
history. Other commands retain their existing saving behavior. Help is bundled with the JAR and needs
no network connection or additional files.

### Candidate Statuses

#### Representation and Persistence

The 'Status' enum defines seven candidate statuses: `RESUME`, `INTERVIEW`, `OFFER`, `DECISION`, `HIRED`, `REJECTED`, and `WITHDRAWN`. The first four are _pending_ statuses; the remaining three are final statuses.

When stored candidate data has no status, loading it defaults to `RESUME`. Upon successful execution
of any command except `HelpCommand`, `LogicManager` saves the complete address book, and
`JsonAdaptedPerson` writes each status using its exact enum name. Viewing help does not trigger a save.

An unrecognized stored value causes data loading to fail and invokes the recovery process described in [Protecting unreadable recruitment data](#protecting-unreadable-recruitment-data).

#### Batch Status Updates

Batch status updates of candidate statuses is handled by the `status` command.

`StatusCommandParser` requires exactly one `st/` prefix. It splits the preamble on whitespace, parses each index using `ParserUtil#parseIndex`, and stores the results in a set, removing duplicates.

`StatusCommand` resolves every index against the _currently displayed_ candidate list, and creates replacement `Person` objects with the requested status, updates the model, resets the candidate filter, and warns if the role or hire constraints fail.

During running of `StatusCommandParser`, `ParseException` will be thrown on any of these conditions:
* No `st/` prefix found.
* Multiple `st/` prefixes found.
* One of the indices is not a nonzero unsigned integer.
* Normalized status does not match one of the `Status` values.

During running of `StatusCommand`, `CommandException` will be thrown on any of these conditions:
* Index falls outside filtered candidate list.
* A pending status (`RESUME`, `INTERVIEW`, `OFFER` or `DECISION`) is being assigned to a candidate whose role is closed. (This is to ensure that closed role only ever contains candidates with a final status.)
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
`Added open role: TITLE.`; the roles list displays the updated roles and the candidates list retains its filter, if any.
Parsing or duplicate rejection leaves the stored records unchanged.

#### Correcting a role's title or required experience

`RoleCommandParser` delegates `edit` to `EditRoleCommandParser`. The parser reads the displayed-role index with `ParserUtil#parseRoleIndex`, rejects repeated `r/` or `x/` prefixes using the existing validation, and validates the title/experience values through `ParserUtil`.

`EditRoleCommand` resolves the index against `Model#getFilteredRoleList`. It constructs the complete replacement role, preserving its status and omitted fields, then checks the full role collection for duplicates before making the change.

`Model#setRole` delegates to `AddressBook#setRole`. The model validates the role replacement through `UniqueRoleList` before changing either collection. If the title's exact spelling changes, every associated candidate is rewritten to the canonical stored title.

The result shows `Edited role: TITLE.` with the updated title. `EditRoleCommand` logs accepted corrections and rejected duplicate titles; `LogicManager` saves both collections through the existing storage flow.

The sequence below starts after parser validation. Nested alternatives reject an invalid index or a duplicate title before either list changes. Only the valid branch calls
`setRole`, which replaces the role before propagating its new title across the full candidate
list. Candidate copying is shown as a single action to omit unrelated field-construction details.
Saving occurs in `LogicManager` after the command returns successfully, as described below.

<puml src="diagrams/EditRoleSequenceDiagram.puml" width="900"
  alt="Role-edit validation, duplicate-title rejection, role replacement and title propagation to all associated candidates"></puml>

#### Saving and restoring associations

After a command other than `HelpCommand` executes successfully, `LogicManager#execute` asks `Storage` to save the complete
address book, including both collections. This also happens for successful read-only commands such as `list`.
`HelpCommand` returns before the save call, so viewing help neither
creates the recruitment data file nor retries a failed save. After resolving a file-access problem,
the user can run `list` successfully to retry saving.

When running AddressBook from a JAR file, `data/addressbook.json`, `preferences.json`, and the log files
are saved in the directory containing the JAR. When running from compiled classes, they are saved in
the process's working directory.

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

Recovery diagnostics follow the same lifecycle: `MainApp` emits a loading `WARNING`, `RecruitmentDataRecovery` records each completed backup and replacement at `INFO`, and unsafe recovery ends with a fatal startup error.

### Role lifecycle and candidate safeguards

#### Closing recruitment

`RoleCommandParser` delegates `close` to `CloseRoleCommandParser`. Close and delete share
`ParserUtil#parseRoleIndex` to reject missing, invalid, or extra index arguments consistently.
`CloseRoleCommand` resolves the index against `Model#getFilteredRoleList` and rejects an already closed role.

Candidate checks use the complete `ReadOnlyAddressBook#getPersonList`, not the filtered candidate
list, matching role titles without case sensitivity. `Status#isPending` classifies statuses `RESUME`, `INTERVIEW`, `OFFER` and `DECISION` as pending. Closure rejects pending candidates with their
count, then rejects more than one hired candidate. No candidates, or no hired candidate, is allowed.
After validation, `Model#setRole` replaces the immutable role with a `CLOSED` copy. Candidate records
and statuses remain unchanged. The observable role list refreshes, and `LogicManager` saves the change. `AddCommand` rejects new candidates for closed roles.

#### Deleting an unused role

`RoleCommandParser` delegates `delete` to `DeleteRoleCommandParser`. `DeleteRoleCommand` applies the
same index guards, then counts associated candidates across all stored records.
Any associated candidate blocks deletion, irrespective of status or visibility; the error includes
their count. An empty role may be deleted whether `OPEN` or `CLOSED`.

Deletion goes through `Model#deleteRole`, `AddressBook#removeRole`, and `UniqueRoleList#remove`.
The role list refreshes and its indices update.
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

Before assigning `HIRED`, `StatusCommand` checks amongst candidates to be assigned the `HIRED` status, as well as all stored candidates with the `HIRED` status, for another candidate with a case-insensitive match on the same role title.

Before assigning a pending status (`RESUME`, `INTERVIEW`, `OFFER` or `DECISION`), the command looks up the candidate's role and rejects the change if the role is closed. Final-to-pending corrections are allowed only when the role remains open.

#### Keeping displayed indices aligned with the UI

Both candidate and role lists remain visible at all times. Candidate commands resolve indices against `Model#getFilteredPersonList`, while role commands use `Model#getFilteredRoleList`. Each list keeps its own filtered view, so role actions do not accidentally modify the candidate view, and vice versa.

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
their roles, including when scrolling. Listeners are not
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

After validation succeeds, `Model#setPerson` updates the candidate collection and the
candidate filter resets to show all records. The existing `Edited candidate: ...` result formats the
updated candidate, including the new name and canonical role. Normal `LogicManager` saving persists
the association without a JSON schema change. Validation failures leave records and filters
unchanged and do not save. Neither role's status changes automatically.

### Role workflow diagnostics and internal checks

Role commands and candidate-role transfers reuse `LogsCenter`, with `INFO` for successful in-memory changes and `FINE` for expected recruitment-rule rejections. Closure and deletion refusals include counts and names in the user-facing message rather than only a generic error.

Assertions check programmer assumptions rather than user input. `CandidateRoleChecks#verifyHiringAllowed` requires a non-null internal status so a caller cannot accidentally bypass the single-hire rule.

### Undo/redo feature

Undo and redo are implemented in `ModelManager` using two in-memory stacks of complete
`AddressBook` snapshots: `undoStack` and `redoStack`. The history is session-scoped and
is not persisted in `addressbook.json`; restarting the application clears both stacks.

#### Implementation

`Command#isUndoable()` identifies commands that can change address-book data. In
`LogicManager#execute(String)`, an undoable command first calls
`Model#recordAddressBookState()`, which pushes a defensive copy onto the undo stack.
After successful execution, `Model#commitAddressBookState()` clears the redo stack. If
execution fails with a `CommandException` or runtime exception,
`Model#discardAddressBookState()` removes the snapshot so the failed command does not
consume undo history.

`UndoCommand` calls `Model#undoAddressBook()`. If a snapshot exists, the current state is
copied to the redo stack and the latest undo snapshot is restored. If none exists, the
address book is unchanged and the user sees `There is no change to undo.`

`RedoCommand` performs the inverse operation through `Model#redoAddressBook()`. If no
redo snapshot exists, the address book is unchanged and the user sees
`There is no change to redo.` A new successful undoable command after an undo clears redo
history. Successful undo and redo operations are saved through the normal storage step,
but the history itself remains in memory only. Read-only commands such as `list`, `find`
and `help` do not affect either stack.

The implementation stores complete address-book snapshots rather than requiring each
mutating command to implement its own inverse operation. This centralises the feature and
makes new mutating commands undoable through the `Command#isUndoable()` contract, at the
cost of higher memory usage for large address books.

#### Historical AB3 placeholder (obsolete)

The material below is retained only as historical reference and does not describe the
current implementation. Do not use its `VersionedAddressBook`, state-pointer, method,
message, example, or diagram descriptions when maintaining the feature.

#### Proposed Implementation (obsolete)

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`.

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book and the undo history to update.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which restores the previous state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check this before attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command checks this before attempting to redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the current one are discarded.

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

### Candidate email validation

`Email` checks the part before the `@` (the local part) and the part after it (the domain) separately. The local part follows the unquoted (dot-atom) form of [RFC 5322](https://www.rfc-editor.org/rfc/rfc5322.txt): no leading or trailing dot, no consecutive dots, and no spaces.

`Email#findValidationError` is the only validator. It runs the checks in a fixed order (the `@`, then the part before it, then the domain) and returns a message naming the first problem found, supporting precise user feedback.

Two kinds of valid email are likely typos. `Email#getUncommonSymbols` returns the symbols before the `@` other than `. _ - + '`, which major email providers do not issue, such as `#` in `john#doe@example.com`.

#### Design considerations

**Aspect: Which local parts are accepted**

* **Alternative 1 (current choice):** The unquoted form of RFC 5322, with a warning for symbols other than `. _ - + '`.
  * Pros: Every address that an email provider issues is accepted, the rule comes from a published standard, and a typo such as `john#doe@example.com` is still flagged.
  * Cons: The user can ignore the warning and keep an address that is a typo.
* **Alternative 2:** Only letters, digits and `. _ - + '`, the symbols that major email providers issue.
  * Pros: A typo with another symbol can never be saved.
  * Cons: Rejects some valid addresses.
* **Alternative 3:** All of RFC 5322, including quoted local parts such as `"john doe"@example.com`.
  * Pros: Accepts every valid address.
  * Cons: A quoted local part can contain spaces and text such as ` a/`, which `ArgumentTokenizer` would read as the start of another field. Major email providers also do not issue such addresses.

**Aspect: How a domain without a dot is handled**

* **Alternative 1 (current choice):** Accept it and warn.
  * Pros: Catches a likely typo, such as `gmailcom` for `gmail.com`, without rejecting a valid address.
  * Cons: The user can ignore the warning and keep an address that is a typo.
* **Alternative 2:** Reject it.
  * Pros: A typo can never be saved.
  * Cons: Rejects valid addresses.

### Candidate phone numbers

`Phone` accepts 8 to 15 digits with an optional leading `+`, and removes spaces and hyphens before saving. Two kinds of phone problems are checked when a candidate is added or their phone number is edited.

* **Exact duplicates are rejected.** `Person#isSamePerson` compares phone numbers with `Phone#equals`, which compares the saved digits exactly. `UniquePersonList` and the data-file loader use the same exact duplicate rule.
* **Possible problems only add a warning to the success message.** `PhoneChecks#appendPhoneWarnings` runs after `AddCommand` or `EditCommand` has stored the candidate. `EditCommand` calls it only when a phone number changed.
  * `Phone#isPlausibleNumber` treats a number without a `+`, or with `+65`, as a Singapore number, which has 8 digits starting with 3, 6, 8 or 9. Numbers with another country code are not checked.
  * `Phone#isPossiblySameNumber` compares numbers after removing the `+` and, if `65` is followed by exactly 8 digits, the `65`. So `+6591234567`, `6591234567` and `91234567` may be the same number.

#### Design considerations

**Aspect: What to do with a number that may be another candidate's**

* **Alternative 1 (current choice):** Accept it and add a warning naming the candidates whose number may be the same.
  * Pros: A real but unusual number, such as an overseas number written without its `+`, is never blocked.
  * Cons: The user may miss the warning and keep a duplicate.
* **Alternative 2:** Reject it, like an exact duplicate.
  * Pros: Duplicates cannot be added by mistake.
  * Cons: Wrongly blocks different numbers that happen to match, such as an overseas number without its country code. The loader would also have to reject such pairs, so data files saved by older versions may no longer load.

The loader must keep using only the exact rule. Otherwise, a pair of candidates accepted with a warning could be saved in a file that then fails to load.

### Candidate notes

Each `Person` has a `Note`, a free-text field for details such as a candidate's skills or availability. Interview dates and times have their own field, described in [Candidate interviews](#candidate-interviews).

#### Recording a note

`NoteCommandParser` splits the arguments into the first word, which is parsed as the index, and the rest, which is trimmed and becomes the note. It does not use `ArgumentTokenizer`, so the note can include prefixes such as `t/` or `n/` without being truncated.

`NoteCommand#execute` looks up the candidate in the displayed candidate list and replaces it with a copy that has the new note. It does not reset the candidate filter, so a recruiter can add notes while viewing a filtered list.

Notes are stored as the `note` field of each candidate in the JSON data file. A candidate saved without a `note` field loads with an empty note.

#### Design considerations

**Aspect: How the note is entered**

* **Alternative 1 (current choice):** A separate `note INDEX [NOTE]` command that takes everything after the index.
  * Pros: Any text can be stored, including text that looks like a prefix.
  * Cons: A note cannot be given when the candidate is first added, so it takes a second command.
* **Alternative 2:** An `nt/NOTE` prefix on `add` and `edit`.
  * Pros: Consistent with the other candidate fields.
  * Cons: The tokenizer ends the note at the next prefix. For example, `nt/Discuss t/java` would save the note `Discuss` and add a `java` tag, so valid notes are silently changed. This alternative is therefore rejected.

**Aspect: Replacing or adding to a note**

* **Alternative 1 (current choice):** `note INDEX NOTE` replaces the whole note, and `note INDEX` clears it.
  * Pros: One simple command covers setting, correcting and clearing a note. The result message says when a note was replaced or cleared, so a note is never overwritten silently, and `undo` restores the previous note if needed.
  * Cons: To add to a note, the user types the whole note again.
* **Alternative 2:** A way to add text to the end of an existing note.
  * Pros: Saves retyping when a recruiter adds details after each recruitment stage.
  * Cons: It needs its own command word. A marker inside `note`, such as a leading `+`, would clash with notes that start with that text, such as `+65 9123 4567 (home)`. It is planned for a future extension.

### Candidate interviews

Each `Person` has an `Interview`, which holds the date and time of the candidate's interview, to the minute. A candidate with no interview has `Interview.NONE`, so no code needs to handle a `null` interview.

#### Validating a date and time

`Interview` accepts only zero-padded `YYYY-MM-DD HH:MM` values that are real dates and valid 24-hour times. It checks a value in two steps, so that each kind of mistake gets its own error message.

1. `Interview#isValidFormat` checks the shape with a regular expression. A value such as `2026-10-20 9:00` fails here, and the user is told the expected format.
2. `Interview#isValidDateTime` parses the value with the pattern `uuuu-MM-dd HH:mm` and `ResolverStyle.STRICT`. A value with the right shape but an impossible date or time, such as `2026-02-30` or `2026-10-20 24:00`, fails here.

Strict resolving is needed because the default resolver silently adjusts `2026-02-30` to `2026-02-28`. The pattern uses `uuuu` (year) instead of `yyyy` (year of era), because strict resolving rejects year values earlier than 1 and improves input validation.

#### Scheduling or clearing an interview

`InterviewCommandParser` tokenizes the arguments with the `d/` prefix. The preamble must be exactly one index. A missing index, an index joined to `d/` without a space, an index that is not a positive integer, or an extra field with the same prefix, rejects the command before execution.

The sequence diagram below shows how `InterviewCommandParser` parses `interview 1 d/2026-10-20 14:00`, and the branch that clears the interview when `d/` is left out. The steps before and after parsing are omitted for brevity.

<puml src="diagrams/InterviewSequenceDiagram.puml" alt="Parsing the interview 1 d/2026-10-20 14:00 command" />

`InterviewCommand#execute` looks up the candidate in the displayed candidate list and replaces it with `Person#withInterview`, a copy that has the new interview. A candidate has only one interview field, so all other fields remain the same.

After saving a new interview, `InterviewCommand` checks whether it might be a mistake and adds a line to the result message for each of these cases, in this order:

* The candidate's status is final (`HIRED`, `REJECTED` or `WITHDRAWN`), using `Status#isPending`, and the interview is today or later. This line comes first, as it suggests the interview may not be needed.
* The date is before today, or more than a year after today. The interview is still saved so that historical interviews can be recorded, but the warning helps catch a mistyped year. Today's date is taken from the system clock at the moment of validation.
* Other candidates in the whole address book, including those hidden by the filter, have an interview at the same date and time. The line `Also at this time:` shows at most two names, shortens names as needed, and keeps the full set in the log.

Clearing an interview is never checked.

Other commands that rebuild a `Person`, such as `edit`, `note`, and `status`, copy the existing interview across, so they never change it.

#### Saving and loading interviews

Interviews are stored as the `interview` field of each candidate in the JSON data file, in the same `YYYY-MM-DD HH:MM` form. `Interview.NONE` is saved as an empty string. When loading, a missing,
empty, or invalid interview value defaults to `Interview.NONE` and still preserves other candidate fields.

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

**Aspect: How unusual interviews are handled**

* **Alternative 1 (current choice):** Save the interview and add a line to the result message.
  * Pros: Each case can be legitimate, such as a panel interviewing two candidates at once, or recording an interview for a candidate who was later rejected. The recruiter is not blocked, but still receives a warning.
  * Cons: A recruiter who does not read the result message can miss the warning.
* **Alternative 2:** Reject the interview.
  * Pros: A mistyped year cannot be saved.
  * Cons: Valid interviews would be refused, and the recruiter would need a way to override the check.

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

**Value proposition**: Help a department's recruiter keep track of candidates applying for their team's open roles, including candidate details and where each candidate stands in the interview process.

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
| `* * *` | department recruiter | record a free-text note about a candidate | jot down anything else worth remembering about them |
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
* 4b. The interview might be a mistake: it is before today or more than a year from today, the candidate already has a final outcome, or another candidate has an interview at the same date and time.
  * 4b1. RecruiterBuddy records the interview, and points out each possible mistake when confirming it.
  * Use case ends.

#### UC05 — Save and Load Data

**System:** RecruiterBuddy<br>
**Actor:** User

**Main success scenario:**

1. User launches RecruiterBuddy.
2. RecruiterBuddy loads the saved candidate and role data.
3. User interacts with the application and later saves updates automatically.

Use case ends.

**Extensions:**

* 2a. The data file is unreadable or invalid.
  * 2a1. RecruiterBuddy creates a backup, replaces the invalid file with a clean empty data set, and warns the user.
  * Use case ends.

### Conclusion

This Developer Guide documents the implemented recruitment workflow, role management, candidate tracking, and validation logic used by RecruiterBuddy.

