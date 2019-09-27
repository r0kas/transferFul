Feature: User related endpoints
  Creating, getting, modifying and deleting users.

  Background:
    Given DataStore is empty
    And TransferFulApp is running
    And endpoint /health returns 200 in next 5 seconds

  Scenario Template: Create new user with valid inputs
    Given user has account request with <name>, <address>, <country>, <type>
    And user sends POST request to /user endpoint
    Then user receives response with <code>
    And response account data contains values: <name>, <address>, <country>, <type>

    Examples:
    | name    | address     | country | type       | code |
    | "John"  | "Wall st."  | "US"    | "Personal" | 201  |
    | "Eve"   | "Vilnius st"| "LT"    | "Business" | 201  |
    | "Bob"   | "Short st." | "DE"    | "Personal" | 201  |

  Scenario Template: Create new user fails with invalid inputs
    Given user has account request with <name>, <address>, <country>, <type>
    And user sends POST request to /user endpoint
    Then user receives response with <code>

    Examples:
      | name    | address     | country | type       | code |
      | ""      | "Some st."  | "FR"    | "Personal" | 400  |
      | "John"  | ""          | "FR"    | "Personal" | 400  |
      | "John"  | "Wall st."  | ""      | "Personal" | 400  |
      | "John"  | "Wall st."  | "SOME"  | "Personal" | 400  |
      | "John"  | "Some st."  | "FR"    | "UNKNOWN"  | 400  |
      | "John"  | "Some st."  | "FR"    | ""         | 400  |

  Scenario: Get existing user
    Given user has account request with "John", "Vilnius st.", "LT", "Personal"
    And user sends POST request to /user endpoint
    And user receives response with 201
    And user saves received ID
    Then user sends GET request to /user with ID
    And user receives response with 200
    And response account data contains values: "John", "Vilnius st.", "LT", "Personal"

  Scenario: Get non existing user
    Given user has random ID
    Then user sends GET request to /user with ID
    And user receives response with 404
    And response message contains "no user found with id"

  Scenario: Delete user with linked account fails
    Given user has account request with "John", "Vilnius st.", "LT", "Personal"
    And user sends POST request to /user endpoint
    Then user receives response with 201
    And user saves received ID
    Then user has account request with saved ID and "USD"
    And user sends POST request to /account endpoint
    And user saves received accountID_0
    Then user sends DELETE request to /user with ID
    And user receives response with 409
    And response message contains "cannot delete. Resource has linked objects"

  Scenario: Delete existing user
    Given user has account request with "John", "Vilnius st.", "LT", "Personal"
    And user sends POST request to /user endpoint
    And user receives response with 201
    And user saves received ID
    Then user sends DELETE request to /user with ID
    And user receives response with 200
    And response message contains "deleted user with id"

  Scenario: Delete non existing user
    Given user has random ID
    Then user sends DELETE request to /user with ID
    And user receives response with 404
    And response message contains "no user found with id"

  Scenario Template: Patch existing user with valid inputs
    Given user has account request with "John", "Vilnius st.", "LT", "Personal"
    And user sends POST request to /user endpoint
    And user saves received ID
    Then user has account request with <newName>, <newAddress>, <newCountry>, <newType>
    And user sends PATCH request to /user with ID
    And user receives response with <code>
    And response account data contains values: <name>, <address>, <country>, <type>

    Examples:
      | newName | newAddress   | newCountry | newType    | code | name   | address      | country | type       |
      | ""      | "Wall st."   | "US"       | ""         | 200  | "John" | "Wall st."   | "US"    | "Personal" |
      | "Eve"   | ""           | ""         | "Business" | 200  | "Eve"  | "Vilnius st."| "LT"    | "Business" |
      | "Bob"   | "Short st."  | "DE"       | "Business" | 200  | "Bob"  | "Short st."  | "DE"    | "Business" |

  Scenario Template: Patch existing user fails with invalid inputs
    Given user has account request with "John", "Vilnius st.", "LT", "Personal"
    And user sends POST request to /user endpoint
    And user saves received ID
    Then user has account request with <name>, <address>, <country>, <type>
    And user sends PATCH request to /user with ID
    Then user receives response with <code>

    Examples:
      | name    | address     | country | type       | code |
      | "John"  | "Wall st."  | "SOME"  | "Personal" | 400  |
      | ""      | ""          | "FR"    | "UNKNOWN"  | 400  |

  Scenario: Patch non existing user
    Given user has random ID
    And user has account request with "John", "Vilnius st.", "LT", "Personal"
    Then user sends PATCH request to /user with ID
    And user receives response with 404
    And response message contains "no user found with id"
