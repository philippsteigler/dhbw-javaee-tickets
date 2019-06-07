package org.dhbw.mosbach.ai.tickets.database;

import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named
@Dependent
public class TicketDAO extends BaseDAO<Ticket, Long>{
    private static final long serialVersionUID = 1L;

    @Override
    @PermitAll
    public Ticket findByUnique(String fieldName, Object key) {
        return super.findByUnique(fieldName, key);
    }
}