package com.dai.trust.models.report;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CcroByOccupancy implements Serializable {

    @Id
    private String code;

    @Column(name = "occupancy_type")
    private String occupancyType;

    @Column
    private Long total;

    @Column
    private Long pending;

    @Column
    private Long current;

    @Column
    private Long historic;

    public static final String QUERY = "select ot.code, get_translation(ot.val, :langCode) as occupancy_type, count(1) as total, \n"
            + "sum(case when r.status_code = 'pending' then 1 else 0 end) as pending,\n"
            + "sum(case when r.status_code = 'current' then 1 else 0 end) as current,\n"
            + "sum(case when r.status_code = 'historic' then 1 else 0 end) as historic \n"
            + "from public.ref_occupancy_type ot left join public.rrr r on ot.code = r.occupancy_type_code \n"
            + "where r.right_type_code = 'ccro' \n"
            + "group by ot.code, ot.val \n"
            + "order by ot.val";

    public CcroByOccupancy() {
        super();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOccupancyType() {
        return occupancyType;
    }

    public void setOccupancyType(String occupancyType) {
        this.occupancyType = occupancyType;
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
