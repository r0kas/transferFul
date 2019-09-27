package com.github.r0kas.model.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Currency;
import java.util.UUID;

/**
 * The type Request account represents JSON object used for mapping incoming account requests.
 */
public class RequestAccount {

  private UUID holderId;
  private Currency currency;

  /**
   * Instantiates a new Request account.
   *
   * @param holderId the holder id
   * @param currency the currency
   */
  @JsonCreator
  public RequestAccount(@JsonProperty("holderId") UUID holderId,
                        @JsonProperty("currency") Currency currency) {
    this.holderId = holderId;
    this.currency = currency;
  }

  /**
   * Getter for holder id.
   *
   * @return the holder id
   */
  public UUID getHolderId() {
    return holderId;
  }

  /**
   * Getter for currency.
   *
   * @return the currency
   */
  public Currency getCurrency() {
    return currency;
  }
}
