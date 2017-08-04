package com.dai.trust.common;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides methods to extract messages from the bundle files
 */
public class MessageProvider implements Serializable {

    private ResourceBundle msgBundle;
    private Locale locale;
    
    /**
     * Class constructor
     *
     * @param langCode Language code to identify a proper bundle file.
     */
    public MessageProvider(String langCode) {
        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }
        
        if (langCode.contains("-")) {
            locale = new Locale(langCode.substring(0, langCode.indexOf("-")), langCode.substring(langCode.lastIndexOf("-") + 1, langCode.length()));
        } else {
            locale = new Locale(langCode);
        }
    }

    /**
     * Returns bundle with messages.
     *
     * @return
     */
    public ResourceBundle getBundle() {
        if (msgBundle == null) {
            msgBundle = java.util.ResourceBundle.getBundle("com/dai/trust/strings", locale);
        }
        return msgBundle;
    }

    /**
     * Returns message value of the provided bundle key. It will use language
     * code, defined in the class constructor.
     *
     * @param key Message key
     * @return
     */
    public String getMessage(String key) {
        try {
            return getBundle().getString(key);
        } catch (MissingResourceException e) {
            return "???" + key + "??? not found";
        }
    }

    /**
     * Returns message value of the provided bundle key. It will use language
     * code, defined in the class constructor.
     *
     * @param key Message key
     * @param params Parameters to insert into message
     * @return
     */
    public String getMessage(String key, Object[] params) {
        try {
            MessageFormat fmt = new MessageFormat(getBundle().getString(key), locale);
            return fmt.format(params);
        } catch (MissingResourceException e) {
            return "???" + key + "??? not found";
        }
    }
}
