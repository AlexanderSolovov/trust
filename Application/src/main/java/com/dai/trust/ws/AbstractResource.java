package com.dai.trust.ws;

import com.dai.trust.common.MessagesKeys;
import com.dai.trust.exceptions.ExceptionFactory;
import com.dai.trust.exceptions.ExceptionUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.RestException;
import com.dai.trust.exceptions.TrustException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Abstract class for REST resources/services */
public abstract class AbstractResource {
    
    private static final Logger logger = LogManager.getLogger(AbstractResource.class.getName());
    
    @Context
    private HttpServletRequest request;

    @Context
    private UriInfo uri;

    protected final String LANG_CODE = "langCode";
    protected final String UNLOCALIZED = "unlocalized";
    
    private ObjectMapper mapper;

    public AbstractResource() {
        super();
    }

    public String getLanguageCode() {
        return request.getLocale().toLanguageTag();
    }

    /**
     * Returns URI information.
     * @return 
     */
    public UriInfo getUriInfo() {
        return uri;
    }

    /**
     * Returns application URL
     * @return 
     */
    public String getApplicationUrl() {
        return request.getRequestURL().substring(0, request.getRequestURL().length() - request.getRequestURI().length()) + request.getContextPath();
    }

    /**
     * Returns request object.
     * @return 
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Returns {@link HttpSession} object object
     * @return 
     */
    public HttpSession getSession() {
        return getRequest().getSession();
    }

    /**
     * Returns Jackson JSON mapper
     *
     * @return
     */
    public ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return mapper;
    }

    /**
     * Process exception and returns back in appropriate format.
     *
     * @param t Throw exception
     * @param langCode Language code
     * @return
     */
    protected RestException processException(Exception t, String langCode) {
        try {
            logger.error(t);
            t.printStackTrace();
            // Identify the type of exception and raise the appropriate Service Fault
            if (t.getClass() == RestException.class) {
                return (RestException) t;
            } else if (ExceptionUtility.hasCause(t, MultipleTrustException.class)) {
                return ExceptionFactory.buildMultipleErrorsException(
                        ExceptionUtility.getCause(t, MultipleTrustException.class).getErrors(),
                        langCode);
            } else if (ExceptionUtility.hasCause(t, TrustException.class)) {
                TrustException ex = ExceptionUtility.getCause(t, TrustException.class);
                return ExceptionFactory.buildGeneralException(ex.getMessage(), langCode, ex.getMessageParameters());
            } else if(ExceptionUtility.isOptimisticLocking(t)){
                return ExceptionFactory.buildGeneralException(MessagesKeys.ERR_OPTIMISTIC_LOCK, langCode);
            } else {
                // Unhandled Exception.
                return ExceptionFactory.buildUnexpected(langCode);
            }
        } catch (Exception e) {
            logger.error(e);
            return ExceptionFactory.buildUnexpected(langCode);
        }
    }
}
