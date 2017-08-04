package com.dai.trust.ws.filters;

import com.dai.trust.common.MessageProvider;
import com.dai.trust.common.MessagesKeys;
import java.io.IOException;
import java.util.List;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 * REST filter for checking user authentication
 */
@Provider
@Authenticated
@Priority(Priorities.AUTHENTICATION - 1)
public class AuthenticatedFilter implements ContainerRequestFilter {

    @Context
    protected UriInfo uriInfo;
    protected boolean authenticated = false;
    protected final String RESPONSE_MESSAGE = "{\"message\":\"%s\"}";
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        final SecurityContext securityContext = requestContext.getSecurityContext();
        authenticated = true;
        
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            String langCode = getLanguageCode();
            MessageProvider msgProvider = new MessageProvider(langCode);
            authenticated = false;
            requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("[" + String.format(RESPONSE_MESSAGE, msgProvider.getMessage(MessagesKeys.ERR_NOT_AUTHENTICATED, null)) + "]")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8")
                    .build());
        }
    }

    protected String getLanguageCode() {
        if (uriInfo.getPathParameters() != null && uriInfo.getPathParameters().containsKey("langCode")) {
            List<String> val = uriInfo.getPathParameters().get("langCode");
            if (val != null && val.size() > 0) {
                return val.get(0);
            }
        }
        // Return English by default if no language code found
        return "en";
    }
}
