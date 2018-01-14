package com.dai.trust.models.report;

import com.dai.trust.models.search.PersonSearchResult;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Legal entity summary
 */
@Entity
public class LegalEntitySummary implements Serializable {

    @Id
    @Column
    private String id;

    @Column(name = "right_id")
    private String rightId;

    @Column
    private String name;

    @Column(name = "id_number")
    private String idNumber;

    @Column
    private String address;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "institution_type")
    private String institutionType;

    @Transient
    private PersonSearchResult representative;

    public static final String PARAM_RIGHT_ID = "rightId";

    public static final String QUERY_SEARCH_BY_RIGHT
            = "SELECT "
            + "p.id, "
            + "rh.rrr_id as right_id, "
            + "p.id as party_id, "
            + "p.name1 as name, "
            + "p.id_number, "
            + "p.mobile_number, "
            + "p.address, "
            + "get_translation(et.val, :langCode) as institution_type "
            + "FROM "
            + "public.rightholder rh "
            + "INNER JOIN public.party p ON rh.party_id = p.id "
            + "LEFT JOIN public.ref_entity_type et ON p.entity_type_code = et.code "
            + "WHERE p.is_private = 'f' AND rh.rrr_id = :" + PARAM_RIGHT_ID + " "
            + "ORDER BY p.name1";

    public LegalEntitySummary() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(String institutionType) {
        this.institutionType = institutionType;
    }

    public PersonSearchResult getRepresentative() {
        return representative;
    }

    public void setRepresentative(PersonSearchResult representative) {
        this.representative = representative;
    }
}
