package com.dai.trust.services.document;

import com.dai.trust.common.DateUtility;
import com.dai.trust.common.FileUtility;
import com.dai.trust.common.MessagesKeys;
import com.dai.trust.common.SharedData;
import com.dai.trust.common.StringUtility;
import com.dai.trust.exceptions.MultipleTrustException;
import com.dai.trust.exceptions.TrustException;
import com.dai.trust.models.document.Document;
import com.dai.trust.models.document.FileInfo;
import com.dai.trust.models.system.Setting;
import com.dai.trust.services.AbstractService;
import com.dai.trust.services.system.SettingsService;
import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

/**
 * Contains methods, related to managing documents and files.
 */
public class DocumentService extends AbstractService {

    private static final Logger logger = LogManager.getLogger(DocumentService.class.getName());

    public DocumentService() {
        super();
    }

    /**
     * Returns file by FileInfo.
     *
     * @param fileInfo File information object
     * @return
     */
    public File getFile(FileInfo fileInfo) {
        if (fileInfo != null) {
            // Get media path
            SettingsService settingsService = new SettingsService();
            File f = new File(settingsService.getMediaPath() + "/" + fileInfo.getFilePath());
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }

    /**
     * Returns file by id.
     *
     * @param id File id
     * @return
     */
    public FileInfo getFileInfo(String id) {
        return getEM().find(FileInfo.class, id);
    }

    /**
     * Validates document
     *
     * @param doc Document to validate
     * @return
     */
    public boolean validateDocument(Document doc) {
        if (doc == null) {
            return true;
        }

        // Check doc type
        if (StringUtility.isEmpty(doc.getTypeCode())) {
            throw new TrustException(MessagesKeys.ERR_DOC_EMPTY_TYPE);
        }

        // Document is new, check file is provided
        if (StringUtility.isEmpty(doc.getFileId())) {
            throw new TrustException(MessagesKeys.ERR_DOC_EMPTY_FILE);
        }
        
        // Get document from db
        Document dbDoc = null;
        if (!StringUtility.isEmpty(doc.getId())) {
            dbDoc = getById(Document.class, doc.getId(), false);
        }

        // Document exists, check it's not involved in any approved applications or rights. If yes, throw error
        if (dbDoc != null) {
            // Check document has changes
            if (!StringUtility.empty(dbDoc.getFileId()).equals(StringUtility.empty(doc.getFileId()))
                    || !StringUtility.empty(dbDoc.getAuthority()).equals(StringUtility.empty(doc.getAuthority()))
                    || !StringUtility.empty(dbDoc.getDescription()).equals(StringUtility.empty(doc.getDescription()))
                    || !StringUtility.empty(dbDoc.getRefNumber()).equals(StringUtility.empty(doc.getRefNumber()))
                    || !StringUtility.empty(dbDoc.getTypeCode()).equals(StringUtility.empty(doc.getTypeCode()))
                    || !DateUtility.areEqual(dbDoc.getDocDate(), doc.getDocDate())
                    || !DateUtility.areEqual(dbDoc.getExpiryDate(), doc.getExpiryDate())
                    || !Objects.equals(dbDoc.getVersion(), doc.getVersion())) {
                // Check document is a part of approved application, registered right or historic parties
                int count = (Integer) getEM().createNativeQuery(
                        "select cast((select count(1) as cnt from application_document ad inner join application a on ad.app_id = a.id where ad.document_id = ?1 and a.status_code != 'pending') +"
                        + "(select count(1) as cnt from party_document pd inner join party p on pd.party_id = p.id where pd.document_id = ?1 and p.status_code = 'historic') +"
                        + "(select count(1) as cnt from rrr_document rd inner join rrr r on rd.rrr_id = r.id where rd.document_id = ?1 and r.status_code != 'pending') as int) as cnt"
                ).setParameter(1, dbDoc.getId()).getSingleResult();

                if (count > 0) {
                    throw new TrustException(MessagesKeys.ERR_DOC_READ_ONLY);
                }
            }
        }
        return true;
    }

    /**
     * Saves file and returns file info for further use with documents or party
     *
     * @param fileStream File stream to save
     * @param fileDetails File details
     * @param body Form data body for the file
     * @return Returns file information object.
     */
    public FileInfo saveFile(InputStream fileStream, FormDataContentDisposition fileDetails, FormDataBodyPart body) {
        // Make validations
        MultipleTrustException errors = new MultipleTrustException();
        SettingsService settingsServeice = new SettingsService();
        int maxFileSize = -1;

        if (fileStream == null || fileDetails == null) {
            errors.addError(new TrustException(MessagesKeys.ERR_FILE_EMPTY));
        } else {
            Setting settingFileSize = settingsServeice.getSetting(Setting.SETTING_MAX_FILE_SIZE);

            // File size
            if (settingFileSize != null) {
                try {
                    maxFileSize = Integer.parseInt(settingFileSize.getVal()) * 1024;
                } catch (Exception e) {
                }
            }

            // File extension
            Setting settingFileExt = settingsServeice.getSetting(Setting.SETTING_FILE_EXTENSIONS);
            if (settingFileExt != null) {
                String[] extensions = settingFileExt.getVal().split(",");
                if (extensions != null && extensions.length > 0) {
                    boolean allowed = false;
                    for (String ext : extensions) {
                        String fileName = fileDetails.getFileName();
                        if (ext.replace(" ", "").equalsIgnoreCase(FileUtility.getFileExtension(fileName))) {
                            allowed = true;
                            break;
                        }
                    }
                    if (!allowed) {
                        errors.addError(new TrustException(MessagesKeys.ERR_FILE_RESTRICTED_TYPE, new Object[]{settingFileExt.getVal()}));
                    }
                }
            }
        }

        // Get path
        String mediaPath = settingsServeice.getMediaPath();
        if (StringUtility.isEmpty(mediaPath)) {
            errors.addError(new TrustException(MessagesKeys.ERR_MEDIA_PATH_NOT_FOUND));
        }

        if (errors.getErrors().size() > 0) {
            throw errors;
        }

        // Save to disk
        Calendar cal = Calendar.getInstance();
        String subfolder = Integer.toString(cal.get(Calendar.YEAR)) + "/" + Integer.toString(cal.get(Calendar.MONTH) + 1);
        String fileId = UUID.randomUUID().toString();
        String originalFileName = fileDetails.getFileName();
        String fileExtension = FileUtility.getFileExtension(originalFileName);
        String filePath = subfolder + "/" + fileId + "." + fileExtension;
        File fileFolder = new File(mediaPath + "/" + subfolder);

        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }

        File file = new File(mediaPath + "/" + filePath);

        try {
            FileUtility.writeFile(fileStream, file, maxFileSize);
        } catch (Exception e) {
            logger.error(e);
            if (!(e instanceof TrustException)) {
                throw new TrustException(MessagesKeys.ERR_FILE_FAILED_SAVING, new Object[]{originalFileName});
            }
            throw (TrustException)e;
        }

        // Save to db
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(fileId);
        fileInfo.setCreatedBy(SharedData.getUserName());
        fileInfo.setCreationTime(cal.getTime());
        fileInfo.setFilePath(filePath);
        fileInfo.setFileSize((int) file.length());
        fileInfo.setMediaType(body.getMediaType().toString());
        fileInfo.setOriginalFileName(originalFileName);

        EntityTransaction tx = null;
        try {
            tx = getEM().getTransaction();
            tx.begin();
            getEM().persist(fileInfo);
            tx.commit();
            return fileInfo;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            if (getEM().isOpen()) {
                getEM().close();
            }
            throw e;
        }
    }
}
