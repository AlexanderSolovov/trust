package com.dai.trust.models.report;

import com.dai.trust.common.StringUtility;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 * Record for registry book
 */
@Entity
public class RegistryBookRecord implements Serializable {

    @Id
    @Column
    private String id;

    @Column(name = "is_private")
    private boolean isPrivate;
    
    @Column(name = "prop_id")
    private String propId;

    @Column(name = "prop_number")
    private String propNumber;

    @Column(name = "file_number")
    private String fileNumber;

    @Column(name = "uka")
    private String uka;

    @Column
    private Double acres;

    @Column(name = "hamlet_name")
    private String hamletName;

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

    @Column(name = "right_id")
    private String rightId;

    @Column(name = "neighbor_north")
    private String neighborNorth;

    @Column(name = "neighbor_south")
    private String neighborSouth;

    @Column(name = "neighbor_east")
    private String neighborEast;

    @Column(name = "neighbor_west")
    private String neighborWest;

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

    @Column(name = "occupancy_type")
    private String occupancyType;

    @Column(name = "occupancy_type_code")
    private String occupancyTypeCode;

    @Column(name = "share_size")
    private String share;

    @Column(name = "owner_type_code")
    private String ownerTypeCode;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dob;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_number")
    private String idNumber;

    @Column
    private Integer age;

    @Column
    private String resident;

    @Column(name = "mobile_number")
    private String mobile;

    @Column(name = "is_resident")
    private Boolean villageResident;

    @Column
    private String address;

    @Column(name = "person_photo_id")
    private String personPhotoId;

    @Column
    private String gender;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column
    private String citizenship;

    public static final String PARAM_PROP_ID = "propId";

    public static final String QUERY_SEARCH_BY_PROP
            = "SELECT  "
            + "  p.id, "
            + "  p.is_private, "
            + "  prop.id AS prop_id, "
            + "  prop.prop_number, "
            + "  prop.file_number, "
            + "  pl.uka, "
            + "  round(cast(st_area(st_transform(pl.geom, 32736)) * cast(0.000247105 as double precision) as numeric), 3) AS acres, "
            + "  get_translation(h.val, :langCode) AS hamlet_name, "
            + "  v.chairman AS village_chairman, "
            + "  v.executive_officer AS village_executive_officer, "
            + "  v.address AS village_address, "
            + "  get_translation(v.val, :langCode) AS village_name, "
            + "  get_translation(d.val, :langCode) AS district_name, "
            + "  rh.rrr_id as right_id, "
            + "  r.neighbor_north, "
            + "  r.neighbor_south, "
            + "  r.neighbor_east, "
            + "  r.neighbor_west, "
            + "  r.reg_date, "
            + "  r.start_date, "
            + "  r.folio_number, "
            + "  r.allocation_date, "
            + "  get_translation(oct.val, :langCode) AS occupancy_type, "
            + "  oct.code AS occupancy_type_code, "
            + "  rh.share_size,  "
            + "  rh.owner_type_code,  "
            + "  p.name1 as first_name,  "
            + "  p.name2 as middle_name,  "
            + "  p.name3 as last_name,  "
            + "  p.id_number,  "
            + "  p.dob,  "
            + "  CAST(CASE WHEN p.dob IS NULL THEN cast(null as double precision) ELSE date_part(cast('year' as text), now()) - date_part(cast('year' as text), p.dob) END as integer) AS age, "
            + "  CASE WHEN p.is_resident THEN cast('Ndiyo' as text) ELSE cast('Hapana' as text) END AS resident, "
            + "  p.is_resident,  "
            + "  p.mobile_number,  "
            + "  p.address,  "
            + "  p.person_photo_id,  "
            + "  get_translation(g.val, :langCode) as gender,  "
            + "  get_translation(ms.val, :langCode) as marital_status,  "
            + "  get_translation(ot.val, :langCode) as owner_type,  "
            + "  get_translation(it.val, :langCode) as id_type,  "
            + "  get_translation(c.val, :langCode) as citizenship "
            + "FROM  "
            + "  public.property prop  "
            + "  INNER JOIN public.parcel pl ON prop.parcel_id = pl.id "
            + "  LEFT JOIN public.ref_hamlet h ON pl.hamlet_code = h.code "
            + "  LEFT JOIN public.ref_village v ON h.village_code = v.code "
            + "  LEFT JOIN public.ref_district d ON v.district_code = d.code "
            + "  INNER JOIN public.rrr r ON prop.id = r.property_id "
            + "  INNER JOIN public.ref_right_type rt ON r.right_type_code = rt.code "
            + "  LEFT JOIN public.ref_occupancy_type oct ON r.occupancy_type_code = oct.code "
            + "  INNER JOIN public.rightholder rh ON r.id = rh.rrr_id  "
            + "  INNER JOIN public.party p ON rh.party_id = p.id  "
            + "  LEFT JOIN public.ref_gender g ON p.gender_code = g.code  "
            + "  LEFT JOIN public.ref_citizenship c ON p.citizenship_code = c.code  "
            + "  LEFT JOIN public.ref_id_type it ON p.id_type_code = it.code  "
            + "  LEFT JOIN public.ref_marital_status ms ON p.marital_status_code = ms.code  "
            + "  LEFT JOIN public.ref_owner_type ot ON rh.owner_type_code = ot.code  "
            + " WHERE prop.id = :" + PARAM_PROP_ID + " and r.status_code = 'current' and rt.right_type_group_code = 'ownership' "
            + " ORDER BY p.name1";

    public RegistryBookRecord() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
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

    public String getUka() {
        return uka;
    }

    public void setUka(String uka) {
        this.uka = uka;
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

    public String getRightId() {
        return rightId;
    }

    public void setRightId(String rightId) {
        this.rightId = rightId;
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

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }

    public String getOwnerTypeCode() {
        return ownerTypeCode;
    }

    public void setOwnerTypeCode(String ownerTypeCode) {
        this.ownerTypeCode = ownerTypeCode;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getResident() {
        return resident;
    }

    public void setResident(String resident) {
        this.resident = resident;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getVillageResident() {
        return villageResident;
    }

    public void setVillageResident(Boolean villageResident) {
        this.villageResident = villageResident;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPersonPhotoId() {
        return personPhotoId;
    }

    public void setPersonPhotoId(String personPhotoId) {
        this.personPhotoId = personPhotoId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getFullName() {
        String name = "";
        if (!StringUtility.isEmpty(getFirstName())) {
            name = getFirstName().trim();
        }
        if (!StringUtility.isEmpty(getMiddleName())) {
            if (name.length() > 0) {
                name = name + " " + getMiddleName().trim();
            } else {
                name = getMiddleName().trim();
            }
        }
        if (!StringUtility.isEmpty(getLastName())) {
            if (name.length() > 0) {
                name = name + " " + getLastName().trim();
            } else {
                name = getLastName().trim();
            }
        }
        return name;
    }
}
