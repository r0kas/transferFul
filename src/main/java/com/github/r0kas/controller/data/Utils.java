package com.github.r0kas.controller.data;

import com.github.r0kas.model.data.HolderType;
import com.github.r0kas.model.data.User;
import com.github.r0kas.model.rest.RequestUser;
import com.google.common.base.Strings;
import com.sun.jdi.InvalidTypeException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import javax.naming.InsufficientResourcesException;

final class Utils {

  private static final Set<String> ISO_COUNTRIES =
      new HashSet<>(Arrays.asList(Locale.getISOCountries()));

  static User modifyUser(User user, RequestUser modData) {
    String name = Objects.requireNonNullElse(modData.name(), user.name());
    user.setName(name);

    String address = Objects.requireNonNullElse(modData.address(), user.address());
    user.setAddress(address);

    String countryCode = Objects.requireNonNullElse(modData.countryCode(), user.countryCode());
    validateCountryCode(countryCode);
    user.setCountryCode(countryCode);

    HolderType type = Objects.requireNonNullElse(modData.type(), user.type());
    user.setType(type);

    user.setUpdatedOn(ZonedDateTime.now(ZoneOffset.UTC));
    return user;
  }

  static void validateBalance(double actual, double toWithdraw)
      throws InsufficientResourcesException {
    if (actual < toWithdraw) {
      throw new InsufficientResourcesException("balance too low to withdraw " + toWithdraw);
    }
  }

  static void validateCurrencyMatch(Currency source, Currency target) throws InvalidTypeException {
    if (source.equals(target)) {
      return;
    }
    throw new InvalidTypeException("currencies do not match");
  }

  static void validateCountryCode(String countryCode) throws IllegalArgumentException {
    if (ISO_COUNTRIES.contains(countryCode)) {
      return;
    }
    throw new IllegalArgumentException(countryCode + " is not a valid country code");
  }

  static void validateStringParams(String... params) throws IllegalArgumentException {
    for (String param : params) {
      if (Strings.isNullOrEmpty(param)) {
        throw new IllegalArgumentException("provided arguments must hold value");
      }
    }
  }

  static void validateObjectParams(Object... params) throws IllegalArgumentException {
    for (Object param : params) {
      if (Objects.isNull(param)) {
        throw new IllegalArgumentException("provided arguments must hold value");
      }
    }
  }
}
