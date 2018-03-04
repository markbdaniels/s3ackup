# s3ackup

Cloud storage backed by Amazon Simple Cloud Storage Service‎ (AWS S3).
This project allows for a directory structure within the users home directory (~/s3ackup/) to be synced to Amazon S3 automatically.
A simple web GUI allows for the user to download an object or directory and maintain the directory structure (unlike the [AWS S3 console](https://s3.console.aws.amazon.com/s3/home)). 
The project is intended to complement the [AWS S3 console](https://s3.console.aws.amazon.com/s3/home) and [CLI](https://aws.amazon.com/cli). 
Functionality such as changing storage class can be done on the console as per normal.


Like dropbox/Google Drive, new and updated files will automatically be synchronized to S3.
Unlike dropbox the synchronization is one way. Only files that you explicitly request to be downloaded will be retrieved from S3.


To use this tool, the user is required to:
* open an account with [AWS](https://aws.amazon.com/free)
* create a bucket on [S3](https://s3.console.aws.amazon.com/s3/home)
* create a new IAM API key with [S3 permissions](https://console.aws.amazon.com/iam/home)
* build and run the project

The pricing for AWS S3 storage can be calculated [here](https://aws.amazon.com/s3/pricing/)
## Getting started

** Java 8 is required **

Run to see if java is installed

```
java -version 

```


Build project with maven as described below and then execute the jar

```
java -jar s3ackup-daemon/target/s3ackup-daemon-1.0.0.jar

```

When the project starts a webpage should load in the defaut browser automatically but if not navigate to here [http://localhost:56000/login](http://localhost:56000/login)





## Development


### Build project and React bundle (resources/static/built/bundle.js)

```
mvn clean install -Dskip.webpack=false -DskipTests
```

Change POM version of all modules

```
mvn versions:set versions:commit -DnewVersion=1.0.0 

```


### Tests

** Note that the test bucket must contain the word test. The tests delete all items in the bucket **

If want to run the tests you will either need to configure test/resources/application-integrationtest.properties

```
aws.key.access=
aws.key.secret=
aws.bucket=
```

or run the build with the parameters:
 
```
mvn clean install -Daws.key.access=access -Daws.key.secret=secret -Daws.bucket=test_bucket 
```

 



## Eclipse code formatting

[eclipse-code-formatter.xml](https://github.com/spring-projects/spring-boot/blob/master/eclipse/eclipse-code-formatter.xml)

