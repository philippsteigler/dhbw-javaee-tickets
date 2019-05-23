package org.dhbw.mosbach.ai.tickets.ejb;

import org.dhbw.mosbach.ai.tickets.database.EntryDAO;
import org.dhbw.mosbach.ai.tickets.model.Entry;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

@Stateless(name = "EntryDAOProxy")
@PermitAll
public class EntryDAOProxy extends EntryDAO {
    private static final long serialVersionUID = 1L;


    @PermitAll
    @Override
    public void persist(Entry entity) {
        super.persist(entity);
    }

    @PermitAll
    @Override
    public void persist(Entry... entities) {
        super.persist(entities);
    }

    @PermitAll
    @Override
    public Entry findByUnique(String fieldName, Object key) {
        return super.findByUnique(fieldName, key);
    }
}
