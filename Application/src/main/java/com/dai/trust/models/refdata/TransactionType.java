package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_transaction_type")
@Entity
public class TransactionType extends AbstractRefDataEntity {
    public TransactionType(){
        super();
    }
}
