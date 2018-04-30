package com.dai.trust.models.search;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;

@Entity
public class PersonSearchResult implements Serializable {

    @Id
    private String id;
    @Column
    private String name;
    @Column
    private String address;
    @Column(name = "mobile_number")
    private String mobileNumber;
    @Column(name = "id_type")
    private String idType;
    @Column(name = "id_number")
    private String idNumber;
    @Column
    private String ccros;
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dob;
    @Column(name = "status_code")
    private String statusCode;

    public static final String QUERY_SELECT
            = "select p.id, get_translation(idt.val, :langCode) as id_type, (name1 || ' ' || coalesce(name3, '') || ' ' || coalesce(name2, '')) as name, p.id_number, p.dob, p.mobile_number, p.address, p.status_code,\n"
            + "party_ccros.ccros \n"
            + "from party p left join ref_id_type idt on p.id_type_code = idt.code left join (\n"
            + "  select rh.party_id, string_agg(distinct(p.prop_number), ', ') as ccros from public.rightholder rh inner join rrr on rh.rrr_id = rrr.id inner join public.property p on rrr.property_id = p.id group by rh.party_id\n"
            + ") party_ccros on p.id = party_ccros.party_id \n";

    public PersonSearchResult() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getCcros() {
        return ccros;
    }

    public void setCcros(String ccros) {
        this.ccros = ccros;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
