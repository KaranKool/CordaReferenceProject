# Corda Reference Project
Reference project for good practices used in Corda.

Corda Blockchain generally follows the following Architecture structure:

<p align="center">
  <img src="Architecture.PNG" alt="Blockchain Architecture" width="500" />
</p>

In this project we will tackle the Blockchain layer and Blockchain API layer. The Blockchain layer is coded in Kotlin language and API in Springboot. The project contains a single node which interacts with the PostgreSQL DB through the corda flows. **No State or Contract is initiated in this Project**. The project also contains web apis for creating, updating and querying in the db.

## Running the nodes

See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.

## Interacting with the nodes

### Shell

When started via the command line, each node will display an interactive shell:

    Welcome to the Corda interactive shell.
    Useful commands include 'help' to see what is available, and 'bye' to shut down the node.

    Tue Nov 06 11:58:13 GMT 2018>>>

You can use this shell to interact with your node.
You can find out more about the node shell [here](https://docs.corda.net/shell.html).

### Webserver

`clients/src/main/kotlin/com/flowdb/webserver/` defines a simple Spring webserver that connects to a node via RPC and
allows you to interact with the node over HTTP.

The API endpoints are defined here:

     clients/src/main/kotlin/com/flowdb/webserver/Controller.kt

#### Running the webserver

##### Via the command line

Run the `runTemplateServer` Gradle task. By default, it connects to the node with RPC address `localhost:10006` with
the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

##### Via IntelliJ

Run the `Run Template Server` run configuration. By default, it connects to the node with RPC address `localhost:10006`
with the username `user1` and the password `test`, and serves the webserver on port `localhost:10050`.

#### Interacting with the webserver

The endpoints include:

    http://localhost:10050/queryToken (GET Endpoint)
    http://localhost:10050/addToken  (POST Endpoint)
    http://localhost:10050/updateToken  (POST Endpoint)

## Logging

Logging is a ubiquitous need in programming. While apparently a simple idea (just print stuff!), there are many ways to do it. In fact, every language, operating system and environment has its own idiomatic and sometimes idiosyncratic logging solution; often, actually, more than one.

Here is Kotlin’s logging story.

First we need to include the necessary dependencies. The below is and example of Log4J but the same patterns and solutions apply to SLF4J, JUL, and other logging libraries.

```gradle
dependencies {
  compile group: 'org.apache.logging.log4j', name: 'log4j-api', version: '2.12.0'
  compile group: 'org.apache.logging.log4j', name: 'log4j-core', version: '2.12.0'
}
```
Logging is done by mainly by using companion objects in Kotlin. The below code shows how to define logger object to do logging in class
```Kotlin
class LoggerInCompanionObject {
    companion object {
        private val loggerWithExplicitClass
          = getLogger(LoggerInCompanionObject::class.java)
    }
 
    //...
}
```
So, with the above code, we can use the logger, in any method of the class:
```Kotlin
fun loggingExample(s: String) {
    loggerWithExplicitClass.info(s)
}
```

## Exception Handling

All exception classes in Kotlin are descendants of the class Throwable. Every exception has a message, stack trace and an optional cause.<br>
To throw an exception object, use the throw-expression:
``` Kotlin
throw IllegalArgumentException("Unknown party name.")
```
To catch an exception, use the try-expression:
``` Kotlin
try {
    // some code
}
catch (e: SomeException) {
    // handler
}
finally {
    // optional finally block
}
```

## Database connection

Corda uses H2 Database by default as its DB to maintain the Node vaults and all the data in the flows and states. Nodes can also be configured to use PostgreSQL and SQL Server.

### PostgreSQL
Nodes can also be configured to use PostgreSQL 9.6, using PostgreSQL JDBC Driver 42.1.4. Here is an example node configuration for PostgreSQL which is should be added in build.gradle :
``` gradle
extraConfig = [
                'dataSourceProperties.dataSource.url' : 'jdbc:postgresql://localhost:5432/postgres',
                'dataSourceProperties.dataSourceClassName' : 'org.postgresql.ds.PGSimpleDataSource',
                'dataSourceProperties.dataSource.user' : 'postgres',
                'dataSourceProperties.dataSource.password' : 'password',
                'jarDirs' : [ 'E:/' ]
        ]
```
It should be noted that you must have driver jar in the location specified in the jarDirs.

### SQLServer
Nodes also have untested support for Microsoft SQL Server 2017, using Microsoft JDBC Driver 6.2 for SQL Server. Here is an example node configuration for SQLServer:
```gradle
dataSourceProperties = {
    dataSourceClassName = "com.microsoft.sqlserver.jdbc.SQLServerDataSource"
    dataSource.url = "jdbc:sqlserver://[HOST]:[PORT];databaseName=[DATABASE_NAME]"
    dataSource.user = [USER]
    dataSource.password = [PASSWORD]
}
database = {
    transactionIsolationLevel = READ_COMMITTED
}
jarDirs = ["[FULL_PATH]/sqljdbc_6.2/enu/"]
```

## Database operations

Database operations are done by creating 2 service layers. First is used to query statements to the DB so the operations can be executed. Second is to connect the flows to db service layer. The DB service layer code is a standard code and can be used directly. The second layer needs to be configured according to project and must create the query statement the will be executed in the DB.

Examples of these can be found in the DatabaseService.kt and CryptoValuesDatabaseService.kt files in the reference project 

## Rest API definition/creation

To execute anything on the Corda network from client we need to create apis for the network. RPC opertaions are done by using the rpcOps from the corda core dependencies. 

First we need to establish a connection to node using a valid RPC login.
``` Kotlin
val client = node.rpcClientToNode()
client.start("user", "password")
val proxy = client.proxy()
```
This can also be done by creating a seperate file for configuration which is what we generally use since the same endpoints can be called by different users with different roles, examples for this can be found in the reference project within th client cordapp.

Next we need to create the endpoints for the apis we need.
``` Kotlin
@GetMapping(value = ["/queryToken"], produces = arrayOf("application/json"))
private fun queryTokenFun(req: RequestEntity<TokenModel>): ResponseEntity<Any> {
        // Put code here...
    }
```
Here the example follows the springboot method of creating endpoints. We generally create structure models to accept input and generate output to make it more structurally uniform.