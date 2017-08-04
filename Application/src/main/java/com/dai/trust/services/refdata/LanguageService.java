package com.dai.trust.services.refdata;

import com.dai.trust.common.StringUtility;
import com.dai.trust.models.refdata.Language;
import com.dai.trust.services.AbstractService;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Contains methods, related to managing languages.
 */
public class LanguageService extends AbstractService {

    private static final Logger logger = LogManager.getLogger(LanguageService.class.getName());

    private List<Language> languages;
    private String languageCode;
    public static final String LANG_SESSION = "langCode";
    public static final String LANG_PARAM = "language";
    public static final String LANG_COOKIE = "trust_language_code";
    public static final String LANG_LTR = "ltr";

    public LanguageService() {
        super();
    }

    public String getLANG_SESSION() {
        return LANG_SESSION;
    }

    public String getLANG_PARAM() {
        return LANG_PARAM;
    }

    public String getLANG_COOKIE() {
        return LANG_COOKIE;
    }

    public String getLANG_LTR() {
        return LANG_LTR;
    }

    /**
     * Returns list of available languages.
     *
     * @param langCode Language code to be used for localization of language
     * names
     * @param onlyActive Indicates that only active languages have to be
     * returned.
     * @return
     */
    public List<Language> getLanguages(String langCode, boolean onlyActive) {
        try {
            // Load langauge if list is null or langauge code is different from previously saved
            if (languages == null || !StringUtility.empty(langCode).equalsIgnoreCase(StringUtility.empty(this.languageCode))) {
                languages = new RefDataService().getRefDataRecords(Language.class, onlyActive, langCode);
                
                // Save langauge code
                if (!StringUtility.empty(langCode).equalsIgnoreCase(StringUtility.empty(this.languageCode))) {
                    this.languageCode = langCode;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to extract languages", e);
        }
        return languages;
    }

    /**
     * Returns language by code. Otherwise returns default one
     *
     * @param code Language code
     * @return
     */
    public Language verifyGetLanguage(String code) {
        Language defaultLanguage = null;
        if (getLanguages(code, true) != null) {
            for (Language lang : getLanguages(code, true)) {
                if (defaultLanguage == null && lang.getIsDefault()) {
                    defaultLanguage = lang;
                }
                if (StringUtility.empty(code).equalsIgnoreCase(lang.getCode())) {
                    return lang;
                }
            }
        }
        return defaultLanguage;
    }
}
