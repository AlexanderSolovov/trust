package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name="ref_app_type_group")
@Entity
public class AppTypeGroup extends AbstractRefDataEntity {
    @Transient
    private List<AppType> appTypes;
    
    public AppTypeGroup(){
        super();
    }

    public List<AppType> getAppTypes() {
        return appTypes;
    }

    public void setAppTypes(List<AppType> appTypes) {
        this.appTypes = appTypes;
    }
}
