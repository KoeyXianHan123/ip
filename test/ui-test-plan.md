# Nova UI Test Plan

Run cases in order with Java 25. Each case starts Nova in a fresh process and temporary working directory.

## TC1: Add and list all task types

Aim: Verify todos, dated deadlines, and events display polymorphically, with deadlines reformatted from yyyy-MM-dd.

### Input

```text
todo borrow book
deadline do homework /by 2019-10-15
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
  [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
  [D][ ] do homework (by: Oct 15 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] borrow book
 2.[D][ ] do homework (by: Oct 15 2019)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC2: Delete tasks and reject invalid task numbers

Aim: Verify deletion removes the selected task and renumbers the remaining list, while malformed and out-of-range delete commands report errors without changing the list.

### Input

```text
todo read book
deadline return book /by 2019-06-06
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
todo borrow book
delete 3
delete one
delete 9
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
  [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
  [D][ ] return book (by: Jun 6 2019)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
  [T][ ] join sports club
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
  [T][ ] borrow book
 Now you have 5 tasks in the list.
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a task number, for example: delete 1
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 9 does not exist in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] read book
 2.[D][ ] return book (by: Jun 6 2019)
 3.[T][ ] join sports club
 4.[T][ ] borrow book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC3: Handle invalid commands with NovaException

Aim: Verify invalid commands and malformed or impossible deadline dates produce specific errors without ending the chatbot session or adding a task.

### Input

```text
todo
blah
deadline submit report
deadline submit report /by 2019-02-30
deadline submit report /by June 6th
event meeting /from Mon 2pm
mark one
mark 1
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 OOPS!!! A deadline must follow: deadline DESCRIPTION /by yyyy-MM-dd
____________________________________________________________
____________________________________________________________
 OOPS!!! The deadline date must be a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
 OOPS!!! The deadline date must be a valid date in yyyy-MM-dd format.
____________________________________________________________
____________________________________________________________
 OOPS!!! An event must follow: event DESCRIPTION /from START /to END
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a task number, for example: mark 1
____________________________________________________________
____________________________________________________________
 OOPS!!! Task 1 does not exist in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC4: Save after completion-state changes

Aim: Verify successful mark and unmark commands remain usable when each task-list change is saved to disk.

### Input

```text
todo write tests
mark 1
unmark 1
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
  [T][ ] write tests
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
  [T][X] write tests
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
  [T][ ] write tests
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1.[T][ ] write tests
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC5: Load saved tasks and skip corrupted records

Aim: Verify startup restores encoded and legacy tasks, including delimiter and Unicode text, while safely ignoring malformed records.

### Initial data file

```text
V2 | T | 1 | cmVhZCB8IGNhZsOp
V2 | D | 0 | cmV0dXJuIGJvb2s= | MjAxOS0wNi0wNg==
V2 | E | 0 | cHJvamVjdCBtZWV0aW5n | QXVnIDZ0aCAycG0= | NHBt
T | 0 | legacy task
V2 | T | 2 | aW52YWxpZCBzdGF0dXM=
V2 | T | 0 | not_base64!
V2 | D | 0 | ZGVhZGxpbmU= |
V2 | D | 0 | aW52YWxpZCBkYXRl | MjAxOS0wMi0zMA==
V2 | X | 0 | dW5rbm93biB0eXBl
corrupted task data
```

### Input

```text
list
bye
```

### Expected output

```text
 _   _                  
| \ | | _____   ____ _ 
|  \| |/ _ \ \ / / _` |
| |\  | (_) \ V / (_| |
|_| \_|\___/ \_/ \__,_|

Hello! I'm Nova.
What can I do for you?
____________________________________________________________
 OOPS!!! I skipped 6 corrupted task record(s) in the data file.
____________________________________________________________
 Here are the tasks in your list:
 1.[T][X] read | café
 2.[D][ ] return book (by: Jun 6 2019)
 3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
 4.[T][ ] legacy task
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
