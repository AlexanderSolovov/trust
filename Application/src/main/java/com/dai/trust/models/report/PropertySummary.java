package com.dai.trust.models.report;

import com.dai.trust.common.StringUtility;
import com.dai.trust.models.refdata.OccupancyType;
import com.dai.trust.models.refdata.OwnerType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.Transient;

@Entity
public class PropertySummary implements Serializable {

    @Id
    private String id;

    @Column(name = "right_id")
    private String rightId;

    @Column(name = "prop_number")
    private String propNumber;

    @Column(name = "file_number")
    private String fileNumber;

    @Column(name = "parcel_id")
    private String parcelId;

    @Column(name = "uka")
    private String uka;

    @Column(name = "lodgement_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date applicationDate;

    @Column(name = "application_id")
    private String applicationId;

    @Column(name = "village_code")
    private String villageCode;

    @Column(name = "village_chairman")
    private String villageChairman;

    @Column(name = "village_executive_officer")
    private String villageExecutiveOfficer;

    @Column(name = "village_address")
    private String villageAddress;

    @Column(name = "village_name")
    private String villageName;

    @Column(name = "district_name")
    private String districtName;

    @Column
    private Double acres;

    @Column(name = "hamlet_name")
    private String hamletName;

    @Column(name = "existing_use")
    private String existingUse;

    @Column(name = "proposed_use")
    private String proposedUse;

    @Column(name = "land_type")
    private String landType;

    @Column(name = "neighbor_north")
    private String neighborNorth;

    @Column(name = "neighbor_south")
    private String neighborSouth;

    @Column(name = "neighbor_east")
    private String neighborEast;

    @Column(name = "neighbor_west")
    private String neighborWest;

    @Column
    private String witness1;

    @Column
    private String witness2;

    @Column
    private String adjudicator1;

    @Column
    private String adjudicator2;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "occupancy_type")
    private String occupancyType;

    @Column(name = "occupancy_type_code")
    private String occupancyTypeCode;

    @Column
    private Double duration;

