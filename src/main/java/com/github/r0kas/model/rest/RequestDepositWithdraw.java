package com.github.r0kas.model.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Currency;
import java.util.UUID;

/**
 * The type Request deposit-withdraw represents
 * JSON object used for mapping account operation requests.
 * This object is shared between deposit and withdraw operations.
 */
public class RequestDepositWithdraw {

  private UUID accountId;
  private Currency currency;
  private double amount;

  /**
   * Instantiates a new Request deposit withdraw.
   *
   * @param accountId the account id
   * @param currency  the currency
   * @param amount    the amount
   */
  @JsonCreator
  RequestDepositWithdraw(@JsonProperty("accountId")UUID accountId,
                         @JsonProperty("currency")Currency currency,
                         @JsonProperty("amount")double amount) {
    this.accountId = accountId;
    this.currency = currency;
    this.amount = amount;
  }

  /**
   * Getter for account id.
   *
   * @return the account id
   */
  public UUID getAccountId() {
    return accountId;
  }

  /**
   * Getter for currency.
   *
   * @return the currency
   */
  public Currency getCurrency() {
    return currency;
  }

  /**
   * Getter for amount.
   *
   * @return the amount
   */
  public double getAmount() {
    return amount;
  }
}
