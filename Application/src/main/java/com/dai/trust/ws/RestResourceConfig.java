package com.dai.trust.ws;

import com.dai.trust.ws.filters.AuthenticatedFilter;
import com.dai.trust.ws.filters.AuthorizedFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * REST resource configuration
 */
public class RestResourceConfig extends ResourceConfig {

    public RestResourceConfig() {
        packages("com.dai.trust.ws");
        register(AuthenticatedFilter.class);
        register(AuthorizedFilter.class);
        register(MultiPartFeature.class);
    }
}
