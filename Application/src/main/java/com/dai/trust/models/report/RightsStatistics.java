package com.dai.trust.models.report;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class RightsStatistics implements Serializable {

    @Id
    private String code;

    @Column(name = "right_type")
    private String rightType;

    @Column
    private Long total;

    @Column
    private Long pending;

    @Column
    private Long current;

    @Column
    private Long historic;

    public static final String QUERY = "select rt.code, get_translation(rt.val, :langCode) as right_type, count(1) as total, \n"
            + "sum(case when r.status_code = 'pending' then 1 else 0 end) as pending,\n"
            + "sum(case when r.status_code = 'current' then 1 else 0 end) as current,\n"
            + "sum(case when r.status_code = 'historic' then 1 else 0 end) as historic \n"
            + "from public.ref_right_type rt left join public.rrr r on rt.code = r.right_type_code \n"
            + "group by rt.code, rt.val \n"
            + "order by rt.val";

    public RightsStatistics() {
        super();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRightType() {
        return rightType;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPending() {
        return pending;
    }

    public void setPending(Long pending) {
        this.pending = pending;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getHistoric() {
        return historic;
    }

    public void setHistoric(Long historic) {
        this.historic = historic;
    }
}
