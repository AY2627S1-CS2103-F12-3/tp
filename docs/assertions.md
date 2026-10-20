---
  layout: default.md
  title: "Assertions and logging"
  pageNav: 3
---

# Assertions and logging

The application uses Java assertions for internal invariants that should always
hold when the code is correct, such as non-empty parsed keywords, non-null
collections, and ensuring that every displayed candidate satisfies the active
filter. Assertions are not a substitute for user-input validation; parsers still
return normal command errors for invalid input.

Use `-ea` when running the application or tests to enable assertions. Logging
records useful operational events, such as parsing a `find` command and applying
its filter. Logs should describe state transitions without exposing sensitive
candidate information.
