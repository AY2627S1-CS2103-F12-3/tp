# RecruiterBuddy

[![CI Status](https://github.com/AY2627S1-CS2103-F12-3/tp/actions/workflows/gradle.yml/badge.svg)](https://github.com/AY2627S1-CS2103-F12-3/tp/actions/workflows/gradle.yml)

[![MarkBind Action](https://github.com/AY2627S1-CS2103-F12-3/tp/actions/workflows/docs.yml/badge.svg)](https://github.com/AY2627S1-CS2103-F12-3/tp/actions/workflows/docs.yml)

[![pages-build-deployment](https://github.com/AY2627S1-CS2103-F12-3/tp/actions/workflows/pages/pages-build-deployment/badge.svg)](https://github.com/AY2627S1-CS2103-F12-3/tp/actions/workflows/pages/pages-build-deployment)

![Ui](docs/images/Ui.png)

RecruiterBuddy is a desktop application for department recruiters who are responsible for filling a small number of vacancies within one department. It helps recruiters maintain candidate details, track the open role associated with each candidate, record upcoming recruiter interviews, and monitor where each candidate stands in the recruitment process.

RecruiterBuddy combines a graphical interface with a command-driven workflow. It is optimised for recruiters who type quickly and prefer using keyboard commands to navigating a primarily mouse-driven interface. This allows recruiters to retrieve and update recruitment records efficiently throughout their workday.

## MVP features

RecruiterBuddy supports the following features:

* **Candidate management:** Add, edit, list, find (by name), and delete candidates. Each candidate is associated with exactly one role and has a name, phone number, email address, and address. Candidates can also have tags and a free-text note, which can hold details such as their interview date and time.
* **Recruitment status tracking:** Assign each candidate exactly one status from `RESUME`, `INTERVIEW`, `OFFER`, `DECISION`, `HIRED`, `REJECTED`, or `WITHDRAWN`. A candidate's status can be updated as they progress through the recruitment pipeline. New candidates start as `RESUME`. Each role can have at most one `HIRED` candidate, and candidates of a closed role cannot return to a pending status.
* **Duplicate prevention:** Prevent a candidate from being added if another candidate has exactly the same name. Detecting candidates with duplicate email addresses or phone numbers is planned for a future version.
* **Interview tracking:** Record or reschedule a candidate's recruiter interview date and time as a free-text candidate note. Checking that interview dates and times are valid is planned for a future version.
* **Role management:** Add, list, edit, and delete role openings. Edit a role's title or required experience level; renaming also updates all associated candidates without changing their other details or the role's status. A role can be deleted only if it has no candidates. Each role represents exactly one vacancy and has either an `OPEN` or `CLOSED` status.
* **Role closure:** Close a role once every candidate for it has a final outcome (`HIRED`, `REJECTED`, or `WITHDRAWN`) and at most one is hired. The role's candidates are kept, and no new candidates can be added to it.
* **Command guidance:** Read an offline guide to every command's purpose, syntax and examples directly in the result area using `help`, the Help menu or F1.
* **Data persistence:** Save changes automatically and restore saved candidate and role data when RecruiterBuddy starts.

RecruiterBuddy focuses on tracking candidates and role openings within a department. It does not manage internal HR matters such as payroll, leave, or employee benefits. It also does not communicate with candidates or hiring managers, process resumes, or automatically assess whether candidates are suitable for a role.

For detailed documentation, refer to the [User Guide](docs/UserGuide.md) and [Developer Guide](docs/DeveloperGuide.md).

## Acknowledgements

* This project is based on the [AddressBook-Level3](https://se-education.org/addressbook-level3/) project created by the [SE-EDU initiative](https://se-education.org).
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), and [JUnit5](https://github.com/junit-team/junit5).
