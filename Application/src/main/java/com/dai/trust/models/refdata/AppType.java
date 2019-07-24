package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "ref_app_type")
@Entity
public class AppType extends AbstractRefDataEntity {

    public static final String CODE_TRANS_TO_ADMIN = "ccro_trans_admin";
    public static final String CODE_ASSENT_TO_BEQUEST = "ccro_trans_assent";
    public static final String CODE_TRANS_TO_SURVIVOR = "ccro_trans_survivor";

    @Column(name = "app_type_group_code")
    private String appTypeGroupCode;

    @Column(name = "transaction_type_code")
    private String transactionTypeCode;

    @Transient
    private List<AppTypeRightType> rightTypeCodes;

    @Column
    private Double fee;

    public AppType() {
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

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }
}
