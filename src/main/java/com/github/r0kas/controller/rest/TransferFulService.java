package com.github.r0kas.controller.rest;

import static spark.Spark.awaitStop;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.patch;
import static spark.Spark.port;
import static spark.Spark.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.github.r0kas.controller.data.AccountService;
import com.github.r0kas.controller.data.UserService;
import com.github.r0kas.model.rest.RequestAccount;
import com.github.r0kas.model.rest.RequestDepositWithdraw;
import com.github.r0kas.model.rest.RequestTransfer;
import com.github.r0kas.model.rest.RequestUser;
import com.github.r0kas.model.rest.Response;
import com.google.common.base.Strings;
import com.sun.jdi.InvalidTypeException;
import java.util.NoSuchElementException;
import java.util.UUID;
import javax.naming.InsufficientResourcesException;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TransferFul service expose rest endpoints representing application logic and functionality.
 */
public class TransferFulService {

  private static final Logger log = LoggerFactory.getLogger(TransferFulService.class);
  private static final int DEFAULT_SERVER_PORT = 4567;
  private ObjectMapper mapper;
  private AccountService accountService;
  private UserService userService;

  /**
   * Instantiates a new Transfer ful service.
   *
   * @param mapper         Jackson Object mapper instance
   * @param accountService Initialised account service instance
   * @param userService    Initialised user service instance
   */
  public TransferFulService(ObjectMapper mapper,
                            AccountService accountService,
                            UserService userService) {
    this.mapper = mapper;
    this.accountService = accountService;
    this.userService = userService;
  }

  /**
   * Start registers available service endpoints and starts web server.
   */
  public void start(String portArgument) {
    if (!Strings.isNullOrEmpty(portArgument)) {
      port(parsePort(portArgument));
    }
    mapExceptions();
    initAccountEndpoints();
    initUserEndpoints();
    initManagementEndpoints();
  }

  /**
   * Stop gracefully shutdowns rest service.
   */
  public void stop() {
    awaitStop();
  }

  private int parsePort(String port) {
    try {
      int serverPort = Integer.parseInt(port);
      if (1024 < serverPort && serverPort < 65535) {
        return serverPort;
      } else {
        log.error("provided port number must be between 1024 and 65535");
      }
    } catch (Exception e) {
      log.error("invalid server port argument: " + port);
    }
    return DEFAULT_SERVER_PORT;
  }

  private void initManagementEndpoints() {

    get("/health", (request, response) -> {
      response.status(HttpStatus.OK_200);
      return "{\"status\": \"up\"}";
    });
  }

  private void initAccountEndpoints() {

    post("/account", (request, response) -> {
      RequestAccount data = mapper.readValue(request.body(), RequestAccount.class);
      UUID accountId = accountService.createAccount(
          data.getHolderId(),
          data.getCurrency());
      response.type("application/json");
      response.status(HttpStatus.CREATED_201);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.CREATED_201,
          accountService.getAccount(accountId));
    });

