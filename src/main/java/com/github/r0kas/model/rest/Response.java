package com.github.r0kas.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Response object for generic application's JSON responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

  private @JsonProperty("id") UUID id;
  private @JsonProperty("status") String status;
  private @JsonProperty("response") Object response;

  /**
   * Instantiates a new Response.
   *
   * @param id            the id
   * @param statusMessage the status message
   * @param response      the response
   */
  public Response(UUID id, String statusMessage, Object response) {
    this.id = id;
    this.status = statusMessage;
    this.response = response;
  }
}
