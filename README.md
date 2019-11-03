# NoteBookService

A simple note manager service that can be run on a Tomcat to manage notes in a MySQL database.  
The service runns on a tomcat docker that is connected to a mysql docker.

Notes can be added, received, updated and removed using the REST interface with a JSON-RPC 2.0 communication standard.

# Build

The web-archive file can be build using maven: `mvn clean install compile`

# Methods

The methods that can be called using JSON-RPC 2.0 are:

* **create_note(Note)** - Receives a note and creates it in the database (returns the notes new id)
* **get_notes(NoteSelector)** - Returns all notes in the database that match the NoteSelector
* **update_note(Note)** - Updates the note (returns the number of affected rows)
* **delete_notes(NoteSelector)** - Deletes all notes that match the NoteSelector (returns the number of affected rows)

# Communication classes

The classes (with fields) needed for the communication are the following:

* **Note**
    * id: int
    * headline: String
    * noteText: String
    * priority: int
    * executionDates: List<DateTime>
    * reminderDates: List<DateTime>
* **NoteSelector**
    * ids: List<Integer>
    * idRelation: Relation
    * date: DateTime
    * dateRelation: Relation
    * priority: int
    * priorityRelation: Relation
* **Relation** (enum)
    * NONE
    * BEFORE
    * AFTER
    * GREATER
    * LESS
    * EQUALS
  
All communication classes need to satisfy the [java bean convention](https://en.wikipedia.org/wiki/JavaBeans).

# Links

(will be added later...)