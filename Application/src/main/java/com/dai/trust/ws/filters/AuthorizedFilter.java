package com.dai.trust.ws.filters;

import com.dai.trust.common.MessageProvider;
import com.dai.trust.common.MessagesKeys;
import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * REST filter for checking user authorization. It extends
 * {@link AuthenticatedFilter} and will check authentication first.
 */
@Provider
@Authorized(roles = {})
@Priority(Priorities.AUTHORIZATION - 1)
public class AuthorizedFilter extends AuthenticatedFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        super.filter(requestContext);
        
        if (authenticated) {
            // Check user roles
            Authorized a = null;
            if (resourceInfo.getResourceMethod() != null) {
                a = resourceInfo.getResourceMethod().getAnnotation(Authorized.class);
            } else if (resourceInfo.getResourceClass() != null) {
                a = resourceInfo.getResourceClass().getAnnotation(Authorized.class);
            }

            if (a != null) {
                if (a.roles() != null && a.roles().length > 0) {
                    for (String role : a.roles()) {
                        if (requestContext.getSecurityContext().isUserInRole(role)) {
                            return;
                        }
                    }
                    // User doesn't belong any role, throw exception
                    String langCode = getLanguageCode();
                    MessageProvider msgProvider = new MessageProvider(langCode);
                    requestContext.abortWith(Response
                            .status(Response.Status.FORBIDDEN)
                            .entity("[" + String.format(RESPONSE_MESSAGE, msgProvider.getMessage(MessagesKeys.ERR_INSUFFICIENT_RIGHTS, null)) + "]")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8")
                            .build());
                }
            }
        }
    }
}
