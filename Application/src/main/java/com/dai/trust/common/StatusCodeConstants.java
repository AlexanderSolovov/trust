package com.dai.trust.common;

import java.io.Serializable;

/**
 * Holds the list of application status codes.
 */
public class StatusCodeConstants implements Serializable {
    public static final String ACTIVE = "active";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";
    public static final String CURRENT = "current";
    public static final String PENDING = "pending";
    public static final String HISTORIC = "historic";
    public static final String WITHDRAWN = "withdrawn";
}
