# BlobReader

### Description

This project has the objective to read any blob file of a database.

It extracts the blob object, deserializes it and saves it into the file system.

It is developed using Java 7 and maven.

### How to use:

1. Once the blob is read from database it needs to be cast to some object. So put all necessary jars in _/src/main/resources/jars/_. 
1. Execute: __mvn eclipse:eclipse__
2. Update the files:
    - _/src/main/resources/config/db.properties_ - set your database connection details
    - _/src/main/resources/config/sql_statement.properties_ - set the query used to retrieve the blob file as described in the file itself
4. Execute the project throught the IDE (Eclipse for example).

### Current Status

- It extracts the blob and saves it to the filesystem successfully.
- The blob is saved in two files. One in XML format and other in JSON format.
- Its execution only happens successfully when executed through the IDE (Eclipse for example).

### TODO

- Improve Unit Tests.
- Fix the Maven Assembly to generate project distrubtion correctly
- Fix the the code to allow read the database connection details when executing through command line.