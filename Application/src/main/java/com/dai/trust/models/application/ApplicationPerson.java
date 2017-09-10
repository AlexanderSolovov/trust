package com.dai.trust.models.application;

import com.dai.trust.models.AbstractEntity;
import com.dai.trust.models.party.Person;
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
@Table(name = "application_party")
@IdClass(ApplicationPartyId.class)
public class ApplicationPerson extends AbstractEntity {

    @Id
    @JsonIgnore
    @Column(name = "app_id", insertable = false, updatable = false)
    private String appId;

    @Id
    @JsonIgnore
    @Column(name = "party_id", insertable = false, updatable = false)
    private String partyId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Application application;
    
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "party_id", referencedColumnName = "id")
    private Person person;

    public ApplicationPerson() {
        super();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
