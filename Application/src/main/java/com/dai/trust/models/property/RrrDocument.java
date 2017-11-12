package com.dai.trust.models.property;

import com.dai.trust.models.AbstractEntity;
import com.dai.trust.models.document.Document;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rrr_document")
@IdClass(RrrDocumentId.class)
public class RrrDocument extends AbstractEntity {

    @Id
    @JsonIgnore
    @Column(name = "rrr_id", insertable = false, updatable = false)
    private String rrrId;

    @Id
    @JsonIgnore
    @Column(name = "document_id", insertable = false, updatable = false)
    private String documentId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rrr_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Rrr rrr;
    
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "document_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Document document;

    public RrrDocument() {
        super();
    }

    public String getRrrId() {
        return rrrId;
    }

    public void setRrrId(String rrrId) {
        this.rrrId = rrrId;
    }

    public Rrr getRrr() {
        return rrr;
    }

    public void setRrr(Rrr rrr) {
        this.rrr = rrr;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

     public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
