package com.dai.trust.filters;

import com.dai.trust.common.SharedData;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet filter to initialize shared data storage and populate it with initial
 * values, such as current user name and EntityManager.
 *
 * @see SharedData
 */
public class ContextFilter implements Filter {

    private FilterConfig filterConfig = null;

    public ContextFilter() {
    }

    /**
     *
     * @param req The servlet request for processing
     * @param res The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Create local data storage and record user name and application url
        try (SharedData sharedData = SharedData.init()) {
            SharedData.set(SharedData.KEY_SESSION, request.getSession());
            SharedData.set(SharedData.KEY_USER_NAME, request.getRemoteUser());
            SharedData.set(SharedData.KEY_APP_URL, request.getRequestURL()
                    .substring(0, request.getRequestURL().length() - request.getRequestURI().length())
                    + request.getContextPath());
            SharedData.set(SharedData.KEY_APP_PATH, request.getServletContext().getRealPath("/"));
            chain.doFilter(request, response);
        }
    }

    /**
     * Init method for this filter
     *
     * @param filterConfig Filter configuration
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("ContextFilter()");
        }
        StringBuilder sb = new StringBuilder("ContextFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    @Override
    public void destroy() {

    }
}
