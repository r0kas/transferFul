package com.github.r0kas.controller.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.r0kas.model.data.Account;
import com.github.r0kas.model.data.HolderType;
import com.github.r0kas.model.data.MemoryDataStore;
import com.github.r0kas.model.data.User;
import com.sun.jdi.InvalidTypeException;
import java.util.Currency;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.naming.InsufficientResourcesException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryAccountServiceTest {

  private AccountService accountService;
  private UserService userService;
  private MemoryDataStore dataStore;
  private UUID userId;
  private Currency eur;

  @BeforeEach
  void init() {
    accountService = new InMemoryDataService();
    userService = new InMemoryDataService();
    dataStore = MemoryDataStore.getInstance();
    userId = userService.createUser("John Wick", "Vilnius st. 1", "LT", HolderType.BUSINESS);
    eur = Currency.getInstance("EUR");
  }

  @AfterEach
  void cleanUp() {
    accountService = null;
    userService = null;
  }

  @Test
  void createAccount_withValidInputs() {
    UUID testAccountId = accountService.createAccount(userId, eur);
    Account testAccount = dataStore.getAccount(testAccountId);
    User holder = dataStore.getUser(userId);

    assertTrue(holder.ownedAccounts().contains(testAccountId));
    assertEquals(eur, testAccount.currency());
    assertEquals(userId, testAccount.holderId());
    assertEquals(0.0, testAccount.balance());
  }

  @Test
  void createAccount_withNonExistentHolderId_shouldThrow() {
    assertThrows(NoSuchElementException.class,
        () -> accountService.createAccount(UUID.randomUUID(), eur));
  }

  @Test
  void createAccount_withNotValidCurrency_shouldThrow() {
    assertThrows(IllegalArgumentException.class,
        () -> accountService.createAccount(userId, Currency.getInstance("ABC")));
  }

  @Test
  void createAccount_withNullParams_shouldThrow() {
    assertThrows(IllegalArgumentException.class,
        () -> accountService.createAccount(null, null));
  }

  @Test
  void getAccount_withValidInputs() {
    Account expectedAccount = new Account(userId, eur);
    dataStore.setAccount(expectedAccount);

    Account testAccount = accountService.getAccount(expectedAccount.id());

    assertEquals(expectedAccount, testAccount);
  }

  @Test
  void getAccount_withNonExistentId_shouldThrow() {
    assertThrows(NoSuchElementException.class,
        () -> accountService.getAccount(UUID.randomUUID()));
  }

  @Test
  void deleteAccount_withValidInputs() {
    Account testAccount = new Account(userId, eur);
    dataStore.setAccount(testAccount);
    User holder = dataStore.getUser(userId);

    accountService.deleteAccount(testAccount.id());

    assertFalse(dataStore.isAccountPresent(testAccount.id()));
    assertTrue(holder.ownedAccounts().isEmpty());
  }

  @Test
  void deleteAccount_withNonExistentId_shouldThrow() {
    assertThrows(NoSuchElementException.class,
        () -> accountService.deleteAccount(UUID.randomUUID()));
  }

  @Test
  void deposit_withValidInputs() {
    Account testAccount = new Account(userId, eur);
    dataStore.setAccount(testAccount);
    double deposit = 100.01;

    assertDoesNotThrow(() -> accountService.deposit(testAccount.id(), deposit, eur));
    assertEquals(deposit, dataStore.getAccount(testAccount.id()).balance());
  }

  @Test
  void deposit_withNotMatchingCurrency_shouldThrow() {
    Account testAccount = new Account(userId, eur);
    dataStore.setAccount(testAccount);

    assertThrows(InvalidTypeException.class,
        () -> accountService.deposit(testAccount.id(), 100.1, Currency.getInstance("GBP")));
  }

  @Test
  void deposit_withNotExistentAccountId_shouldThrow() {
    assertThrows(NoSuchElementException.class,
        () -> accountService.deposit(UUID.randomUUID(), 100.1, eur));
  }

  @Test
  void withdraw_withValidInputs() {
    double balance = 100.01;
    double withdraw = 50.44;
    Account testAccount = new Account(userId, eur);
    testAccount.setBalance(balance);
    dataStore.setAccount(testAccount);

    assertDoesNotThrow(() -> accountService.withdraw(testAccount.id(), withdraw, eur));
    assertEquals(balance - withdraw, dataStore.getAccount(testAccount.id()).balance());
  }

  @Test
  void withdraw_withInsufficientBalance_shouldThrow() {
    double withdraw = 50.44;
    Account testAccount = new Account(userId, eur);
    dataStore.setAccount(testAccount);

    assertThrows(InsufficientResourcesException.class,
        () -> accountService.withdraw(testAccount.id(), withdraw, eur));
  }

  @Test
  void withdraw_withNotMatchingCurrency_shouldThrow() {
    Account testAccount = new Account(userId, eur);
    dataStore.setAccount(testAccount);

    assertThrows(InvalidTypeException.class,
        () -> accountService.withdraw(testAccount.id(), 100.1, Currency.getInstance("GBP")));
  }

  @Test
  void withdraw_withNonExistentId_shouldThrow() {
    assertThrows(NoSuchElementException.class,
        () -> accountService.withdraw(UUID.randomUUID(), 1, eur));
  }

  @Test
  void transfer_withValidInputs() {
    double sourceBalance = 50.5;
    Account sourceAccount = new Account(userId, eur);
    sourceAccount.setBalance(sourceBalance);
    Account targetAccount = new Account(userId, eur);
    dataStore.setAccount(sourceAccount);
    dataStore.setAccount(targetAccount);

    double transfer = 22.2;
    assertDoesNotThrow(
        () -> accountService.transfer(sourceAccount.id(), targetAccount.id(), transfer));
    assertEquals(sourceBalance - transfer, dataStore.getAccount(sourceAccount.id()).balance());
    assertEquals(transfer, dataStore.getAccount(targetAccount.id()).balance());
  }

  @Test
  void transfer_withNotMatchingCurrency_shouldThrow() {
    Account sourceAccount = new Account(userId, eur);
    Account targetAccount = new Account(userId, Currency.getInstance("GBP"));
    dataStore.setAccount(sourceAccount);
    dataStore.setAccount(targetAccount);

    assertThrows(InvalidTypeException.class,
        () -> accountService.transfer(sourceAccount.id(), targetAccount.id(), 1));
  }

  @Test
  void transfer_withInsufficientSourceBalance_shouldThrow() {
    Account sourceAccount = new Account(userId, eur);
    Account targetAccount = new Account(userId, eur);
    dataStore.setAccount(sourceAccount);
    dataStore.setAccount(targetAccount);

    assertThrows(InsufficientResourcesException.class,
        () -> accountService.transfer(sourceAccount.id(), targetAccount.id(), 1));
  }

  @Test
  void transfer_withNonExistentAccountId_shouldThrow() {
    assertThrows(NoSuchElementException.class,
        () -> accountService.transfer(UUID.randomUUID(), UUID.randomUUID(), 1));
  }
}
