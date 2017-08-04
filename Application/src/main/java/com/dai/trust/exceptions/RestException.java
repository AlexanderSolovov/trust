package com.dai.trust.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Exception object, used for returning exceptions to the REST clients
 */
public class RestException extends WebApplicationException {

    public RestException(int statusCode, String message) {
        super(Response.status(statusCode)
                .entity(message)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8")
                .build());
    }
}
