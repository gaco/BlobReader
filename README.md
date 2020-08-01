# BlobReader
[![Build Status](https://travis-ci.org/gaco/BlobReader.svg?branch=master)](https://travis-ci.org/gaco/BlobReader)
---

### Description

This project has the objective to read any Blob file of a database.

It extracts the blob object, deserializes it and saves it into the file system.

It is developed using Java 7 and maven.

### How to use:

1a. Once the blob is read from database it needs to be cast to some object. So put all necessary jars in _/src/main/resources/jars/_. Then execute: __mvn eclipse:eclipse__. 

1b. Another option is to do a maven build (__mvn clean install__), which will generate a BlobReader-dist.zip. Unzip it and put all needed jars in _/jars/_

2. Update the files:
    - _/src/main/resources/config/db.properties_ - set your database connection details
    - _/src/main/resources/config/sql_statement.properties_ - set the query used to retrieve the blob file as described in the file itself

3. Execute the project throught the IDE (Eclipse for example) if step 1a was followed or trough the .CMD/.SH generated if you used step 1b.

### Current Status

- It extracts the blob and saves it to the filesystem successfully.
- The blob is saved in two files. One in XML format and other in JSON format.
