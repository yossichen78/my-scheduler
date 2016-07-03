## My Scheduler

Follow these steps to get started:

1. Git-clone this repository.

        $ git clone https://github.com/yossichen78/my-scheduler.git

2. Create DB on mysql: create a new db called "scheduler" and "scheduler-test". Create table on both DBs using resources/db/schema.sql

3. Username and Password for both DBs are set to "user"/"password", but these as well as the other DB connection settings can be changed.
 
   They are set-up at resources/application.conf (main and test have separate files).
   
4. Change directory into your clone:

        $ cd my-project

5. Launch SBT:

        $ sbt

6. Compile everything and run all tests:

        > test

7. Start the application:

        > re-start

8. To view all events on the DB Browse to [http://localhost:8080](http://localhost:8080/)

9. To add events POST an event json to [http://localhost:8080](http://localhost:8080/)

    example:
    
       {
            "creator_name":"yoss",
            "event_type":"clear_cache",
            "event_target":"resource1",
            "event_time":"2016-07-01 05:13:00"
       } 

    * all fields are mandatory, event_type should be "welcome_email" | "clear_cache" | "meeting_reminder".
    
    event_time should be a timestamp string in the format "YYYY-MM-DD hh-mm-ss".

10. Stop the application:

        > re-stop

