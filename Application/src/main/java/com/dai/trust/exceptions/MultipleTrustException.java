package com.dai.trust.exceptions;

import com.dai.trust.common.MessagesKeys;
import java.util.ArrayList;
import java.util.List;

/**
 * This exception is a container for multiple {@link TrustException}s.
 */
public class MultipleTrustException extends TrustException {
    List<TrustException> errors;
    
    public MultipleTrustException() {
        super(MessagesKeys.ERR_LIST_HEADER);
        errors = new ArrayList<>();
    }

    public List<TrustException> getErrors() {
        return errors;
    }

    public void setErrors(List<TrustException> errors) {
        this.errors = errors;
    }
    
    /** 
     * Adds error into the list of errors
     * @param error Error to add
     */
    public void addError(TrustException error){
        if(error != null && errors != null){
            errors.add(error);
        }
    }
}
