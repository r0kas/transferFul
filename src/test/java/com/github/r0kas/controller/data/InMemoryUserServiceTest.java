package com.github.r0kas.controller.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.r0kas.model.data.HolderType;
import com.github.r0kas.model.data.MemoryDataStore;
import com.github.r0kas.model.data.User;
import com.github.r0kas.model.rest.RequestUser;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryUserServiceTest {

  private UserService userService;
  private MemoryDataStore dataStore;
  private String name;
  private String address;
  private String country;
  private HolderType userType;

  @BeforeEach
  void init() {
    userService = new InMemoryDataService();
    dataStore = MemoryDataStore.getInstance();
    name = "John Wick";
    address = "Vilnius st. 1";
    country = "LT";
    userType = HolderType.PERSONAL;
  }

  @AfterEach
  void cleanUp() {
    userService = null;
  }

  @Test
  void createUser_withValidInputs() {
    UUID testUserID = userService.createUser(name, address, country, userType);
    User testUser = dataStore.getUser(testUserID);

    assertEquals(name, testUser.name());
    assertEquals(address, testUser.address());
    assertEquals(country, testUser.countryCode());
    assertEquals(userType, testUser.type());
    assertNotNull(testUser.ownedAccounts());
  }

  @Test
  void createUser_withInvalidName_shouldThrow() {
    assertThrows(IllegalArgumentException.class,
        () -> userService.createUser("", address, country, userType));
  }

  @Test
  void createUser_withInvalidAddress_shouldThrow() {
    assertThrows(IllegalArgumentException.class,
        () -> userService.createUser(name, "", country, userType));
  }

  @Test
  void createUser_withInvalidCountry_shouldThrow() {
    assertThrows(IllegalArgumentException.class,
        () -> userService.createUser(name, address, "country", userType));
  }

  @Test
  void createUser_withInvalidType_shouldThrow() {
    assertThrows(IllegalArgumentException.class,
        () -> userService.createUser(name, address, country, null));
  }

  @Test
  void getUser_withValidInputs() {
    User expectedUser = new User(name, address, country, userType);
    dataStore.setUser(expectedUser);

    User testUser = userService.getUser(expectedUser.id());

    assertEquals(expectedUser, testUser);
  }

  @Test
  void getUser_withInvalidID_shouldThrow() {
    assertThrows(NoSuchElementException.class, () -> userService.getUser(UUID.randomUUID()));
  }

  @Test
  void updateUser_withValidInputs() {
    User originalUser = new User(name, address, country, userType);
    dataStore.setUser(originalUser);

    String newName = "Robert";
    String newAddress = "Wall st. 1";
    String newCountry = "DE";
    RequestUser data = new RequestUser(newName, newAddress, newCountry, HolderType.BUSINESS);

    User modifiedUser = userService.updateUser(originalUser.id(), data);

    assertEquals(newName, modifiedUser.name());
    assertEquals(newAddress, modifiedUser.address());
    assertEquals(newCountry, modifiedUser.countryCode());
    assertEquals(HolderType.BUSINESS, modifiedUser.type());
    assertNotEquals(originalUser.createdOn(), modifiedUser.updatedOn());
  }

  @Test
  void updateUser_withModifiedName_shouldReturnUserWithNewName() {
    User originalUser = new User(name, address, country, userType);
    dataStore.setUser(originalUser);

    String newName = "Robert";
    RequestUser data = new RequestUser(newName, null, null, null);

    User modifiedUser = userService.updateUser(originalUser.id(), data);

    assertEquals(newName, modifiedUser.name());
    assertEquals(originalUser.address(), modifiedUser.address());
    assertEquals(originalUser.countryCode(), modifiedUser.countryCode());
    assertEquals(originalUser.type(), modifiedUser.type());
    assertNotEquals(originalUser.createdOn(), modifiedUser.updatedOn());
  }

  @Test
  void updateUser_withModifiedAddress_shouldReturnUserWithNewAddress() {
    User originalUser = new User(name, address, country, userType);
    dataStore.setUser(originalUser);

    String newAddress = "Wall st. 1";
    RequestUser data = new RequestUser(null, newAddress, null, null);

    User modifiedUser = userService.updateUser(originalUser.id(), data);

    assertEquals(originalUser.name(), modifiedUser.name());
    assertEquals(newAddress, modifiedUser.address());
    assertEquals(originalUser.countryCode(), modifiedUser.countryCode());
    assertEquals(originalUser.type(), modifiedUser.type());
    assertNotEquals(originalUser.createdOn(), modifiedUser.updatedOn());
  }

  @Test
  void updateUser_withModifiedCountry_shouldReturnWithNewCountry() {
    User originalUser = new User(name, address, country, userType);
    dataStore.setUser(originalUser);

    String newCountry = "DE";
    RequestUser data = new RequestUser(null, null,  newCountry, null);

    User modifiedUser = userService.updateUser(originalUser.id(), data);

    assertEquals(originalUser.name(), modifiedUser.name());
    assertEquals(originalUser.address(), modifiedUser.address());
    assertEquals(newCountry, modifiedUser.countryCode());
    assertEquals(originalUser.type(), modifiedUser.type());
    assertNotEquals(originalUser.createdOn(), modifiedUser.updatedOn());
  }

  @Test
  void updateUser_withModifiedType_shouldReturnWithNewType() {
    User originalUser = new User(name, address, country, userType);
    dataStore.setUser(originalUser);

    RequestUser data = new RequestUser(null, null,  null, HolderType.BUSINESS);

    User modifiedUser = userService.updateUser(originalUser.id(), data);

    assertEquals(originalUser.name(), modifiedUser.name());
    assertEquals(originalUser.address(), modifiedUser.address());
    assertEquals(originalUser.countryCode(), modifiedUser.countryCode());
    assertEquals(HolderType.BUSINESS, modifiedUser.type());
    assertNotEquals(originalUser.createdOn(), modifiedUser.updatedOn());
  }

  @Test
  void updateUser_withNotExistentUser_shouldThrow() {
    RequestUser data = new RequestUser("", "", "", HolderType.BUSINESS);
    assertThrows(NoSuchElementException.class,
        () -> userService.updateUser(UUID.randomUUID(), data));
  }

  @Test
  void deleteAccount_withValidInputs() {
    UUID testUserID = userService.createUser(name, address, country, userType);
    userService.deleteUser(testUserID);
    assertThrows(NoSuchElementException.class, () -> userService.getUser(testUserID));
  }

  @Test
  void deleteAccount_withNotExistentID_shouldThrow() {
    assertThrows(NoSuchElementException.class, () -> userService.deleteUser(UUID.randomUUID()));
  }

  @Test
  void deleteAccount_withOwnedAccounts_shouldThrow() {
    User testUser = new User(name, address, country, userType);
    testUser.addOwnedAccount(UUID.randomUUID());
    dataStore.setUser(testUser);

    assertThrows(UnsupportedOperationException.class, () -> userService.deleteUser(testUser.id()));
  }
}
