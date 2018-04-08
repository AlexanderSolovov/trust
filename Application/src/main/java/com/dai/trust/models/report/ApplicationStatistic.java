package com.dai.trust.models.report;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ApplicationStatistic implements Serializable {

    @Id
    private String code;

    @Column(name = "app_type")
    private String appType;

    @Column
    private Long lodged;

    @Column
    private Long pending;

    @Column
    private Long withdrawn;

    @Column
    private Long approved;

    @Column
    private Long rejected;

    public static final String QUERY = "select apt.code, get_translation(apt.val, :langCode) as app_type, \n"
            + "sum(case when a.lodgement_date between :fromDate and :toDate then 1 else 0 end) as lodged, \n"
            + "sum(case when a.status_code = 'pending' and a.lodgement_date between :fromDate and :toDate then 1 else 0 end) as pending,\n"
            + "sum(case when a.status_code = 'withdrawn' and a.lodgement_date between :fromDate and :toDate then 1 else 0 end) as withdrawn,\n"
            + "sum(case when a.status_code = 'approved' and a.lodgement_date between :fromDate and :toDate then 1 else 0 end) as approved,\n"
            + "sum(case when a.status_code = 'rejected' and a.lodgement_date between :fromDate and :toDate then 1 else 0 end) as rejected \n"
            + "from public.ref_app_type apt left join public.application a on apt.code = a.app_type_code \n"
            + "group by apt.code, apt.val \n"
            + "order by apt.val";

    public ApplicationStatistic() {
        super();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public Long getLodged() {
        return lodged;
    }

    public void setLodged(Long lodged) {
        this.lodged = lodged;
    }

    public Long getPending() {
        return pending;
    }

    public void setPending(Long pending) {
        this.pending = pending;
    }

    public Long getWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(Long withdrawn) {
        this.withdrawn = withdrawn;
    }

    public Long getApproved() {
        return approved;
    }

    public void setApproved(Long approved) {
        this.approved = approved;
    }

    public Long getRejected() {
        return rejected;
    }

    public void setRejected(Long rejected) {
        this.rejected = rejected;
    }
}
