package com.dai.trust.models.application;

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
@Table(name = "application_document")
@IdClass(ApplicationDocumentId.class)
public class ApplicationDocument extends AbstractEntity {

    @Id
    @JsonIgnore
    @Column(name = "app_id", insertable = false, updatable = false)
    private String appId;

    @Id
    @JsonIgnore
    @Column(name = "document_id", insertable = false, updatable = false)
    private String documentId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Application application;
    
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    public ApplicationDocument() {
        super();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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
