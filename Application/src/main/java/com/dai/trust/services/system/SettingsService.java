package com.dai.trust.services.system;

import com.dai.trust.common.MessageProvider;
import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.system.Setting;
import com.dai.trust.services.AbstractService;
import java.util.List;
import javax.servlet.http.HttpSession;

/**
 * Contains methods, related to managing system settings.
 */
public class SettingsService extends AbstractService {

    public final static String SETTING_VERSION = "version";

    public SettingsService() {
        super();
    }

    /**
     * Returns system settings.
     *
     * @return
     */
    public List<Setting> getSettings() {
        return getAll(Setting.class, "val");
    }

    /**
     * Returns system settings.
     *
     * @param id Setting id
     * @return
     */
    public Setting getSetting(String id) {
        return getById(Setting.class, id, false);
    }

    /**
     * Saves setting to the database
     *
     * @param setting Setting object to save
     * @return Returns saved setting
     */
    public Setting saveSetting(Setting setting) {
        // Make validations
        MultipleTrustException errors = new MultipleTrustException();

        if (StringUtility.isEmpty(setting.getId().trim())) {
            errors.addError(new TrustException(MessagesKeys.ERR_NAME_EMPTY));
        } else {
            setting.setId(setting.getId().trim());
        }

        if (StringUtility.isEmpty(setting.getVal())) {
            errors.addError(new TrustException(MessagesKeys.ERR_VALUE_EMPTY));
        }

        if (StringUtility.isEmpty(setting.getDescription())) {
            errors.addError(new TrustException(MessagesKeys.ERR_DESCRIPTION_EMPTY));
        }

        if (errors.getErrors().size() > 0) {
            throw errors;
        }

        return save(setting, true);
    }

    public void verifyVersion(HttpSession session) {
        String dbVersion = (String) session.getAttribute(SettingsService.SETTING_VERSION);
        if (StringUtility.isEmpty(dbVersion)) {
            // Get system version from string values
            MessageProvider msgProvider = new MessageProvider(null);
            String systemVersion = msgProvider.getMessage(MessagesKeys.GENERAL_VERSION);
            SettingsService service = new SettingsService();
            Setting setting = service.getSetting(SettingsService.SETTING_VERSION);

            if (setting != null) {
                // Compare versions
                dbVersion = setting.getVal();
                if (!StringUtility.empty(dbVersion).equalsIgnoreCase(StringUtility.empty(systemVersion))) {
                    throw new TrustException(MessagesKeys.ERR_VERSIONS_MISMATCH);
                }
            }

            if (StringUtility.isEmpty(dbVersion)) {
                if (!StringUtility.isEmpty(systemVersion)) {
                    // Set to system version
                    dbVersion = systemVersion;
                } else {
                    // Set to default 0.1
                    dbVersion = "0.1";
                }
            }
            session.setAttribute(SettingsService.SETTING_VERSION, dbVersion);
        }
    }
}
