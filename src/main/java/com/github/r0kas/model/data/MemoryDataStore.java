package com.github.r0kas.model.data;

import static java.util.Objects.isNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In memory data storage structure for accounts and users.
 */
public final class MemoryDataStore {

  private static MemoryDataStore instance;

  private Map<UUID, Account> accounts;
  private Map<UUID, User> users;

  private MemoryDataStore() {
    accounts = new ConcurrentHashMap<>();
    users = new ConcurrentHashMap<>();
  }

  /**
   * Get instance ensures that this object is a singleton.
   *
   * @return existing DataStore instance or create and return new.
   */
  public static synchronized MemoryDataStore getInstance() {
    if (isNull(instance)) {
      instance = new MemoryDataStore();
    }
    return instance;
  }

  /**
   * Getter for account object from accounts map.
   *
   * @param accountID the account id
   * @return the account
   */
  public Account getAccount(UUID accountID) {
    return this.accounts.get(accountID);
  }

  /**
   * Puts account object into accounts map.
   *
   * @param account the account
   */
  public void setAccount(Account account) {
    this.accounts.put(account.id(), account);
  }

  /**
   * Is account present boolean.
   *
   * @param accountID the account id
   * @return the boolean
   */
  public boolean isAccountPresent(UUID accountID) {
    return this.accounts.containsKey(accountID);
  }

  /**
   * Remove account from accounts map.
   *
   * @param accountID the account id
   */
  public void removeAccount(UUID accountID) {
    this.accounts.remove(accountID);
  }

  /**
   * Getter for user object from user map.
   *
   * @param userID the user id
   * @return the user
   */
  public User getUser(UUID userID) {
    return this.users.get(userID);
  }

  /**
   * Puts user object into user map.
   *
   * @param user the user
   */
  public void setUser(User user) {
    this.users.put(user.id(), user);
  }

  /**
   * Is user present boolean.
   *
   * @param userID the user id
   * @return the boolean
   */
  public boolean isUserPresent(UUID userID) {
    return this.users.containsKey(userID);
  }

  /**
   * Remove user from user map.
   *
   * @param userID the user id
   */
  public void removeUser(UUID userID) {
    this.users.remove(userID);
  }
}
