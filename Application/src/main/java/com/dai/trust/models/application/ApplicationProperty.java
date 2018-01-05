package com.dai.trust.models.application;

import com.dai.trust.models.AbstractEntity;
import com.dai.trust.models.property.Property;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "application_property")
@IdClass(ApplicationPropertyId.class)
public class ApplicationProperty extends AbstractEntity {

    @Id
    @JsonIgnore
    @Column(name = "app_id", insertable = false, updatable = false)
    private String appId;

    @Id
    @Column(name = "property_id", insertable = false, updatable = false)
    private String propId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Application application;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Property property;

    public ApplicationProperty() {
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

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}
