package com.github.r0kas.model.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.r0kas.model.data.HolderType;

/**
 * The type Request user represents JSON object used for mapping incoming user requests.
 */
public class RequestUser {

  private String name;
  private String address;
  private String countryCode;
  private HolderType type;

  /**
   * Instantiates a new User Request object.
   *
   * @param name        user name
   * @param address     user address
   * @param countryCode user country code
   * @param type        user type
   */
  @JsonCreator
  public RequestUser(@JsonProperty("name") String name,
                     @JsonProperty("address") String address,
                     @JsonProperty("country") String countryCode,
                     @JsonProperty("type") HolderType type) {
    this.name = name;
    this.address = address;
    this.countryCode = countryCode;
    this.type = type;
  }

  /**
   * Getter for user name.
   *
   * @return user name
   */
  public String name() {
    return name;
  }

  /**
   * Getter for user address.
   *
   * @return user address
   */
  public String address() {
    return address;
  }

  /**
   * Getter for user country code.
   *
   * @return user country code
   */
  public String countryCode() {
    return countryCode;
  }

  /**
   * Getter for user type.
   *
   * @return user account type as Holder Type object
   */
  public HolderType type() {
    return type;
  }
}
