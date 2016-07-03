## My Scheduler

Follow these steps to get started:

1. Git-clone this repository.

        $ git clone https://github.com/yossichen78/my-scheduler.git

2. Create DB on mysql: create a new db called "scheduler" and "scheduler-test". Create table on both DBs using resources/db/schema.sql

3. Setup resources/application.conf :
    
   Default Username and Password for both DBs are set to "user"/"password".
   
   DB URI is jdbc:mysql://localhost:8889/scheduler
 
   Default host and port are "localhost"/"8080".
   
   All of these values can be changed by editing application.conf. There are two of these - at the main and test folders.

4. Change directory into your clone:

        $ cd my-project

5. Launch SBT:

        $ sbt

6. Compile everything and run all tests:

        > test

7. Start the application:

        > re-start

8. To view all events on the DB Browse to [http://localhost:8080](http://localhost:8080/) 

   (or the other host/port combination defined at application.conf)

9. To add events POST an event json to [http://localhost:8080](http://localhost:8080/)

   (or the other host/port combination defined at application.conf)
    
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

