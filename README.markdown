## _spray_ My Scheduler

For this project I used Spray and MySQL.

Follow these steps to get started:

1. Git-clone this repository.

        $ git clone git://github.com/spray/spray-template.git my-project

2. Create DB on mysql, create a new db called "scheduler". The schema for the table is @ resources/db/schema.sql

3. Username and Password are set to user/password, but this as well as the DB connection settings can be changed @ resources/application.conf
   
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

all fields are mandatory, event type should be "welcome_email" | "clear_cache" | "meeting_reminder".
event_time should be a timestamp string in the format "YYYY-MM-DD hh-mm-ss".

10. Stop the application:

        > re-stop

