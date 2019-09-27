package com.github.r0kas.controller.data;

import com.github.r0kas.model.data.Account;
import com.sun.jdi.InvalidTypeException;
import java.util.Currency;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.naming.InsufficientResourcesException;

public interface AccountService {
  /**
   * Creates new account in application's persistence layer.
   *
   * @param holderId UUID of new account's owner
   * @param accountCurrency created account's currency
   * @return UUID of newly created account
   * @throws IllegalArgumentException if parameters validation fails
   * @throws NoSuchElementException if no User is found with provided holder ID
   */
  UUID createAccount(UUID holderId, Currency accountCurrency)
      throws IllegalArgumentException, NoSuchElementException;

  /**
   * Retrieve account by provided its ID.
   *
   * @param accountId UUID of desired account
   * @return Account for provided ID
   * @throws NoSuchElementException if account with provided ID doesn't exists
   */
  Account getAccount(UUID accountId) throws NoSuchElementException;

  /**
   * Removes account entry of provided ID reference.
   *
   * @param accountId UUID of account to delete
   * @throws NoSuchElementException if account with provided ID doesn't exists
   */
  void deleteAccount(UUID accountId) throws NoSuchElementException;

  /**
   * Deposit some amount of currency to desired account.
   *
   * @param accountId UUID of account to deposit to
   * @param amount of funds that is desired to deposit
   * @param currency which is desired to deposit to account
   * @throws NoSuchElementException if account with provided ID doesn't exists
   * @throws InvalidTypeException if provided currency does not match with account currency
   */
  void deposit(UUID accountId, double amount, Currency currency)
      throws NoSuchElementException, InvalidTypeException;

  /**
   * Remove some amount of currency from desired account.
   *
   * @param accountId UUID of account to withdraw from
   * @param amount of funds to withdraw from account
   * @param currency which is desired to withdraw from account
   * @throws NoSuchElementException if account with provided ID doesn't exists
   * @throws InvalidTypeException if provided currency does not match with account currency
   * @throws InsufficientResourcesException if account balance is insufficient
   */
  void withdraw(UUID accountId, double amount, Currency currency)
      throws NoSuchElementException, InvalidTypeException, InsufficientResourcesException;

  /**
   * Transfer desired amount of funds from source account to target account.
   *
   * @param sourceAccountID UUID of account from which to transfer funds
   * @param targetAccountID UUID of account to which to transfer funds
   * @param amount of funds to transfer
   * @throws NoSuchElementException if one source or target does not exist
   * @throws InvalidTypeException if source account currency does not match with target
   * @throws InsufficientResourcesException if source account does not have sufficient balance
   */
  void transfer(UUID sourceAccountID, UUID targetAccountID, double amount)
      throws NoSuchElementException, InvalidTypeException, InsufficientResourcesException;
}
