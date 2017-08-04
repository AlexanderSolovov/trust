package com.dai.trust.models.refdata;

import com.dai.trust.models.AbstractRefDataEntity;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

@Table(name="ref_doc_type")
@DynamicUpdate(value=true)
@SelectBeforeUpdate(value=true)
@Entity
public class DocumentType extends AbstractRefDataEntity {
    public DocumentType(){
        super();
    }
}
