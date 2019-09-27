package com.github.r0kas.model.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;

/**
 * Account represents funds holding entity.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Account {

  private UUID id;
  private UUID holderId;
  private String createdOn;
  private String updatedOn;
  private Currency currency;
  private double balance;

  /**
   * New account constructor for generation of funds holding account.
   * Account must be linked to an account owning user.
   *
   * @param holderID UUID of User which will be the owner of the account
   * @param currency sets account's funds currency
   */
  public Account(UUID holderID, Currency currency) {
    this.id = UUID.randomUUID();
    this.holderId = holderID;
    this.createdOn = ZonedDateTime.now(ZoneOffset.UTC).toString();
    this.updatedOn = createdOn;
    this.currency = currency;
    this.balance = 0.0;
  }

  /**
   * Getter for account Id.
   *
   * @return account uuid
   */
  public UUID id() {
    return id;
  }

  /**
   * Getter for account holding entity Id.
   *
   * @return account holder uuid
   */
  public UUID holderId() {
    return holderId;
  }

  /**
   * Getter for Date Time of when this account was created.
   *
   * @return string representing creation date
   */
  public String createdOn() {
    return createdOn;
  }

  /**
   * Getter of latest update time.
   *
   * @return string representing last update date
   */
  public String updatedOn() {
    return updatedOn;
  }

  /**
   * Sets last update date time of this object.
   *
   * @param updatedOn ZonedDateTime of last update time.
   */
  public void setUpdatedOn(ZonedDateTime updatedOn) {
    this.updatedOn = updatedOn.toString();
  }

  /**
   * Getter of account set currency.
   *
   * @return account currency
   */
  public Currency currency() {
    return currency;
  }

  /**
   * Getter for current account balance.
   *
   * @return double representing account balance
   */
  public double balance() {
    return balance;
  }

  /**
   * Sets account balance.
   *
   * @param balance double to which account balance should be set
   */
  public void setBalance(double balance) {
    this.balance = balance;
  }
}
