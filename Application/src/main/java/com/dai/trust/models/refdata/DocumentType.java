package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name="ref_doc_type")
@Entity
public class DocumentType extends AbstractRefDataEntity {
    public DocumentType(){
        super();
    }
}
