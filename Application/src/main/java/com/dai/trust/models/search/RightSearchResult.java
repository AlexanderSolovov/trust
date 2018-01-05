package com.dai.trust.models.search;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RightSearchResult implements Serializable {

    @Id
    private String id;
    @Column(name = "prop_id")
    private String propId;
    @Column(name = "prop_number")
    private String propNumber;
    @Column(name = "file_number")
    private String fileNumber;
    @Column(name = "parcel_id")
    private String parcel_id;
    @Column(name = "uka")
    private String uka;
    @Column(name = "right_type")
    private String rightType;
    @Column
    private String status;
    @Column(name = "rightholder_data")
    private String rightholderData;
    
    public static final String PARAM_PROP_NUMBER = "propNumber";
    public static final String PARAM_FILE_NUMBER = "fileNumber";
    public static final String PARAM_UKA = "uka";
    public static final String PARAM_RIGHT_TYPE_CODE = "rightTypeCode";
    public static final String PARAM_RIGHTHOLDER_NAME = "rightholderName";
    public static final String PARAM_RIGHTHOLDER_ID = "rightholderId";
    public static final String PARAM_STATUS_CODE = "statusCode";
    
    private static final String SEARCH_SELECT
            = "select r.id, p.id as prop_id, p.prop_number, p.file_number, pl.id as parcel_id, pl.uka, get_translation(rt.val, :langCode) as right_type, get_translation(rs.val, :langCode) as status, \n"
            + "	rightholders.rightholder_data\n"
            + "from (public.property p inner join public.parcel pl on p.parcel_id = pl.id) \n"
            + "  inner join (((public.rrr r inner join public.ref_right_type rt on r.right_type_code = rt.code) inner join public.ref_reg_status rs on r.status_code = rs.code)\n"
            + "  inner join \n"
            + "	(select rh.rrr_id, string_agg(trim(trim(p.name1 || ' ' || coalesce(p.name3, '')) || ' ' || coalesce(p.name2, '')), ', ') as rightholder_names,\n"
            + "	  string_agg(coalesce(p.id_number, ''), ',') as rightholder_ids,\n"
            + "	  string_agg(trim(trim(p.name1 || ' ' || coalesce(p.name3, '')) || ' ' || coalesce(p.name2, '')) || \n"
            + "	  (case when coalesce(p.id_number, '') != '' then E'\\n' || '(' || trim(coalesce(get_translation(idt.val, NULL), '') || ' #' || p.id_number) || ')' else '' end), E',\\n' order by p.is_private, p.name1) as rightholder_data \n"
            + "	from public.rightholder rh inner join public.party p on rh.party_id = p.id left join public.ref_id_type idt on p.id_type_code = idt.code\n"
            + "	group by rh.rrr_id) rightholders on r.id = rightholders.rrr_id) on p.id = r.property_id \n";

    public static final String QUERY_SEARCH = SEARCH_SELECT + " where lower(p.prop_number) like :" + PARAM_PROP_NUMBER + " \n"
            + " and lower(coalesce(p.file_number, '')) like :" + PARAM_FILE_NUMBER + " \n"
            + " and lower(pl.uka) like :" + PARAM_UKA + " \n"
            + " and ('' = :" + PARAM_RIGHT_TYPE_CODE + " or r.right_type_code = :" + PARAM_RIGHT_TYPE_CODE + ") \n"
            + " and lower(rightholders.rightholder_names) like :" + PARAM_RIGHTHOLDER_NAME + " \n"
            + " and lower(rightholders.rightholder_ids) like :" + PARAM_RIGHTHOLDER_ID + " \n"
            + " and ('' = :" + PARAM_STATUS_CODE + " or r.status_code = :" + PARAM_STATUS_CODE + ") limit 1001";

    public RightSearchResult() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getParcel_id() {
        return parcel_id;
    }

    public void setParcel_id(String parcel_id) {
        this.parcel_id = parcel_id;
    }

    public String getUka() {
        return uka;
    }

    public void setUka(String uka) {
        this.uka = uka;
    }

    public String getRightType() {
        return rightType;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRightholderData() {
        return rightholderData;
    }

    public void setRightholderData(String rightholderData) {
        this.rightholderData = rightholderData;
    }
}
