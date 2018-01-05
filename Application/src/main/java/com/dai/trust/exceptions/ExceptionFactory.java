package com.dai.trust.exceptions;

import com.dai.trust.common.MessageProvider;
import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.StringUtility;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * List of static methods to generate Open Tenure exceptions in JSON format.
 */
public class ExceptionFactory {

    private static final String RESPONSE_MESSAGE = "{\"message\":\"%s\"}";

    /**
     * Builds general REST exception.
     *
     * @param statusCode HTTP status code to assign to the response
     * @param errorCode Code of message from {@link ServiceMessage}
     * @param langCode Language code (e.g. en)
     * @param params List parameters to substitute in the message
     * @return
     */
    public static RestException buildGeneralException(
            int statusCode,
            String errorCode,
            String langCode,
            Object[] params) {
        MessageProvider msgProvider = new MessageProvider(langCode);
        return new RestException(
                statusCode, 
                "[" + String.format(RESPONSE_MESSAGE, msgProvider.getMessage(errorCode, params).replace("\"", "\\\"")) + "]"
        );
    }
    
    /**
     * Builds general REST exception.
     *
     * @param statusCode HTTP status code to assign to the response
     * @param errorCode Code of message from {@link ServiceMessage}
     * @param langCode Language code
     * @return
     */
    public static RestException buildGeneralException(
            int statusCode,
            String errorCode,
            String langCode) {
        return buildGeneralException(statusCode, errorCode, langCode, null);
    }

    /**
     * Builds general REST exception with HTTP 400 code.
     *
     * @param errorCode Code of message from {@link ServiceMessage}
     * @param langCode Language code
     * @return
     */
    public static RestException buildGeneralException(String errorCode, String langCode) {
        return buildGeneralException(Response.Status.BAD_REQUEST.getStatusCode(), errorCode, langCode);
    }

    /**
     * Builds general REST exception with HTTP 400 code.
     *
     * @param errorCode Code of message from {@link ServiceMessage}
     * @param langCode Language code
     * @param params List parameters to substitute in the message
     * @return
     */
    public static RestException buildGeneralException(
            String errorCode,
            String langCode,
            Object[] params) {
        return buildGeneralException(Response.Status.BAD_REQUEST.getStatusCode(), errorCode, langCode, params);
    }

    /**
     * Builds REST exception with multiple error messages. The status will be set to HTTP 400 code.
     *
     * @param <T> Exception type
     * @param errors List of exceptions
     * @param langCode Language code
     * @return
     */
    public static <T extends TrustException> RestException buildMultipleErrorsException(List<T> errors, String langCode) {
        String errorString = "";
        if(errors != null){
            MessageProvider msgProvider = new MessageProvider(langCode);
            for(T error : errors){
                if(StringUtility.isEmpty(errorString)){
                    errorString = String.format(RESPONSE_MESSAGE, msgProvider.getMessage(error.getMessage(), error.getMessageParameters()));
                } else {
                    errorString = errorString + "," + String.format(RESPONSE_MESSAGE, msgProvider.getMessage(error.getMessage(), error.getMessageParameters()));
                }
            }
        }
        return new RestException(Response.Status.BAD_REQUEST.getStatusCode(), "[" + errorString + "]");
    }
    
    /**
     * Builds general unexpected exception. HTTP 400 (BAD_REQUEST)
     *
     * @param langCode Language code
     * @see MessagesKeys.ERR_UNEXPECTED_ERROR
     * @return
     */
    public static RestException buildUnexpected(String langCode) {
        return buildGeneralException(Response.Status.BAD_REQUEST.getStatusCode(),
                MessagesKeys.ERR_UNEXPECTED_ERROR, langCode);
    }

    /**
     * Builds unauthorized exception with HTTP 401 code (Unauthorized).
     *
     * @param langCode Language code
     * @see MessagesKeys.ERR_NOT_AUTHENTICATED
     * @return
     */
    public static RestException buildUnauthorized(String langCode) {
        return buildGeneralException(Response.Status.UNAUTHORIZED.getStatusCode(),
                MessagesKeys.ERR_NOT_AUTHENTICATED, langCode);
    }

    /**
     * Builds unauthorized exception with HTTP 403 code (Forbidden).
     *
     * @param langCode Language code
     * @see MessagesKeys.ERR_INSUFFICIENT_RIGHTS
     * @return
     */
    public static RestException buildForbidden(String langCode) {
        return buildGeneralException(Response.Status.FORBIDDEN.getStatusCode(),
                MessagesKeys.ERR_INSUFFICIENT_RIGHTS, langCode);
    }
    
    /**
     * Builds JSON conversion exception with HTTP 400 code.
     *
     * @param langCode Language code
     * @param objectName Object name, which failed to convert
     * @see MessagesKeys.ERR_JSON_CONVERSION
     * @return
     */
    public static RestException buildBadJson(String langCode, String objectName) {
        return buildGeneralException(MessagesKeys.ERR_JSON_CONVERSION, langCode, new Object[]{objectName});
    }
}
