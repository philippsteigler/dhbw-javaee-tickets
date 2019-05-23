package org.dhbw.mosbach.ai.tickets.database;

import org.dhbw.mosbach.ai.tickets.model.Entry;
import org.dhbw.mosbach.ai.tickets.security.CDIRoleCheck;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named
@Dependent
@CDIRoleCheck
public class EntryDAO extends BaseDAO<Entry, Long> {
    private static final long serialVersionUID = 1L;

    @Override
    @PermitAll
    public Entry findByUnique(String fieldName, Object key) {
        return super.findByUnique(fieldName, key);
    }
}
