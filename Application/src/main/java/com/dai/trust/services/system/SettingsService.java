package com.dai.trust.services.system;

import com.dai.trust.common.MessageProvider;
import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.SharedData;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.system.Setting;
import com.dai.trust.services.AbstractService;
import java.io.File;
import java.util.List;
import javax.servlet.http.HttpSession;

/**
 * Contains methods, related to managing system settings.
 */
public class SettingsService extends AbstractService {

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
     * Returns folder path, where all document files are stored.
     *
     * @return
     */
    public String getMediaPath() {
        if (SharedData.getSession() != null) {
            Object tmpPath = SharedData.getSession().getAttribute(Setting.SETTING_MEDIA_PATH);
            if (tmpPath != null && !StringUtility.isEmpty(tmpPath.toString())) {
                return tmpPath.toString();
            }
        }

        String mediaPath = SharedData.getAppPath() + "/../trust_files";
        Setting settgingPath = getSetting(Setting.SETTING_MEDIA_PATH);

        if (settgingPath != null) {
            if (new File(settgingPath.getVal()).isAbsolute()) {
                mediaPath = settgingPath.getVal();
            } else {
                mediaPath = SharedData.getAppPath() + "/" + settgingPath.getVal();
            }
        }

        // Save to session
        if (SharedData.getSession() != null) {
            SharedData.getSession().setAttribute(Setting.SETTING_MEDIA_PATH, mediaPath);
        }
        return mediaPath;
    }

    private String getMediaPathFromSetting() {
        Setting settgingPath = getSetting(Setting.SETTING_MEDIA_PATH);
        if (settgingPath != null) {
            return settgingPath.getVal();
        }
        return "";
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

        // Check in db
        Setting dbSetting = getSetting(setting.getId());
        if(dbSetting != null && dbSetting.isReadOnly()){
            errors.addError(new TrustException(MessagesKeys.ERR_SETTING_READONLY));
        }
        
        if (errors.getErrors().size() > 0) {
            throw errors;
        }

        return save(setting, true);
    }

    /** 
     * Checks application and database version. If they are not compatible, appropriate exception will be thrown. 
     * @param session HttpSession object to save db version for subsequent checks
     */
    public void verifyVersion(HttpSession session) {
        String dbVersion = (String) session.getAttribute(Setting.SETTING_VERSION);
        if (StringUtility.isEmpty(dbVersion)) {
            // Get system version from string values
            MessageProvider msgProvider = new MessageProvider(null);
            String systemVersion = msgProvider.getMessage(MessagesKeys.GENERAL_VERSION);
            SettingsService service = new SettingsService();
            Setting setting = service.getSetting(Setting.SETTING_VERSION);

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
            session.setAttribute(Setting.SETTING_VERSION, dbVersion);
        }
    }
}
