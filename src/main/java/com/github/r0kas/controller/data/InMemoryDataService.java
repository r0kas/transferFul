package com.github.r0kas.controller.data;

import static com.github.r0kas.controller.data.Utils.modifyUser;
import static com.github.r0kas.controller.data.Utils.validateBalance;
import static com.github.r0kas.controller.data.Utils.validateCountryCode;
import static com.github.r0kas.controller.data.Utils.validateCurrencyMatch;
import static com.github.r0kas.controller.data.Utils.validateObjectParams;
import static com.github.r0kas.controller.data.Utils.validateStringParams;

import com.github.r0kas.model.data.Account;
import com.github.r0kas.model.data.HolderType;
import com.github.r0kas.model.data.MemoryDataStore;
import com.github.r0kas.model.data.User;
import com.github.r0kas.model.rest.RequestUser;
import com.sun.jdi.InvalidTypeException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.naming.InsufficientResourcesException;

public class InMemoryDataService implements UserService, AccountService {

  private MemoryDataStore dataStore;

  public InMemoryDataService() {
    this.dataStore = MemoryDataStore.getInstance();
  }

  @Override
  public UUID createAccount(UUID holderId, Currency accountCurrency)
      throws IllegalArgumentException, NoSuchElementException {

    validateObjectParams(holderId, accountCurrency);
    validateUserPresent(holderId);

    Account account = new Account(holderId, accountCurrency);
    dataStore.setAccount(account);
    addOwnedAccountToUser(holderId, account.id());

    return account.id();
  }

  @Override
  public Account getAccount(UUID accountId) throws NoSuchElementException {
    validateAccountPresent(accountId);
    return dataStore.getAccount(accountId);
  }

  @Override
  public synchronized void deleteAccount(UUID accountId) throws NoSuchElementException {
    validateAccountPresent(accountId);
    Account accountToRemove = dataStore.getAccount(accountId);

    removeOwnedAccountFromUser(accountToRemove.holderId(), accountToRemove.id());
    dataStore.removeAccount(accountToRemove.id());
  }

  @Override
  public synchronized void deposit(UUID accountId, double amount, Currency currency)
      throws NoSuchElementException, InvalidTypeException {

    Account account = getAccount(accountId);
    double deposit = Math.abs(amount);

    validateCurrencyMatch(account.currency(), currency);

    account.setBalance(account.balance() + deposit);
    account.setUpdatedOn(ZonedDateTime.now(ZoneOffset.UTC));
    dataStore.setAccount(account);
  }

  @Override
  public synchronized void withdraw(UUID accountId, double amount, Currency currency)
      throws NoSuchElementException, InvalidTypeException, InsufficientResourcesException {

    Account account = getAccount(accountId);
    double withdraw = Math.abs(amount);

    validateCurrencyMatch(account.currency(), currency);
    validateBalance(account.balance(), withdraw);

    account.setBalance(account.balance() - withdraw);
    account.setUpdatedOn(ZonedDateTime.now(ZoneOffset.UTC));
    dataStore.setAccount(account);
  }

  @Override
  public synchronized void transfer(UUID sourceAccountID, UUID targetAccountID, double amount)
      throws NoSuchElementException, InvalidTypeException, InsufficientResourcesException {

    Account sourceAccount = getAccount(sourceAccountID);
    Account targetAccount = getAccount(targetAccountID);
    double transfer = Math.abs(amount);

    withdraw(sourceAccount.id(), transfer, targetAccount.currency());
    deposit(targetAccount.id(), transfer, sourceAccount.currency());
  }

  @Override
  public UUID createUser(String name, String address,
                         String countryCode, HolderType type)
      throws IllegalArgumentException {

    validateStringParams(name, address, countryCode);
    validateObjectParams(type);
    validateCountryCode(countryCode);

    User user = new User(name, address, countryCode, type);
    dataStore.setUser(user);
    return user.id();
  }

  @Override
  public User getUser(UUID userID) throws NoSuchElementException {
    validateUserPresent(userID);
    return dataStore.getUser(userID);
  }

  @Override
  public synchronized User updateUser(UUID userId, RequestUser data) throws NoSuchElementException {
    User userToModify = getUser(userId);
    User modifiedUser = modifyUser(userToModify, data);
    dataStore.setUser(modifiedUser);
    return modifiedUser;
  }

  @Override
  public synchronized void deleteUser(UUID userID)
      throws NoSuchElementException, UnsupportedOperationException {

    validateUserPresent(userID);
    validateUserHasNoAccounts(userID);
    dataStore.removeUser(userID);
  }

  private void addOwnedAccountToUser(UUID holderId, UUID accountId) {
    User holder = dataStore.getUser(holderId);
    holder.addOwnedAccount(accountId);
    holder.setUpdatedOn(ZonedDateTime.now(ZoneOffset.UTC));
    dataStore.setUser(holder);
  }

  private void removeOwnedAccountFromUser(UUID holderId, UUID accountId) {
    User holder = dataStore.getUser(holderId);
    holder.removeOwnedAccount(accountId);
    holder.setUpdatedOn(ZonedDateTime.now(ZoneOffset.UTC));
    dataStore.setUser(holder);
  }

  private void validateAccountPresent(UUID accountID) throws NoSuchElementException {
    if (dataStore.isAccountPresent(accountID)) {
      return;
    }
    throw new NoSuchElementException("no account found with id: " + accountID.toString());
  }

  private void validateUserPresent(UUID userID) throws NoSuchElementException {
    if (dataStore.isUserPresent(userID)) {
      return;
    }
    throw new NoSuchElementException("no user found with id: " + userID.toString());
  }

  private void validateUserHasNoAccounts(UUID userID) throws UnsupportedOperationException {
    if (dataStore.getUser(userID).ownedAccounts().isEmpty()) {
      return;
    }
    throw new UnsupportedOperationException("user has linked accounts");
  }
}
