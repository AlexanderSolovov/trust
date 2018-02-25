package com.dai.trust.services.report;

import com.dai.trust.common.StringUtility;
import com.dai.trust.models.document.FileInfo;
import com.dai.trust.models.report.DeceasedOwnerSummary;
import com.dai.trust.models.report.LegalEntitySummary;
import com.dai.trust.models.report.PersonWithRightSummary;
import com.dai.trust.models.report.PoiSummary;
import com.dai.trust.models.report.PropertySummary;
import com.dai.trust.models.report.RegistryBookRecord;
import com.dai.trust.models.search.PersonSearchResult;
import com.dai.trust.models.system.Setting;
import com.dai.trust.services.AbstractService;
import com.dai.trust.services.document.DocumentService;
import com.dai.trust.services.property.ParcelMapService;
import com.dai.trust.services.search.SearchService;
import com.dai.trust.services.system.SettingsService;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.persistence.Query;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Returns various reports
 */
public class ReportsService extends AbstractService {

    private static final Logger logger = LogManager.getLogger(ReportsService.class.getName());

    public ReportsService() {
    }

    /**
     * Returns {@link PropertySummary} object, containing full information on
     * the property, including rightholders and POIs.
     *
     * @param langCode Language code for localization
     * @param propId Property id
     * @return
     */
    public PropertySummary getPropertyInfo(String langCode, String propId) {
        // Make swahili forcibily
        langCode = "sw";

        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(PropertySummary.QUERY_SEARCH, PropertySummary.class);
        q.setParameter("langCode", langCode);
        q.setParameter(PropertySummary.PARAM_PROP_ID, propId);
        PropertySummary prop = (PropertySummary) q.getSingleResult();

        if (prop == null) {
            return null;
        }

        // Get rightholders
        q = getEM().createNativeQuery(PersonWithRightSummary.QUERY_SEARCH_BY_RIGHT, PersonWithRightSummary.class);
        q.setParameter("langCode", langCode);
        q.setParameter(PersonWithRightSummary.PARAM_RIGHT_ID, prop.getRightId());
        prop.setPersons(q.getResultList());

        q = getEM().createNativeQuery(LegalEntitySummary.QUERY_SEARCH_BY_RIGHT, LegalEntitySummary.class);
        q.setParameter("langCode", langCode);
        q.setParameter(LegalEntitySummary.PARAM_RIGHT_ID, prop.getRightId());
        prop.setLegalEntities(q.getResultList());

        // Search for representative
        if (prop.getLegalEntities() != null && prop.getLegalEntities().size() > 0) {
            SearchService searchService = new SearchService();
            List<PersonSearchResult> appPersons = searchService.searchPersonFromApplication(langCode, prop.getApplicationId());
            if (appPersons != null && appPersons.size() > 0) {
                for (LegalEntitySummary le : prop.getLegalEntities()) {
                    le.setRepresentative(appPersons.get(0));
                }
            }
        }

        // Get POIs
        q = getEM().createQuery("Select p From PoiSummary p Where p.rrrId = :rightId", PoiSummary.class);
        q.setParameter("rightId", prop.getRightId());
        prop.setPois(q.getResultList());

        // Get deceased person
        q = getEM().createQuery("Select d From DeceasedOwnerSummary d Where d.rrrId = :rightId", DeceasedOwnerSummary.class);
        q.setParameter("rightId", prop.getRightId());
        prop.setDeceasedPersons(q.getResultList());

        return prop;
    }

    /**
     * Returns list of {@link RegistryBookRecord}, containing information on the
     * property and rightholders.
     *
     * @param langCode Language code for localization
     * @param propId Property id
     * @return
     */
    public List<RegistryBookRecord> getRegistryBookRecordsForProp(String langCode, String propId) {
        // Make swahili forcibily
        langCode = "sw";

        if (StringUtility.isEmpty(langCode)) {
            langCode = "en";
        }

        Query q = getEM().createNativeQuery(RegistryBookRecord.QUERY_SEARCH_BY_PROP, RegistryBookRecord.class);
        q.setParameter("langCode", langCode);
        q.setParameter(RegistryBookRecord.PARAM_PROP_ID, propId);
        return q.getResultList();
    }

