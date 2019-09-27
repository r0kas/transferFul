package com.github.r0kas.model.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * The type Request transfer represents JSON object used for mapping incoming transfer requests.
 */
public class RequestTransfer {

  private UUID sourceAccountId;
  private UUID targetAccountId;
  private double amount;

  /**
   * Instantiates a new Request transfer.
   *
   * @param sourceAccountId the source account id
   * @param targetAccountId the target account id
   * @param amount          the amount
   */
  @JsonCreator
  RequestTransfer(@JsonProperty("source")UUID sourceAccountId,
                  @JsonProperty("target")UUID targetAccountId,
                  @JsonProperty("amount")double amount) {
    this.sourceAccountId = sourceAccountId;
    this.targetAccountId = targetAccountId;
    this.amount = amount;
  }

  /**
   * Getter for source account id.
   *
   * @return the source account id
   */
  public UUID getSourceAccountId() {
    return sourceAccountId;
  }

  /**
   * Getter for target account id.
   *
   * @return the target account id
   */
  public UUID getTargetAccountId() {
    return targetAccountId;
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
