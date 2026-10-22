---
  layout: default.md
  title: ""
---

# RecruiterBuddy

[![CI Status](https://github.com/AY2627S1-CS2103-F12-3/tp/actions/workflows/gradle.yml/badge.svg)](https://github.com/AY2627S1-CS2103-F12-3/tp/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/AY2627S1-CS2103-F12-3/tp/branch/master/graph/badge.svg)](https://codecov.io/gh/AY2627S1-CS2103-F12-3/tp)

![Ui](images/Ui.png)

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
* **Command guidance:** Open a help window that links to the User Guide, which covers the purpose, format, and examples of every command.
* **Data persistence:** Save changes automatically and restore saved candidate and role data when RecruiterBuddy starts.

RecruiterBuddy focuses on tracking candidates and role openings within a department. It does not manage internal HR matters such as payroll, leave, or employee benefits. It also does not communicate with candidates or hiring managers, process resumes, or automatically assess whether candidates are suitable for a role.

* If you are interested in using RecruiterBuddy, head over to the [_Quick Start_ section of the **User Guide**](UserGuide.html#quick-start).
* If you are interested in developing RecruiterBuddy, the [**Developer Guide**](DeveloperGuide.html) is a good place to start.

## Acknowledgements

* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), and [JUnit5](https://github.com/junit-team/junit5).
