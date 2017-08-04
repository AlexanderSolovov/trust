package com.dai.trust.ws.responses;

import com.dai.trust.common.StringUtility;

/**
 * List of static methods to generate JSON responses.
 */
public class ResponseFactory {

    private static final String OK_MESSAGE = "\"result\":\"OK\"";

    /**
     * Builds general OK response.
     *
     * @return
     */
    public static final String buildOk() {
        return "{" + OK_MESSAGE + "}";
    }
    
    /**
     * Builds general response with result in a form {"result":"value"}.
     * @param value Value to embed into response.
     * @return
     */
    public static final String buildResultResponse(String value) {
        String response = "{\"result\":\"%s\"}";
        if(StringUtility.isEmpty(value)){
            return String.format(response, "");
        }
        return String.format(response, value);
    }
}
