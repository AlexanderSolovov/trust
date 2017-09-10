package com.dai.trust.services.party;

import com.dai.trust.models.party.LegalEntity;
import com.dai.trust.models.party.Person;
import com.dai.trust.services.AbstractService;

/**
 * Contains methods for managing private persons and legal entities.
 */
public class PartyService extends AbstractService {

    public PartyService() {
        super();
    }

    /**
     * Returns Person object.
     *
     * @param id Person id.
     * @return
     */
    public Person getPerson(String id) {
        return getById(Person.class, id, false);
    }
    
    /**
     * Returns Legal Entity object.
     *
     * @param id Legal Entity id.
     * @return
     */
    public LegalEntity getLegalEntity(String id) {
        return getById(LegalEntity.class, id, false);
    }
}
