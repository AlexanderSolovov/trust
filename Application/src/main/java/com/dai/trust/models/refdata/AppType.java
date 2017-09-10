package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_app_type")
@Entity
public class AppType extends AbstractRefDataEntity {
    @Column(name = "app_type_group_code")
    private String appTypeGroupCode;
    
    @Column(name = "transaction_type_code")
    private String transactionTypeCode;
    
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
}
