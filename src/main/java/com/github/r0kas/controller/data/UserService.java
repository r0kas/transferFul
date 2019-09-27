package com.github.r0kas.controller.data;

import com.github.r0kas.model.data.HolderType;
import com.github.r0kas.model.data.User;
import com.github.r0kas.model.rest.RequestUser;
import java.util.NoSuchElementException;
import java.util.UUID;

public interface UserService {
  /**
   * Creates new user in application's persistence layer.
   *
   * @param name user's full name
   * @param address user's address of registration
   * @param countryCode user's country of residence
   * @param type user's account type
   * @return UUID of created user
   * @throws IllegalArgumentException if parameters validation fails
   */
  UUID createUser(String name, String address, String countryCode, HolderType type)
      throws IllegalArgumentException;

  /**
   * Retrieve user instance by providing respective user ID.
   *
   * @param userID UUID of account holder
   * @return found account holder object
   * @throws NoSuchElementException if no record is found for provided UUID
   */
  User getUser(UUID userID) throws NoSuchElementException;

  /**
   * Updates account user in application's persistence layer.
   *
   * @param userId UUID of user to update
   * @param data with which user will be updated
   * @throws NoSuchElementException if no user is found with provided ID
   */
  User updateUser(UUID userId, RequestUser data) throws NoSuchElementException;

  /**
   * Removes user entry of provided ID reference.
   *
   * @param userID UUID of user to delete
   * @throws NoSuchElementException if no user is found with provided ID
   * @throws UnsatisfiedLinkError if user has linked accounts
   */
  void deleteUser(UUID userID) throws NoSuchElementException, UnsupportedOperationException;
}
