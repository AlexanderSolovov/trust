package com.dai.trust.models.report;

import com.dai.trust.common.StringUtility;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 * Person summary, including right information
 */
@Entity
public class PersonWithRightSummary implements Serializable {

    @Id
    @Column
    private String id;

    @Column(name = "right_id")
    private String rightId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "owner_type_code")
    private String ownerTypeCode;

    @Column(name = "owner_type")
    private String ownerType;

    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dob;

    @Column(name = "id_type_code")
    private String idTypeCode;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "gender_code")
    private String genderCode;

    @Column
    private String gender;

    @Column(name = "marital_status_code")
    private String maritalStatusCode;

    @Column(name = "marital_status")
    private String maritalStatus;

    @Column
    private String address;

    @Column(name = "person_photo_id")
    private String personPhotoId;

    @Transient
    private File photo;
    
    @Column(name = "citizenship_code")
    private String citizenshipCode;

    @Column
    private String citizenship;

    @Column(name = "mobile_number")
    private String mobile;

    @Column(name = "share_size")
    private String share;

    @Column
    private Integer age;

    @Column
    private String resident;

    @Column(name = "is_resident")
    private Boolean villageResident;

    public static final String PARAM_RIGHT_ID = "rightId";

    public static final String QUERY_SEARCH_BY_RIGHT
            = "SELECT "
            + "p.id,"
            + "rh.rrr_id as right_id,"
            + "rh.share_size, "
            + "rh.owner_type_code, "
            + "p.name1 as first_name, "
            + "p.name2 as middle_name, "
            + "p.name3 as last_name, "
            + "p.id_type_code, "
            + "p.id_number, "
            + "p.citizenship_code, "
            + "p.gender_code, "
            + "p.marital_status_code, "
            + "p.dob, "
            + "CAST(CASE WHEN p.dob IS NULL THEN cast(null as double precision) ELSE date_part(cast('year' as text), now()) - date_part(cast('year' as text), p.dob) END as integer) AS age, "
            + "CASE WHEN p.is_resident THEN cast('Ndiyo' as text) ELSE cast('Hapana' as text) END AS resident, "
            + "p.is_resident, "
            + "p.mobile_number, "
            + "p.address, "
            + "p.person_photo_id, "
            + "get_translation(g.val, :langCode) as gender, "
            + "get_translation(ms.val, :langCode) as marital_status, "
            + "get_translation(ot.val, :langCode) as owner_type, "
            + "get_translation(it.val, :langCode) as id_type, "
            + "get_translation(c.val, :langCode) as citizenship "
            + "FROM "
            + "public.rightholder rh "
            + "INNER JOIN public.party p ON rh.party_id = p.id "
            + "LEFT JOIN public.ref_gender g ON p.gender_code = g.code "
            + "LEFT JOIN public.ref_citizenship c ON p.citizenship_code = c.code "
            + "LEFT JOIN public.ref_id_type it ON p.id_type_code = it.code "
            + "LEFT JOIN public.ref_marital_status ms ON p.marital_status_code = ms.code "
            + "LEFT JOIN public.ref_owner_type ot ON rh.owner_type_code = ot.code "
            + "WHERE p.is_private AND rh.rrr_id = :" + PARAM_RIGHT_ID + " "
            + "ORDER BY p.name1";

    public PersonWithRightSummary() {
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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getIdTypeCode() {
        return idTypeCode;
    }

    public void setIdTypeCode(String idTypeCode) {
        this.idTypeCode = idTypeCode;
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

    public String getGenderCode() {
        return genderCode;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatusCode() {
        return maritalStatusCode;
    }

    public void setMaritalStatusCode(String maritalStatusCode) {
        this.maritalStatusCode = maritalStatusCode;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
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

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public String getCitizenshipCode() {
        return citizenshipCode;
    }

    public void setCitizenshipCode(String citizenshipCode) {
        this.citizenshipCode = citizenshipCode;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
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

    public Boolean getVillageResident() {
        return villageResident;
    }

    public void setVillageResident(Boolean villageResident) {
        this.villageResident = villageResident;
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
