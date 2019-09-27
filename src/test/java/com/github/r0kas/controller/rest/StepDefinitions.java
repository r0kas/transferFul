package com.github.r0kas.controller.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.r0kas.controller.data.AccountService;
import com.github.r0kas.controller.data.InMemoryDataService;
import com.github.r0kas.controller.data.UserService;
import com.google.common.base.Strings;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;
import java.rmi.server.ServerNotActiveException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class StepDefinitions {

  public static final String LOCAL_API_ENDPOINT = "http://localhost:4567";
  private CloseableHttpClient httpClient = HttpClients.createDefault();
  private ObjectMapper mapper = new ObjectMapper();
  private AccountService accountService;
  private UserService userService;
  private TransferFulService restService;
  private String requestJson;
  private CloseableHttpResponse response;
  private UUID ID;
  private UUID accountID_0;
  private UUID accountID_1;

  @Given("TransferFulApp is running")
  public void transferfulappIsRunning() {
    accountService = new InMemoryDataService();
    userService = new InMemoryDataService();
    if (Objects.isNull(restService)) {
      restService = new TransferFulService(mapper, accountService, userService);
    } else {
      restService.stop();
    }
    restService.start("");
  }

  @Given("DataStore is empty")
  public void datastoreIsEmpty() {
    accountService = null;
    userService = null;
  }

  @And("endpoint {word} returns {int} in next {int} seconds")
  public void endpointReturnsInTheNextSeconds(String endpoint, int statusCode, int seconds)
      throws InterruptedException, ServerNotActiveException {
    HttpGet get = new HttpGet(LOCAL_API_ENDPOINT + endpoint);
    for (int i = 0; i < seconds; i++) {
      try {
        response = httpClient.execute(get);
        if (response.getStatusLine().getStatusCode() == statusCode) {
          return;
        }
      } catch (Exception ignore) { }
      TimeUnit.SECONDS.sleep(1);
    }
    throw new ServerNotActiveException(endpoint + " did not return in " + seconds + " seconds");
  }

  @When("user has account request with {string}, {string}, {string}, {string}")
  public void userHasRequestWith(String name, String address, String country, String type)
      throws JsonProcessingException {
    ObjectNode json = mapper.createObjectNode()
        .put("name", Strings.emptyToNull(name))
        .put("address", Strings.emptyToNull(address))
        .put("country", Strings.emptyToNull(country))
        .put("type", Strings.emptyToNull(type));
    requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }

  @Then("user has deposit-withdraw request with saved accountID_0 and {float} and {string}")
  public void depositWithdrawRequestWithSavedID0(double amount, String currency)
      throws JsonProcessingException {
    ObjectNode json = mapper.createObjectNode()
        .put("accountId", accountID_0.toString())
        .put("currency", currency)
        .put("amount", Double.toString(amount));
    requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }

  @When("user has transfer request from accountID_0 to accountID_1 for {float}")
  public void userHasTransferRequestFromAccountID_ToAccountID_For(double amount)
      throws JsonProcessingException {
    ObjectNode json = mapper.createObjectNode()
        .put("source", accountID_0.toString())
        .put("target", accountID_1.toString())
        .put("amount", Double.toString(amount));
    requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    System.out.println(requestJson);
  }

  @Then("user has deposit-withdraw request with saved accountID_1 and {float} and {string}")
  public void depositWithdrawRequestWithSavedID1(double amount, String currency)
      throws JsonProcessingException {
    ObjectNode json = mapper.createObjectNode()
        .put("accountId", accountID_1.toString())
        .put("currency", currency)
        .put("amount", Double.toString(amount));
    requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }

  @Then("user has account request with saved ID and {string}")
  public void userHasAccountRequestWithSavedUserIDAnd(String currency)
      throws JsonProcessingException {
    ObjectNode json = mapper.createObjectNode()
        .put("holderId", ID.toString())
        .put("currency", currency);
    requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
  }


  @And("user sends POST request to {word} endpoint")
  public void userSendsPOSTRequestToUser(String endpoint) throws IOException {
    HttpPost post = new HttpPost(LOCAL_API_ENDPOINT + endpoint);
    post.setEntity(new StringEntity(requestJson));
    post.setHeader("Accept", "application/json");
    post.setHeader("Content-type", "application/json");
    response = httpClient.execute(post);
  }

  @Then("user sends DELETE request to {word} with ID")
  public void userSendsDELETERequestToUserWithUserID(String endpoint) throws IOException {
    HttpDelete delete = new HttpDelete(LOCAL_API_ENDPOINT + endpoint + "/" + ID.toString());
    response = httpClient.execute(delete);
  }

  @And("user sends PATCH request to {word} with ID")
  public void userSendsPATCHRequestToUserWithUserID(String endpoint) throws IOException {
    HttpPatch patch = new HttpPatch(LOCAL_API_ENDPOINT + endpoint + "/" + ID.toString());
    patch.setEntity(new StringEntity(requestJson));
    patch.setHeader("Accept", "application/json");
    patch.setHeader("Content-type", "application/json");
    response = httpClient.execute(patch);
  }

  @Then("user sends DELETE request to {word} with accountID_0")
  public void userSendsDELETERequestToAccountWithAccountID(String endpoint)
      throws IOException {
    HttpDelete delete =
        new HttpDelete(LOCAL_API_ENDPOINT + endpoint + "/" + accountID_0.toString());
    response = httpClient.execute(delete);
  }

  @Then("user sends GET request to {word} with accountID_0")
  public void userSendsGetRequestToWordWithAccountID0(String endpoint) throws IOException {
    HttpGet get = new HttpGet(LOCAL_API_ENDPOINT + endpoint + "/" + accountID_0.toString());
    response = httpClient.execute(get);
  }

  @Then("user sends GET request to {word} with accountID_1")
  public void userSendsGetRequestToWordWithAccountID1(String endpoint) throws IOException {
    HttpGet get = new HttpGet(LOCAL_API_ENDPOINT + endpoint + "/" + accountID_1.toString());
    response = httpClient.execute(get);
  }

  @Then("user sends GET request to {word} with ID")
  public void userSendsGETRequestToUserWithID(String endpoint) throws IOException {
    HttpGet get = new HttpGet(LOCAL_API_ENDPOINT + endpoint + "/" + ID.toString());
    response = httpClient.execute(get);
  }

  @And("response account data contains values: {string}, {string}, {string}, {string}")
  public void responseDataContainsValues(String name, String address, String country, String type)
      throws IOException {
    JsonNode json = mapper.readTree(response.getEntity().getContent());
    JsonNode data = json.with("response");
    assertEquals(name, data.get("name").asText());
    assertEquals(address, data.get("address").asText());
    assertEquals(country, data.get("countryCode").asText());
    assertEquals(type, data.get("type").asText());
  }

  @Given("user has random accountID_0")
  public void userHasRandomAccountID() {
    accountID_0 = UUID.randomUUID();
  }

  @And("user saves received ID")
  public void userSavesReceivedID() throws IOException {
    JsonNode json = mapper.readTree(response.getEntity().getContent());
    JsonNode data = json.with("response");
    ID = UUID.fromString(data.get("id").asText());
  }

  @And("user saves received accountID_0")
  public void userSavesReceivedAccountID0() throws IOException {
    JsonNode json = mapper.readTree(response.getEntity().getContent());
    JsonNode data = json.with("response");
    accountID_0 = UUID.fromString(data.get("id").asText());
  }
  @And("user saves received accountID_1")
  public void userSavesReceivedAccountID1() throws IOException {
    JsonNode json = mapper.readTree(response.getEntity().getContent());
    JsonNode data = json.with("response");
    accountID_1 = UUID.fromString(data.get("id").asText());
  }

  @When("user has random ID")
  public void userHasRandomUserID() {
    ID = UUID.randomUUID();
  }

  @Then("user receives response with {int}")
  public void userReceivesResponseWith(Integer status) {
    assertEquals(status, response.getStatusLine().getStatusCode());
  }

  @And("response message contains {string}")
  public void responseMessageContains(String message) throws IOException {
    JsonNode json = mapper.readTree(response.getEntity().getContent());
    assertTrue(json.get("response").asText().contains(message));
  }

  @And("response account balance is {float}")
  public void responseAccountBalanceIs(double amount) throws IOException {
    JsonNode json = mapper.readTree(response.getEntity().getContent());
    JsonNode data = json.with("response");
    assertEquals(Double.toString(amount), data.get("balance").asText());
  }

  @Given("account is created with {string} currency")
  public void accountIsCreatedWithCurrency(String currency) throws IOException {
    userHasRequestWith("John", "Vilnius st.", "LT", "Personal");
    userSendsPOSTRequestToUser("/user");
    userReceivesResponseWith(201);
    userSavesReceivedID();
    userHasAccountRequestWithSavedUserIDAnd(currency);
    userSendsPOSTRequestToUser("/account");
    userReceivesResponseWith(201);
  }

  @Then("response account data contains saved ID and {string}")
  public void responseAccountDataContainsSaveUserIDAnd(String currency)
      throws IOException {
    JsonNode json = mapper.readTree(response.getEntity().getContent());
    JsonNode data = json.with("response");
    assertEquals(ID.toString(), data.get("holderId").asText());
    assertEquals(currency, data.get("currency").asText());
  }
}