    /**
     * Returns adjudication form report
     *
     * @param langCode Language code for localization
     * @param propId Property code
     * @return
     */
    public JasperPrint getAdjudicationForms(String langCode, String propId) {
        try {
            PropertySummary prop = getPropertyInfo(langCode, propId);

            if (prop == null) {
                return null;
            }

            ParcelMapService parcelMap = new ParcelMapService();
            BufferedImage map = parcelMap.getParcelMap(prop.getParcelId());

            HashMap params = new HashMap();
            params.put("MAP_IMAGE", map);

            List<PersonWithRightSummary> l = prop.getPersonsForSignature();
            PropertySummary[] beans = new PropertySummary[]{prop};
            JRDataSource jds = new JRBeanArrayDataSource(beans);

            return JasperFillManager.fillReport(
                    ReportsService.class.getResourceAsStream("/reports/AdjudicationForm.jasper"),
                    params, jds);
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }
    }

    /**
     * Returns certificate (title) report
     *
     * @param langCode Language code for localization
     * @param propId Property code
     * @return
     */
    public JasperPrint getCertificate(String langCode, String propId) {
        try {
            PropertySummary prop = getPropertyInfo(langCode, propId);

            if (prop == null) {
                return null;
            }

            ParcelMapService parcelMap = new ParcelMapService();
            DocumentService docService = new DocumentService();
            SettingsService settingService = new SettingsService();

            // Add photo to private rightholders
            if (prop.getPersons() != null && prop.getPersons().size() > 0) {
                for (PersonWithRightSummary person : prop.getPersons()) {
                    if (!StringUtility.isEmpty(person.getPersonPhotoId())) {
                        try {
                            FileInfo fileInfo = docService.getFileInfo(person.getPersonPhotoId());
                            person.setPhoto(docService.getFile(fileInfo));
                        } catch (Exception e) {
                        }
                    }
                }
            }

            BufferedImage map = parcelMap.getParcelMap(prop.getParcelId());
            Setting officerNameSetting = settingService.getSetting(Setting.SETTING_DISTRICT_OFFICER);
            String officerName = "";
            if (officerNameSetting != null) {
                officerName = StringUtility.empty(officerNameSetting.getVal());
            }
            URL resource = ReportsService.class.getResource("/reports/CrroNonPersonSignature.jasper");

            HashMap params = new HashMap();
            params.put("MAP_IMAGE", map);
            params.put("DLO_OFFICER", officerName);
            params.put("SUBREPORT_PATH", Paths.get(resource.toURI()).toAbsolutePath().toString());
            params.put("IMG_BREKET", ReportsService.class.getResourceAsStream("/reports/images/breket.jpg"));

            PropertySummary[] beans = new PropertySummary[]{prop};
            JRDataSource jds = new JRBeanArrayDataSource(beans);

            return JasperFillManager.fillReport(
                    ReportsService.class.getResourceAsStream("/reports/Ccro.jasper"),
                    params, jds);
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }
    }
    
    /**
     * Returns transaction sheet report
     *
     * @param langCode Language code for localization
     * @param propId Property code
     * @return
     */
    public JasperPrint getTransactionSheet(String langCode, String propId) {
        try {
            List<RegistryBookRecord> records = getRegistryBookRecordsForProp(langCode, propId);

            if (records == null) {
                return null;
            }
            
            // Add dummy record to make empty table rows
            int ownersCount = 0;
            int size = records.size();
            int addedRows = 0;
            int totalRows = 17;

            for (int i = 0; i < size; i++) {
                RegistryBookRecord rb = records.get(i + addedRows);
                ownersCount += 1;
                
                if (i == size - 1 || !rb.getPropId().equals(records.get(i + addedRows + 1).getPropId())) {
                    if (totalRows > ownersCount) {
                        for (int j = 0; j < totalRows - ownersCount; j++) {
                            addedRows += 1;
                            RegistryBookRecord rbEmpty = new RegistryBookRecord();
                            rbEmpty.setPropId(rb.getPropId());
                            rbEmpty.setIsPrivate(true);
                            rbEmpty.setId(UUID.randomUUID().toString());
                            records.add(i + addedRows, rbEmpty);
                        }
                    }
                    ownersCount = 0;
                } 
            }

            HashMap params = new HashMap();
           
            RegistryBookRecord[] beans = records.toArray(new RegistryBookRecord[records.size()]);
            JRDataSource jds = new JRBeanArrayDataSource(beans);

            return JasperFillManager.fillReport(
                    ReportsService.class.getResourceAsStream("/reports/TransactionSheet.jasper"),
                    params, jds);
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }
    }
}
