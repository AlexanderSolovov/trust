package com.dai.trust.common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Contains helping string methods
 */
public class StringUtility {

    /**
     * Checks string for null or empty value and returns true if any of them.
     *
     * @param value String value to check
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Returns empty string if provided value is null, otherwise the value
     * itself will be returned.
     *
     * @param value String value to check
     */
    public static String empty(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    /**
     * Calculates MD5 for provided string and returns result as string
     * @param input Input string to which MD5 should be calculated
     * @return 
     */
    public static String getMD5(String input) {
        if(StringUtility.isEmpty(input)){
            return null;
        }
        return getMD5(input.getBytes());
    }
    
    /**
     * Calculates MD5 for provided bytes array and returns result as string
     * @param bytes Bytes array to which MD5 should be calculated
     * @return 
     */
    public static String getMD5(byte[] bytes) {
        try {
            if(bytes == null || bytes.length < 1){
                return null;
            }
            
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(bytes);
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
            
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }
}
