package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name="ref_app_type")
@Entity
public class AppType extends AbstractRefDataEntity {
    @Column(name = "app_type_group_code")
    private String appTypeGroupCode;
    
    @Column(name = "transaction_type_code")
    private String transactionTypeCode;
    
    @OneToMany(mappedBy = "appType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppTypeRightType> rightTypeCodes;
    
    public AppType(){
        super();
    }

    public String getAppTypeGroupCode() {
        return appTypeGroupCode;
    }

    public void setAppTypeGroupCode(String appTypeGroupCode) {
        this.appTypeGroupCode = appTypeGroupCode;
    }

    public String getTransactionTypeCode() {
        return transactionTypeCode;
    }

    public void setTransactionTypeCode(String transactionTypeCode) {
        this.transactionTypeCode = transactionTypeCode;
    }

    public List<AppTypeRightType> getRightTypeCodes() {
        return rightTypeCodes;
    }

    public void setRightTypeCodes(List<AppTypeRightType> rightTypeCodes) {
        this.rightTypeCodes = rightTypeCodes;
    }
}
