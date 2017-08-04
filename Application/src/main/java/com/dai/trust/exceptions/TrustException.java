package com.dai.trust.exceptions;

/**
 * Generic exception for all other business logic exceptions
 */
public class TrustException extends RuntimeException {

    private Object[] messageParameters;

    public Object[] getMessageParameters() {
        return messageParameters;
    }

    public void setMessageParameters(Object[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    public TrustException(String messageCode) {
        super(messageCode);
    }

    public TrustException(String messageCode, Object[] messageParameters) { 
        super(messageCode);
        this.messageParameters = messageParameters; 
    }

    public TrustException(String messageCode, Throwable cause) {
        super(messageCode, cause);
    }

    public TrustException(String messageCode, Object[] messageParameters, Throwable cause) { 
        super(messageCode, cause);
        this.messageParameters = messageParameters; 
    }

    @Override
    public String toString() {
        String result = super.toString();
        if (messageParameters != null) {
            int idx = 1;
            for (Object obj : messageParameters) {
                if (obj != null) {
                    result = result + ", Param" + idx + "=" + obj.toString();
                } else {
                    result = result + ", Param" + idx + "=null";
                }
                idx++;
            }
        }
        return result;
    }
}
