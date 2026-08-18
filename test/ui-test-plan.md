# Nova UI Test Plan

Run cases in order with Java 25. Each case starts Nova in a fresh process.

## TC1: Add and list all task types

Aim: Verify todos, deadlines, and events retain string timing data and display polymorphically.

### Input

```text
todo borrow book
deadline do homework /by no idea :-p
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
  [D][ ] do homework (by: no idea :-p)
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
 2.[D][ ] do homework (by: no idea :-p)
 3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## TC2: Handle invalid commands with NovaException

Aim: Verify that invalid todo, deadline, event, mark, and unknown commands produce specific errors without ending the chatbot session.

### Input

```text
todo
blah
deadline submit report
event meeting /from Mon 2pm
mark one
mark 1
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
 OOPS!!! A deadline must follow: deadline DESCRIPTION /by DATE_OR_TIME
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
 Bye. Hope to see you again soon!
____________________________________________________________
```