    get("/account/:id", (request, response) -> {
      UUID accountId = UUID.fromString(request.params(":id"));
      response.type("application/json");
      response.status(HttpStatus.OK_200);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.OK_200,
          accountService.getAccount(accountId));
    });

    delete("/account/:id", (request, response) -> {
      UUID accountId = UUID.fromString(request.params(":id"));
      accountService.deleteAccount(accountId);
      response.status(HttpStatus.OK_200);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.OK_200,
          "deleted account with id: " + accountId.toString());
    });

    post("/account/deposit", (request, response) -> {
      RequestDepositWithdraw data = mapper.readValue(request.body(), RequestDepositWithdraw.class);
      accountService.deposit(data.getAccountId(),
                             data.getAmount(),
                             data.getCurrency());
      response.type("application/json");
      response.status(HttpStatus.OK_200);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.OK_200,
          "deposit successful to account with id: " + data.getAccountId().toString());
    });

    post("/account/withdraw", (request, response) -> {
      RequestDepositWithdraw data = mapper.readValue(request.body(), RequestDepositWithdraw.class);
      accountService.withdraw(data.getAccountId(),
                              data.getAmount(),
                              data.getCurrency());
      response.type("application/json");
      response.status(HttpStatus.OK_200);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.OK_200,
          "withdraw successful from account with id: " + data.getAccountId().toString());
    });

    post("/account/transfer", (request, response) -> {
      RequestTransfer data = mapper.readValue(request.body(), RequestTransfer.class);
      accountService.transfer(data.getSourceAccountId(),
                              data.getTargetAccountId(),
                              data.getAmount());
      response.type("application/json");
      response.status(HttpStatus.OK_200);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.OK_200,
          "transfer successful from account with id: " + data.getSourceAccountId().toString()
      + " to account with id: " + data.getTargetAccountId().toString());
    });
  }

  private void initUserEndpoints() {

    post("/user", (request, response) -> {
      RequestUser data = mapper.readValue(request.body(), RequestUser.class);
      UUID userID = userService.createUser(data.name(),
                                           data.address(),
                                           data.countryCode(),
                                           data.type());
      response.type("application/json");
      response.status(HttpStatus.CREATED_201);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.CREATED_201,
          userService.getUser(userID));
    });

    patch("/user/:id", (request, response) -> {
      UUID userId = UUID.fromString(request.params(":id"));
      RequestUser data = mapper.readValue(request.body(), RequestUser.class);
      response.type("application/json");
      response.status(HttpStatus.OK_200);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.OK_200,
          userService.updateUser(userId, data));
    });

    get("/user/:id", (request, response) -> {
      UUID userId = UUID.fromString(request.params(":id"));
      response.type("application/json");
      response.status(HttpStatus.OK_200);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.OK_200,
          userService.getUser(userId));
    });

    delete("/user/:id", (request, response) -> {
      UUID userId = UUID.fromString(request.params(":id"));
      userService.deleteUser(userId);
      response.status(HttpStatus.OK_200);
      return jsonResponse(UUID.randomUUID(),
          HttpStatus.OK_200,
          "deleted user with id: " + userId.toString());
    });
  }

  private void mapExceptions() {

    exception(IllegalArgumentException.class, (exception, request, response) -> {
      UUID reqId = UUID.randomUUID();
      log.error(exception.toString() + " : " + reqId);
      response.status(HttpStatus.BAD_REQUEST_400);
      response.body(
          jsonResponse(reqId, HttpStatus.BAD_REQUEST_400, exception.getMessage()));
    });

    exception(InvalidFormatException.class, (exception, request, response) -> {
      UUID reqId = UUID.randomUUID();
      log.error(exception.toString() + " : " + reqId);
      response.status(HttpStatus.BAD_REQUEST_400);
      response.body(
          jsonResponse(reqId, HttpStatus.BAD_REQUEST_400, exception.getMessage()));
    });

    exception(NoSuchElementException.class, (exception, request, response) -> {
      UUID reqId = UUID.randomUUID();
      log.error(exception.toString() + " : " + reqId);
      response.status(HttpStatus.NOT_FOUND_404);
      response.body(
          jsonResponse(reqId,HttpStatus.NOT_FOUND_404, exception.getMessage()));
    });

    exception(UnsupportedOperationException.class, (exception, request, response) -> {
      UUID reqId = UUID.randomUUID();
      log.error(exception.toString() + " : " + reqId);
      response.status(HttpStatus.CONFLICT_409);
      response.body(
          jsonResponse(reqId,
              HttpStatus.CONFLICT_409,
              "cannot delete. Resource has linked objects"));
    });

    exception(InvalidTypeException.class, (exception, request, response) -> {
      UUID reqId = UUID.randomUUID();
      log.error(exception.toString() + " : " + reqId);
      response.status(HttpStatus.CONFLICT_409);
      response.body(
          jsonResponse(reqId,
              HttpStatus.CONFLICT_409,
              "cannot complete. Incompatible currency"));
    });

    exception(InsufficientResourcesException.class, (exception, request, response) -> {
      UUID reqId = UUID.randomUUID();
      log.error(exception.toString() + " : " + reqId);
      response.status(HttpStatus.PAYMENT_REQUIRED_402);
      response.body(
          jsonResponse(reqId,
              HttpStatus.PAYMENT_REQUIRED_402,
              "insufficient funds in source account"));
    });
  }

  private String jsonResponse(UUID id, int status, Object data) {

    log.info("Sending response for request {}", id.toString());
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
          new Response(id, HttpStatus.getMessage(status), data));
    } catch (JsonProcessingException e) {
      log.error("THIS" + e.toString());
      return "{\"id\":" + id + ",\"status\":\"Server Error\"}";
    }
  }
}
