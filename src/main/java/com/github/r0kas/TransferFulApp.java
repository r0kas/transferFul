package com.github.r0kas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.r0kas.controller.data.AccountService;
import com.github.r0kas.controller.data.InMemoryDataService;
import com.github.r0kas.controller.data.UserService;
import com.github.r0kas.controller.rest.TransferFulService;

/**
 * TransferFul application exposes RESTFul api for funds related operations.
 */
public class TransferFulApp {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    AccountService accountService = new InMemoryDataService();
    UserService userService = new InMemoryDataService();

    TransferFulService restService = new TransferFulService(
        new ObjectMapper(), accountService, userService);

    String portArg = "";
    if (args.length > 0) {
      portArg = args[0];
    }
    restService.start(portArg);

    Runtime.getRuntime().addShutdownHook(new Thread(restService::stop));
  }

}
