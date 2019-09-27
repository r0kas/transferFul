Feature: Account related endpoints
  Creating, getting, modifying, deleting accounts.
  Making deposits, withdrawals and transfers between accounts.

  Background:
    Given DataStore is empty
    And TransferFulApp is running
    And endpoint /health returns 200 in next 5 seconds

  Scenario: Create new account with valid inputs
    Given user has account request with "John", "Vilnius st.", "LT", "Personal"
    And user sends POST request to /user endpoint
    Then user receives response with 201
    And user saves received ID
    Then user has account request with saved ID and "USD"
    And user sends POST request to /account endpoint
    And user receives response with 201

  Scenario: Create new account fails with non existing holder id
    Given user has random ID
    And user has account request with saved ID and "GBP"
    And user sends POST request to /account endpoint
    Then user receives response with 404
    And response message contains "no user found with id"

  Scenario: Create new account fails with invalid currency
    Given user has account request with "John", "Vilnius st.", "LT", "Personal"
    And user sends POST request to /user endpoint
    Then user receives response with 201
    And user saves received ID
    Then user has account request with saved ID and "INVALID"
    And user sends POST request to /account endpoint
    And user receives response with 400

  Scenario: Get existing account
    Given account is created with "SEK" currency
    And user saves received accountID_0
    When user sends GET request to /account with accountID_0
    Then user receives response with 200
    And response account data contains saved ID and "SEK"

  Scenario: Get non existing account
    Given user has random ID
    Then user has account request with saved ID and "EUR"
    And user sends GET request to /account with ID
    And user receives response with 404
    And response message contains "no account found with id"

  Scenario: Delete existing account
    Given account is created with "SEK" currency
    And user saves received accountID_0
    Then user sends DELETE request to /account with accountID_0
    And user receives response with 200
    And response message contains "deleted account with id"

  Scenario: Delete non existing account
    Given user has random ID
    Then user sends DELETE request to /account with ID
    And user receives response with 404
    And response message contains "no account found with id"

  Scenario Template: Deposit to account and withdraw
    Given account is created with "EUR" currency
    And user saves received accountID_0
    Then user has deposit-withdraw request with saved accountID_0 and <amount> and <currency>
    And user sends POST request to /account/deposit endpoint
    And user receives response with 200
    And response message contains "deposit successful to account with id"
    Then user sends GET request to /account with accountID_0
    And user receives response with 200
    And response account balance is <amount>
    Then user has deposit-withdraw request with saved accountID_0 and <amount> and <currency>
    And user sends POST request to /account/withdraw endpoint
    And user receives response with 200
    And response message contains "withdraw successful from account with id"
    Then user sends GET request to /account with accountID_0
    And user receives response with 200
    And response account balance is 0.0

    Examples:
      | amount | currency |
      | 150.3  | "EUR"    |
      | 222.22 | "EUR"    |
      | 666.55 | "EUR"    |

  Scenario: Deposit fails with non existent account id
    Given user has random accountID_0
    And user has deposit-withdraw request with saved accountID_0 and 33.33 and "USD"
    Then user sends POST request to /account/deposit endpoint
    And user receives response with 404
    And response message contains "no account found with id"

  Scenario: Deposit fails with non matching currency
    Given account is created with "EUR" currency
    And user saves received accountID_0
    Then user has deposit-withdraw request with saved accountID_0 and 12.34 and "USD"
    And user sends POST request to /account/deposit endpoint
    And user receives response with 409
    And response message contains "cannot complete. Incompatible currency"

  Scenario: Withdraw fails with insufficient balance
    Given account is created with "SEK" currency
    And user saves received accountID_0
    Then user has deposit-withdraw request with saved accountID_0 and 100.0 and "SEK"
    And user sends POST request to /account/deposit endpoint
    And user receives response with 200
    And response message contains "deposit successful to account with id"
    Then user has deposit-withdraw request with saved accountID_0 and 200.0 and "SEK"
    And user sends POST request to /account/withdraw endpoint
    And user receives response with 402
    And response message contains "insufficient funds in source account"

  Scenario: Withdraw fails with non matching currency
    Given account is created with "SEK" currency
    And user saves received accountID_0
    Then user has deposit-withdraw request with saved accountID_0 and 100.0 and "SEK"
    And user sends POST request to /account/deposit endpoint
    And user receives response with 200
    And response message contains "deposit successful to account with id"
    Then user has deposit-withdraw request with saved accountID_0 and 200.0 and "EUR"
    And user sends POST request to /account/withdraw endpoint
    And user receives response with 409
    And response message contains "cannot complete. Incompatible currency"

  Scenario Template: Transfer between two accounts
    Given account is created with "SEK" currency
    And user saves received accountID_0
    When user has deposit-withdraw request with saved accountID_0 and <deposit_0> and "SEK"
    And user sends POST request to /account/deposit endpoint
    Then user receives response with 200
    And response message contains "deposit successful to account with id"
    Given account is created with "SEK" currency
    And user saves received accountID_1
    When user has deposit-withdraw request with saved accountID_1 and <deposit_1> and "SEK"
    And user sends POST request to /account/deposit endpoint
    Then user receives response with 200
    And response message contains "deposit successful to account with id"
    When user has transfer request from accountID_0 to accountID_1 for <transfer>
    And user sends POST request to /account/transfer endpoint
    Then user receives response with 200
    And response message contains "transfer successful"
    When user sends GET request to /account with accountID_0
    And user receives response with 200
    And response account balance is <balance_0>
    When user sends GET request to /account with accountID_1
    And user receives response with 200
    And response account balance is <balance_1>

    Examples:
      | deposit_0 | deposit_1 | transfer | balance_0 | balance_1 |
      | 100.0     | 100.0     | 55.0     | 45.0      | 155.0     |
      | 103230.0  | 1.0       | 100000.0 | 3230.0    | 100001.0  |
      | 1.0       | 1.0       | 1.0      | 0.0       | 2.0       |
      | 33.0      | 0.0       | 13.0     | 20.0      | 13.0      |

  Scenario: Transfer fails with incompatible currencies
    Given account is created with "GBP" currency
    And user saves received accountID_0
    Given account is created with "SEK" currency
    And user saves received accountID_1
    When user has transfer request from accountID_0 to accountID_1 for 1.0
    And user sends POST request to /account/transfer endpoint
    Then user receives response with 409
    And response message contains "cannot complete. Incompatible currency"

  Scenario: Transfer fails due to insufficient funds
    Given account is created with "GBP" currency
    And user saves received accountID_0
    Given account is created with "GBP" currency
    And user saves received accountID_1
    When user has transfer request from accountID_0 to accountID_1 for 1.0
    And user sends POST request to /account/transfer endpoint
    Then user receives response with 402
    And response message contains "insufficient funds in source account"

  Scenario: Transfer fails with non existent account id
    Given user has random accountID_0
    Given account is created with "SEK" currency
    And user saves received accountID_1
    When user has transfer request from accountID_0 to accountID_1 for 1.0
    And user sends POST request to /account/transfer endpoint
    And user receives response with 404
    And response message contains "no account found with id"
