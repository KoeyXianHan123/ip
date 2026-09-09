# Nova User Guide

Nova is a task-tracking chatbot available through a console and a JavaFX graphical interface. It stores tasks
between sessions and supports todos, deadlines, and events.

Commands are case-sensitive. Enter each command exactly as shown, replacing words in uppercase with your own
values.

## Viewing all tasks

Format: `list`

Displays every task with its task number and completion status.

## Adding a todo

Format: `todo DESCRIPTION`

Example: `todo borrow book`

Adds a task without a date or time.

## Adding a deadline

Format: `deadline DESCRIPTION /by yyyy-MM-dd`

Example: `deadline submit report /by 2026-09-30`

Adds a task due on the specified date.

## Adding an event

Format: `event DESCRIPTION /from START /to END`

Example: `event project meeting /from Monday 2pm /to 4pm`

Adds an event with the specified start and end information.

## Marking a task

Format: `mark TASK_NUMBER`

Example: `mark 2`

Marks the numbered task as completed.

## Unmarking a task

Format: `unmark TASK_NUMBER`

Example: `unmark 2`

Marks the numbered task as incomplete.

## Deleting a task

Format: `delete TASK_NUMBER`

Example: `delete 2`

Deletes the numbered task. Task numbers may change after deletion; use `list` to view the updated numbers.

## Finding tasks by keyword

Format: `find KEYWORD`

Example: `find book`

Displays tasks whose descriptions contain the case-sensitive keyword. Results retain their original task numbers.

## Finding deadlines by date

Format: `on yyyy-MM-dd`

Example: `on 2026-09-30`

Displays deadlines due on the specified date while retaining their original task numbers.

## Viewing help

Format: `help`

Displays all available commands and their syntax:

```text
 Here are Nova's commands:
 list
 todo DESCRIPTION
 deadline DESCRIPTION /by yyyy-MM-dd
 event DESCRIPTION /from START /to END
 mark TASK_NUMBER
 unmark TASK_NUMBER
 delete TASK_NUMBER
 find KEYWORD
 on yyyy-MM-dd
 help
 bye
```

## Exiting Nova

Format: `bye`

Displays Nova's farewell message and ends the current session.