    @Column(name = "reg_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date regDate;

    @Column(name = "start_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;

    @Column(name = "folio_number")
    private String folioNumber;

    @Column(name = "allocation_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date allocationDate;

    @Column(name = "annual_fee")
    private Double annualFee;

    @Column(name = "right_type")
    private String rightType;

    @Transient
    private List<PersonWithRightSummary> persons;

    @Transient
    private List<LegalEntitySummary> legalEntities;

    @Transient
    private List<PoiSummary> pois;

    @Transient
    private List<DeceasedOwnerSummary> deceasedPersons;

    public static final String PARAM_PROP_ID = "propId";

    private static final String SEARCH_SELECT = "SELECT "
            + "p.id, "
            + "p.prop_number, "
            + "p.file_number, "
            + "p.parcel_id, "
            + "pl.uka, "
            + "a.lodgement_date, "
            + "a.id AS application_id, "
            + "v.code AS village_code, "
            + "v.chairman AS village_chairman, "
            + "v.executive_officer AS village_executive_officer, "
            + "v.address AS village_address, "
            + "get_translation(d.val, null) AS district_name, "
            + "get_translation(v.val, :langCode) AS village_name, "
            + "round(cast(st_area(st_transform(pl.geom, 32736)) * cast(0.000247105 as double precision) as numeric), 3) AS acres, "
            + "get_translation(h.val, :langCode) AS hamlet_name, "
            + "get_translation(dlu.val, :langCode) AS existing_use, "
            + "get_translation(alu.val, :langCode) AS proposed_use, "
            + "get_translation(lt.val, :langCode) AS land_type, "
            + "r.id as right_id, "
            + "r.neighbor_north, "
            + "r.neighbor_south, "
            + "r.neighbor_east, "
            + "r.neighbor_west, "
            + "r.adjudicator1, "
            + "r.adjudicator2, "
            + "r.witness1, "
            + "r.witness2, "
            + "r.status_code, "
            + "get_translation(rt.val, :langCode) AS right_type, "
            + "get_translation(ot.val, :langCode) AS occupancy_type, "
            + "ot.code AS occupancy_type_code, "
            + "r.duration, "
            + "r.reg_date, "
            + "r.start_date, "
            + "r.folio_number, "
            + "r.allocation_date, "
            + "r.annual_fee, "
            + "(CASE WHEN r.status_code = 'pending' THEN 1 ELSE 2 END) AS status_order "
            + "FROM public.property p  "
            + " INNER JOIN public.rrr r ON p.id = r.property_id "
            + " INNER JOIN public.application a ON r.application_id = a.id "
            + " INNER JOIN public.ref_right_type rt ON r.right_type_code = rt.code "
            + " LEFT JOIN public.ref_occupancy_type ot ON r.occupancy_type_code = ot.code "
            + " LEFT JOIN public.ref_landuse dlu ON r.declared_landuse_code = dlu.code "
            + " LEFT JOIN public.ref_landuse alu ON r.approved_landuse_code = alu.code "
            + " INNER JOIN public.parcel pl ON p.parcel_id = pl.id "
            + " LEFT JOIN public.ref_hamlet h ON pl.hamlet_code = h.code "
            + " LEFT JOIN public.ref_village v ON h.village_code = v.code "
            + " LEFT JOIN public.ref_district d ON v.district_code = d.code "
            + " LEFT JOIN public.ref_land_type lt ON pl.land_type_code = lt.code ";

    public static final String QUERY_SEARCH = SEARCH_SELECT
            + " WHERE p.id = :" + PARAM_PROP_ID + " and (r.status_code = 'pending' OR r.status_code = 'current') and rt.right_type_group_code = 'ownership' "
            + "ORDER BY status_order "
            + "LIMIT 1;";

    public PropertySummary() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRightId() {
        return rightId;
    }

    public void setRightId(String rightId) {
        this.rightId = rightId;
    }

    public String getPropNumber() {
        return propNumber;
    }

    public void setPropNumber(String propNumber) {
        this.propNumber = propNumber;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public String getUka() {
        return uka;
    }

    public void setUka(String uka) {
        this.uka = uka;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }

    public String getVillageChairman() {
        return villageChairman;
    }

    public void setVillageChairman(String villageChairman) {
        this.villageChairman = villageChairman;
    }

    public String getVillageExecutiveOfficer() {
        return villageExecutiveOfficer;
    }

    public void setVillageExecutiveOfficer(String villageExecutiveOfficer) {
        this.villageExecutiveOfficer = villageExecutiveOfficer;
    }

    public String getVillageAddress() {
        return villageAddress;
    }

    public void setVillageAddress(String villageAddress) {
        this.villageAddress = villageAddress;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Double getAcres() {
        return acres;
    }

    public void setAcres(Double acres) {
        this.acres = acres;
    }

    public String getHamletName() {
        return hamletName;
    }

    public void setHamletName(String hamletName) {
        this.hamletName = hamletName;
    }

    public String getExistingUse() {
        return existingUse;
    }

    public void setExistingUse(String existingUse) {
        this.existingUse = existingUse;
    }

    public String getProposedUse() {
        return proposedUse;
    }

    public void setProposedUse(String proposedUse) {
        this.proposedUse = proposedUse;
    }

    public String getLandType() {
        return landType;
    }

    public void setLandType(String landType) {
        this.landType = landType;
    }

    public String getNeighborNorth() {
        return neighborNorth;
    }

    public void setNeighborNorth(String neighborNorth) {
        this.neighborNorth = neighborNorth;
    }

    public String getNeighborSouth() {
        return neighborSouth;
    }

    public void setNeighborSouth(String neighborSouth) {
        this.neighborSouth = neighborSouth;
    }

    public String getNeighborEast() {
        return neighborEast;
    }

    public void setNeighborEast(String neighborEast) {
        this.neighborEast = neighborEast;
    }

    public String getNeighborWest() {
        return neighborWest;
    }

    public void setNeighborWest(String neighborWest) {
        this.neighborWest = neighborWest;
    }

    public String getAdjudicator1() {
        return adjudicator1;
    }

    public void setAdjudicator1(String adjudicator1) {
        this.adjudicator1 = adjudicator1;
    }

    public String getAdjudicator2() {
        return adjudicator2;
    }

    public void setAdjudicator2(String adjudicator2) {
        this.adjudicator2 = adjudicator2;
    }

    public String getWitness1() {
        return witness1;
    }

    public void setWitness1(String witness1) {
        this.witness1 = witness1;
    }

    public String getWitness2() {
        return witness2;
    }

    public void setWitness2(String witness2) {
        this.witness2 = witness2;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getOccupancyType() {
        return occupancyType;
    }

    public void setOccupancyType(String occupancyType) {
        this.occupancyType = occupancyType;
    }

    public String getOccupancyTypeCode() {
        return occupancyTypeCode;
    }

    public void setOccupancyTypeCode(String occupancyTypeCode) {
        this.occupancyTypeCode = occupancyTypeCode;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getFolioNumber() {
        return folioNumber;
    }

    public void setFolioNumber(String folioNumber) {
        this.folioNumber = folioNumber;
    }

    public Date getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(Date allocationDate) {
        this.allocationDate = allocationDate;
    }

    public Double getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(Double annualFee) {
        this.annualFee = annualFee;
    }

    public String getRightType() {
        return rightType;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    public List<PersonWithRightSummary> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonWithRightSummary> persons) {
        this.persons = persons;
    }

    public List<LegalEntitySummary> getLegalEntities() {
        return legalEntities;
    }

    public void setLegalEntities(List<LegalEntitySummary> legalEntities) {
        this.legalEntities = legalEntities;
    }

    public List<PoiSummary> getPois() {
        return pois;

    }

    public void setPois(List<PoiSummary> pois) {
        this.pois = pois;
    }

    public List<DeceasedOwnerSummary> getDeceasedPersons() {
        return deceasedPersons;
    }

    public void setDeceasedPersons(List<DeceasedOwnerSummary> deceasedPersons) {
        this.deceasedPersons = deceasedPersons;
    }

    public List<PersonWithRightSummary> getPersonsForSignature() {
        if (getPersons() == null || getPersons().size() < 1) {
            return new ArrayList<>();
        }
        
        if (getOccupancyTypeCode().equals(OccupancyType.TYPE_GUARDIAN)) {
            ArrayList<PersonWithRightSummary> guardians = new ArrayList<>();
            for (PersonWithRightSummary p : getPersons()) {
                if (p.getOwnerTypeCode() != null && p.getOwnerTypeCode().equals(OwnerType.TYPE_GUARDIAN)) {
                    guardians.add(p);
                }
            }
            return guardians;
        }
        return getPersons();
    }

    public String getOwnerNames() {
        if ((getPersons() == null || getPersons().size() < 1)
                && (getLegalEntities() == null || getLegalEntities().size() < 1)) {
            return "";
        }

        String names = "";
        String owners = "";
        String ownersWithShare = "";
        String guardians = "";
        String administrators = "";
        String deceased = "";

        // Find owners, guardians and admins
        if (getPersons() != null && getPersons().size() > 0) {
            for (int i = 0; i < getPersons().size(); i++) {

                PersonWithRightSummary person = getPersons().get(i);

                if (person.getOwnerTypeCode().equals(OwnerType.TYPE_OWNER)) {
                    String owner = "";
                    String share = StringUtility.empty(person.getShare());

                    if (!share.endsWith("%")) {
                        share = share + "%";
                    }

                    if (owners.length() > 0) {
                        // Check if last owner
                        if (i + 1 == getPersons().size() || !getPersons().get(i + 1).getOwnerTypeCode().equals(OwnerType.TYPE_OWNER)) {
                            owner = " na <b>" + person.getFullName() + "</b>";
                            owners = owners + owner;
                        } else {
                            owner = ", <b>" + person.getFullName() + "</b>";
                            owners = owners + owner;
                        }
                    } else {
                        owner = "<b>" + person.getFullName() + "</b>";
                        owners = owner;
                    }

                    ownersWithShare = ownersWithShare + owner + " (<b>" + share + "</b>)";
                } else if (person.getOwnerTypeCode().equals(OwnerType.TYPE_GUARDIAN)) {
                    if (guardians.length() > 0) {
                        guardians = guardians + ", <b>" + person.getFullName() + "</b>";
                    } else {
                        guardians = "<b>" + person.getFullName() + "</b>";
                    }
                } else if (person.getOwnerTypeCode().equals(OwnerType.TYPE_ADMINISTRATOR)) {
                    if (administrators.length() > 0) {
                        administrators = administrators + ", <b>" + person.getFullName() + "</b>";
                    } else {
                        administrators = "<b>" + person.getFullName() + "</b>";
                    }
                }
            }
        } else if (getLegalEntities() != null && getLegalEntities().size() > 0) {
            for (LegalEntitySummary nonPerson : getLegalEntities()) {
                if (owners.length() > 0) {
                    owners = owners + ", <b>" + StringUtility.empty(nonPerson.getName()) + "</b>";
                } else {
                    owners = "<b>" + StringUtility.empty(nonPerson.getName()) + "</b>";
                }
            }
        }

        // Find deceased persons
        if (getDeceasedPersons() != null && getDeceasedPersons().size() > 0) {
            for (DeceasedOwnerSummary dPerson : getDeceasedPersons()) {
                if (deceased.length() > 0) {
                    deceased = deceased + ", <b>" + dPerson.getFullName() + "</b>";
                } else {
                    deceased = "<b>" + dPerson.getFullName() + "</b>";
                }
            }
        }

        String resident = "Mkazi";
        String residents = "Wakazi";

        if (getHasNonResident()) {
            resident = "Sio mkazi";
            residents = "Sio Wakazi";
        }

        if (getOccupancyTypeCode().equals(OccupancyType.TYPE_SINGLE) || getOccupancyTypeCode().equals(OccupancyType.TYPE_NONNATURAL)) {
            names = owners + " (humu ndani akirejewa kama \"" + resident + "\")";
        } else if (getOccupancyTypeCode().equals(OccupancyType.TYPE_JOINT)) {
            names = owners + " kwa umiliki wa pamoja usio gawanyika (humu ndani wakirejewa kama \"" + residents + "\")";
        } else if (getOccupancyTypeCode().equals(OccupancyType.TYPE_COMMON)) {
            names = ownersWithShare + " kwa umiliki wa hisa (humu ndani wakirejewa kama \"" + residents + "\")";
        } else if (getOccupancyTypeCode().equals(OccupancyType.TYPE_GUARDIAN)) {
            names = guardians + " msimamizi mlezi wa " + owners + " (humu ndani akirejewa kama \"" + resident + "\")";
        } else if (getOccupancyTypeCode().equals(OccupancyType.TYPE_PROBATE)) {
            names = administrators + " ambaye ni msimamizi wa mirathi ya Marehemu " + deceased;
        }
        return names;
    }

    public boolean getHasNonResident() {
        if (getPersons() != null && getPersons().size() > 0) {
            for (PersonWithRightSummary person : getPersons()) {
                if (person.getVillageResident()) {
                    return false;
                }
            }
        }
        return true;
    }
}
