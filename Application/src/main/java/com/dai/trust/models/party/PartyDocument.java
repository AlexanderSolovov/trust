package com.dai.trust.models.party;

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
@Table(name = "party_document")
@IdClass(PartyDocumentId.class)
public class PartyDocument extends AbstractEntity {

    @Id
    @JsonIgnore
    @Column(name = "party_id", insertable = false, updatable = false)
    private String partyId;

    @Id
    @JsonIgnore
    @Column(name = "document_id", insertable = false, updatable = false)
    private String documentId;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", updatable = false, insertable = false, referencedColumnName = "id")
    private Party party;
    
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "document_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Document document;

    public PartyDocument() {
        super();
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
