package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_transaction_type")
@Entity
public class TransactionType extends AbstractRefDataEntity {
    public static final String CAVEAT_REGISTRATION = "reg_caveat";
    public static final String MORTGAGE_REGISTRATION = "reg_mortgage";
    public static final String OWNERSHIP_REGISTRATION = "reg_ownership";
    public static final String CAVEAT_REMOVAL = "remove_caveat";
    public static final String MORTGAGE_REMOVAL = "remove_mortgage";
    public static final String SURRENDER = "surrender";
    public static final String TERMINATION = "terminate";
    public static final String OWNERSHIP_TRANSFER = "trans_ownership";

    public TransactionType(){
        super();
    }
}
