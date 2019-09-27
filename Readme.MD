# TransferFul API - money transfers

This application allows to create users, accounts and use these entities to transfer money between them.
First users needs to be created, they will be the holders of their accounts. 
Then accounts creation can follow by linking them to users in the process. Once there are accounts created -
funds moving operations can commence. Including deposit, withdraw and transfer.  

Application is built on [Spark Java](http://sparkjava.com/) which is lightweight and blazing fast web framework.

In memory data structures and user, account services quality is ensured with unit tests.
On top of that there are full coverage of BDD integration tests using cucumber.

## Prerequisites

To build and run this project you'll require:

 - Java 11
 - Maven
 
## Test and Build

Standard maven test goal includes checkstyle, unit and integration tests.
To start all tests run: `mvn clean test`

Build process will generate `fat` jar with all required dependencies included.
As lightweight web framework is used, application with all dependencies will use just over 8MB of system's disk space.
To start build process run: `mvn clean package`

Artifact will appear in target directory with `TransferFul-0.1.0.jar` name.


## Start the application

After application artifact is successfully generated it can be started using Java executable.

To start application on standard server port *4567* run: `java -jar TransferFul-0.1.0.jar`

Server port can be pre-defined when starting the application as first argument.

For ex.: `java -jar TransferFul-0.1.0.jar 8080` will start the application on port *8080*.

Ports between *1024* and *65535* can be chosen.


## API contract

Available api endpoints, their request and response structure is described in this section.

### POST /user
Creates new user based on valid user request.

#### Request
```json
{
  "name" : "Eve",
  "address" : "Vilnius st",
  "country" : "LT",
  "type" : "Business"
}
```
#### Response 201
```json
{
  "id" : "5020633a-c755-41f1-9bcc-5fcf2abcd9ec",
  "status" : "Created",
  "response" : {
    "id" : "91600974-96c5-42d0-bd2a-69b1b2fc87a4",
    "createdOn" : "2019-09-27T02:48:51.649874Z",
    "updatedOn" : "2019-09-27T02:48:51.649874Z",
    "name" : "Eve",
    "address" : "Vilnius st",
    "countryCode" : "LT",
    "type" : "Business",
    "ownedAccounts" : [ ]
  }
}
```

### GET /user/:id
Returns user if it exists. Existing user's UUID has to be provided as url parameter.

#### Response 200
```json
{
  "id" : "f47a1897-4a0f-45ab-977f-1efb65b09dd0",
  "status" : "Created",
  "response" : {
    "id" : "3dc7dbda-1da2-4b18-9f81-3507a9fa408b",
    "createdOn" : "2019-09-27T02:48:51.747996Z",
    "updatedOn" : "2019-09-27T02:48:51.747996Z",
    "name" : "John",
    "address" : "Vilnius st.",
    "countryCode" : "LT",
    "type" : "Personal",
    "ownedAccounts" : [ ]
  }
}
```

### DELETE /user/:id
Deletes a user if it exists. Existing user's UUID has to be provided as url parameter.

#### Response 200
```json
{
  "id" : "f2916329-566b-42d9-a72c-b5577ddcd937",
  "status" : "OK",
  "response" : "deposit successful to account with id: 5169f7b6-741c-4bf7-8478-d98db9921480"
}
```

### PATCH /user/:id
Update existing user with new values. Existing user's UUID had to be provided as url parameter.

#### Request
```json
{
  "name" : "Eve",
  "type" : "Business"
}
```
#### Response 200
```json
{
  "id" : "f47a1897-4a0f-45ab-977f-1efb65b09dd0",
  "status" : "Created",
  "response" : {
    "id" : "3dc7dbda-1da2-4b18-9f81-3507a9fa408b",
    "createdOn" : "2019-09-27T02:48:51.747996Z",
    "updatedOn" : "2019-09-27T02:55:31.234791Z",
    "name" : "Eve",
    "address" : "Vilnius st.",
    "countryCode" : "LT",
    "type" : "Business",
    "ownedAccounts" : [ ]
  }
}
```

### POST /account
Creates new account based on valid account request.

#### Request
```json
{
  "holderId" : "2c6aa158-637a-4f09-8a63-d0752cd18d40",
  "currency" : "USD"
}
```
#### Response 201
```json
{
  "id" : "e28e6e51-13d8-4abd-8802-f090bdbb0e01",
  "status" : "OK",
  "response" : {
    "id" : "74bdf333-f6d2-4451-9fa6-99d086642c78",
    "holderId" : "2c6aa158-637a-4f09-8a63-d0752cd18d40",
    "createdOn" : "2019-09-27T02:53:08.012876Z",
    "updatedOn" : "2019-09-27T02:53:08.012876Z",
    "currency" : "USD",
    "balance" : 0.0
  }
}
```

### GET /account/:id
Returns account if it exists. Existing account's UUID has to be provided as url parameter.

#### Response 200
```json
{
  "id" : "f2916329-566b-42d9-a72c-b5577ddcd937",
  "status" : "OK",
  "response" : {
    "id" : "bfca20ae-c673-4064-9916-e88c11fd4b84",
    "holderId" : "3e3e2659-cb26-445e-96ee-b543414ba673",
    "createdOn" : "2019-09-27T02:53:08.084087Z",
    "updatedOn" : "2019-09-27T02:53:08.090355Z",
    "currency" : "EUR",
    "balance" : 150.30
  }
}
```

### DELETE /account/:id
Deletes a account if it exists. Existing account's UUID has to be provided as url parameter.

#### Response 200
```json
{
  "id" : "f8f5d1a1-1b27-4e07-b27b-690c52e5ec6a",
  "status" : "OK",
  "response" : "deleted account with id: 0860cf1b-7d1a-4795-b5af-d55c5a7ffffd"
}
```


### POST /account/deposit
Deposit requested amount of funds into specified account.

#### Request
```json
{
  "accountId" : "bfca20ae-c673-4064-9916-e88c11fd4b84",
  "currency" : "EUR",
  "amount" : 150.30
}
```
#### Response 200
```json
{
  "id" : "f2916329-566b-42d9-a72c-b5577ddcd937",
  "status" : "OK",
  "response" : "deposit successful to account with id: bfca20ae-c673-4064-9916-e88c11fd4b84"
}
```

### POST /account/withdraw
Deposit requested amount of funds into specified account.

#### Request
```json
{
  "accountId" : "a2ed9011-1378-45b4-948f-0134a71f2188",
  "currency" : "EUR",
  "amount" : 666.54
}
```
#### Response 200
```json
{
  "id" : "f2916329-566b-42d9-a72c-b5577ddcd937",
  "status" : "OK",
  "response" : "withdraw successful from account with id: a2ed9011-1378-45b4-948f-0134a71f2188"
}
```


### POST /account/transfer
Transfer requested amount of funds from source to target account.

#### Request
```json
{
  "source" : "cbc77391-ec04-4db1-b820-44925e03c22a",
  "target" : "78dd84ef-e4c9-4998-a9b3-14aedc149ce2",
  "amount" : 55.0
}
```
#### Response 200
```json
{
  "id" : "f2916329-566b-42d9-a72c-b5577ddcd937",
  "status" : "OK",
  "response" : "transfer successful from account with id: cbc77391-ec04-4db1-b820-44925e03c22a to account with id: 78dd84ef-e4c9-4998-a9b3-14aedc149ce2"
}
```

### Expected errors
Following fault response codes can be returned on circumstances when requested operations cannot be fulfilled:
    
  - BAD_REQUEST_400 will be returned on scenarios when invalid request inputs are provided
  - NOT_FOUND_404 will be returned if user or account is not found for requested operation
  - CONFLICT_409 will be returned if objects have unsatisfied dependencies or do not match
  - PAYMENT_REQUIRED_402 will be returned when account has insufficient funds for the operation
