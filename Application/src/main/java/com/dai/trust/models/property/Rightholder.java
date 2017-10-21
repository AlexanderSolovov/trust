package com.dai.trust.models.property;

import com.dai.trust.models.AbstractEntity;
import com.dai.trust.models.party.Party;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rightholder")
@IdClass(RightholderId.class)
public class Rightholder extends AbstractEntity {

    @Id
    @JsonIgnore
    @Column(name = "rrr_id", insertable = false, updatable = false)
    private String rrrId;

    @Id
    @JsonIgnore
    @Column(name = "party_id", insertable = false, updatable = false)
    private String partyId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rrr_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Rrr rrr;
    
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "party_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Party party;

    public Rightholder() {
        super();
    }

    public String getRrrId() {
        return rrrId;
    }

    public void setRrrId(String rrrId) {
        this.rrrId = rrrId;
    }

    public Rrr getRrr() {
        return rrr;
    }

    public void setRrr(Rrr rrr) {
        this.rrr = rrr;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }
}
