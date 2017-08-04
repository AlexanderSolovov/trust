package com.dai.trust.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.BatchUpdateException;

/**
 * Exception utilities
 */
public final class ExceptionUtility {

    /**
     * Formats the stacktrace for an exception into a string
     *
     * @param t The throwable exception
     * @return The stacktrace of the exception formatted as a string
     */
    public static String getStackTraceAsString(Throwable t) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        return result.toString();
    }

    /**
     * Loops through the exception hierarchy to determine if it has the
     * specified cause
     *
     * @param t The throwable exception that has been caught
     * @param causeType The type of cause to check for.
     * @return true if the cause type exists in the exception hierarchy.
     */
    public static boolean hasCause(Throwable t, Class causeType) {
        return !(getCause(t, causeType) == null);
    }

    /**
     * Loops through the exception hierarchy to get the cause of the specified
     * type
     *
     * @param t The throwable exception that has been caught
     * @param causeType The type of cause to check for.
     * @return the exception of the specified type or null.
     */
    public static <T> T getCause(Throwable t, Class<T> causeType) {
        T result = null;
        if (t.getClass() == causeType) {
            result = (T) t;
        } else if (t.getCause() != null) {
            result = getCause(t.getCause(), causeType);
        }
        return result;
    }

    /**
     * Checks if exception is optimistic lock from the database.
     *
     * @param t Exception object.
     * @return
     */
    public static boolean isOptimisticLocking(Throwable t) {
        if (getStackTraceAsString(t).contains("row_has_different_version")) {
            return true;
        } else if (hasCause(t, BatchUpdateException.class)) {
            BatchUpdateException cause = getCause(t, BatchUpdateException.class);
            if (cause.getMessage().contains("row_has_different_version")) {
                return true;
            } else if (cause.getNextException() != null) {
                return isOptimisticLocking(cause.getNextException());
            }
        }
        return false;
    }
}
