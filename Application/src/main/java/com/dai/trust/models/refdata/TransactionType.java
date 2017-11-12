package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_transaction_type")
@Entity
public class TransactionType extends AbstractRefDataEntity {
    public static final String FIRST_REGISTRATION = "first_registration";
    public static final String REMOVE = "remove";
    public static final String SURRENDER = "surrender";
    public static final String TERMINATION = "terminate";
    public static final String TRANSFER = "transfer";
    public static final String VARY = "vary";
    public static final String RECTIFY = "rectify";
    public static final String CHANGE_NAME = "change_name";
    
    public TransactionType(){
        super();
    }
}
