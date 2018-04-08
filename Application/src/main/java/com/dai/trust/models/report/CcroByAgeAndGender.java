package com.dai.trust.models.report;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CcroByAgeAndGender implements Serializable {

    @Id
    private Long total;

    @Column(name = "below_35")
    private Long totalBelow35;

    @Column(name = "over_35")
    private Long totalOver35;

    @Column(name = "below_35_male")
    private Long maleBelow35;

    @Column(name = "below_35_female")
    private Long femaleBelow35;

    @Column(name = "over_35_male")
    private Long maleOver35;

    @Column(name = "over_35_female")
    private Long femaleOver35;

    public static final String QUERY = "select count(1) as total, \n"
            + "sum(case when DATE_PART('year', now()) - DATE_PART('year', p.dob) <= 35 then 1 else 0 end) as below_35, \n"
            + "sum(case when DATE_PART('year', now()) - DATE_PART('year', p.dob) > 35 then 1 else 0 end) as over_35,\n"
            + "sum(case when DATE_PART('year', now()) - DATE_PART('year', p.dob) <= 35 and p.gender_code = 'male' then 1 else 0 end) as below_35_male, \n"
            + "sum(case when DATE_PART('year', now()) - DATE_PART('year', p.dob) <= 35 and p.gender_code = 'female' then 1 else 0 end) as below_35_female, \n"
            + "sum(case when DATE_PART('year', now()) - DATE_PART('year', p.dob) > 35 and p.gender_code = 'male' then 1 else 0 end) as over_35_male,\n"
            + "sum(case when DATE_PART('year', now()) - DATE_PART('year', p.dob) > 35 and p.gender_code = 'female' then 1 else 0 end) as over_35_female \n"
            + "from public.rrr r inner join public.rightholder rh on r.id= rh.rrr_id inner join public.party p on rh.party_id = p.id \n"
            + "where r.right_type_code = 'ccro' and r.status_code = 'current' and rh.owner_type_code = 'owner' and p.is_private and p.dob is not null";

    public CcroByAgeAndGender() {
        super();
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalBelow35() {
        return totalBelow35;
    }

    public void setTotalBelow35(Long totalBelow35) {
        this.totalBelow35 = totalBelow35;
    }

    public Long getTotalOver35() {
        return totalOver35;
    }

    public void setTotalOver35(Long totalOver35) {
        this.totalOver35 = totalOver35;
    }

    public Long getMaleBelow35() {
        return maleBelow35;
    }

    public void setMaleBelow35(Long maleBelow35) {
        this.maleBelow35 = maleBelow35;
    }

    public Long getFemaleBelow35() {
        return femaleBelow35;
    }

    public void setFemaleBelow35(Long femaleBelow35) {
        this.femaleBelow35 = femaleBelow35;
    }

    public Long getMaleOver35() {
        return maleOver35;
    }

    public void setMaleOver35(Long maleOver35) {
        this.maleOver35 = maleOver35;
    }

    public Long getFemaleOver35() {
        return femaleOver35;
    }

    public void setFemaleOver35(Long femaleOver35) {
        this.femaleOver35 = femaleOver35;
    }
}
