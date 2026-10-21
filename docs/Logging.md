---
  layout: default.md
  title: "Logging guide"
---

# Logging guide

* The project uses the `java.util.logging` package for logging.
* `LogsCenter` manages logging levels and destinations.
* A class obtains a `Logger` with `LogsCenter.getLogger(Class)`, which logs messages at the configured level.
* Log messages are written to the console and a `.log` file.
* The output logging level can be controlled by changing the `LOG_LEVEL` constant in the `LogsCenter` class.
* **When choosing a level for a log message**, follow the conventions given in [_[se-edu/guides] Java: Logging conventions_](https://se-education.org/guides/conventions/java/logging.html).

## Role workflow diagnostics

Role addition, editing, closure and deletion record successful in-memory changes at `INFO`. Candidate edits record a role transfer only when the destination differs from the original role. Displayed indices identify the command's selected record at that moment; they are not permanent identifiers. These messages precede `LogicManager` saving, so they do not prove that the data reached disk.

Expected business-rule rejections use `FINE`: duplicate role titles, a closed transfer destination, candidates blocking closure or deletion, or another hired candidate occupying the destination. Counts are included where they help explain the refusal. The normal command error still reaches the user; it is not replaced with a log message or an assertion failure. To inspect `FINE` messages during development, lower `LogsCenter.LOG_LEVEL` and rebuild; the default remains `INFO`.

These diagnostics omit candidate names, contact details, notes and raw command text. This is not an application-wide privacy guarantee: the existing `LogicManager` command log and parser diagnostics can contain the original user input. Do not share real recruitment logs without reviewing and redacting them.
