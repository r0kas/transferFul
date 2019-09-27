package com.github.r0kas.model.data;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Holder Type represents user type in the system.
 */
public enum HolderType {
  /**
   * Personal type user.
   */
  PERSONAL("Personal"),
  /**
   * Business type user.
   */
  BUSINESS("Business");

  @JsonValue
  private final String displayName;

  HolderType(String displayName) {
    this.displayName = displayName;
  }

  /**
   * Gets holder type name.
   *
   * @return the display name
   */
  public String getDisplayName() {
    return displayName;
  }
}
