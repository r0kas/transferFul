package com.github.r0kas.model.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User represents formal entities to which accounts are linked to.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class User {

  private UUID id;
  private final String createdOn;
  private String updatedOn;
  private String name;
  private String address;
  private String countryCode;
  private HolderType type;
  private List<UUID> ownedAccounts;

  /**
   * New User constructor. User is a description of account holding entity.
   * User can be Business or Personal type.
   *
   * @param name        full name of the user. It can be name surname or company name
   * @param address     registration address of the user
   * @param countryCode of user residence
   * @param type        describes user type. One of HolderType enums.
   */
  public User(String name, String address, String countryCode, HolderType type) {
    this.id = UUID.randomUUID();
    this.createdOn = ZonedDateTime.now(ZoneOffset.UTC).toString();
    this.updatedOn = createdOn;
    this.name = name;
    this.address = address;
    this.countryCode = countryCode;
    this.type = type;
    this.ownedAccounts = new ArrayList<>();
  }

  /**
   * Getter fpr user uuid.
   *
   * @return user uuid
   */
  public UUID id() {
    return id;
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
   * Getter for Date Time of when this account was lastly updated.
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
   * Getter for user representing name.
   *
   * @return user name
   */
  public String name() {
    return name;
  }

  /**
   * Setter for user name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Getter for user address.
   *
   * @return the string
   */
  public String address() {
    return address;
  }

  /**
   * Setter for user address.
   *
   * @param address the address
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * Getter for country code string.
   *
   * @return country code string
   */
  public String countryCode() {
    return countryCode;
  }

  /**
   * Setter for country code.
   *
   * @param code string representing country code.
   */
  public void setCountryCode(String code) {
    this.countryCode = code;
  }

  /**
   * Getter for user type.
   *
   * @return the holder type
   */
  public HolderType type() {
    return type;
  }

  /**
   * Setter for user type.
   *
   * @param type holder type object representing user type
   */
  public void setType(HolderType type) {
    this.type = type;
  }

  /**
   * Get owned accounts list. User cannot be deleted until this list has anything.
   *
   * @return owned accounts list object containing accounts UUIDs
   */
  public List<UUID> ownedAccounts() {
    return ownedAccounts;
  }

  /**
   * Method to add additional owned account to owned accounts list.
   *
   * @param accountID the account id
   * @return boolean representing if account was added successfully
   */
  public boolean addOwnedAccount(UUID accountID) {
    return ownedAccounts.add(accountID);
  }

  /**
   * Remove owned account from owned accounts list.
   *
   * @param accountID the account id
   * @return boolean representing if account was removed successfully
   */
  public boolean removeOwnedAccount(UUID accountID) {
    return ownedAccounts.remove(accountID);
  }
}
