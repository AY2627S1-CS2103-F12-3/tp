---
  layout: default.md
  title: "Assertions and logging"
  pageNav: 3
---

# Assertions and logging

The application uses explicit defensive checks for internal invariants. For
example, command and predicate constructors reject null inputs, and predicates
reject null candidates. These checks protect API boundaries without treating
user input as trusted: parsers still perform normal validation and return
user-facing errors for invalid commands and statuses.

The earlier assertion work proposed in issue #110 is not being deferred into
hidden Java `assert` statements. The useful checks are implemented as
`requireNonNull` validations and are covered by tests; redundant checks for
non-empty parsed keywords and already-filtered lists were removed because the
parser and model filtering logic already enforce those conditions. No runtime
behaviour depends on enabling `-ea`.

Use `-ea` when running the application or tests to enable assertions. Logging
records useful operational events, such as parsing a `find` command and applying
its filter. Logs should describe state transitions without exposing sensitive
candidate information.
