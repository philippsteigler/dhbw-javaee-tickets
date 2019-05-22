package org.dhbw.mosbach.ai.tickets.ejb;

import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

@Stateless(name = "TicketDAOProxy")
@PermitAll
public class TicketDAOProxy extends TicketDAO {
    private static final long serialVersionUID = 1L;


    @PermitAll
    @Override
    public void persist(Ticket entity) {
        super.persist(entity);
    }

    @PermitAll
    @Override
    public void persist(Ticket... entities) {
        super.persist(entities);
    }

    @PermitAll
    @Override
    public Ticket findByUnique(String fieldName, Object key) {
        return super.findByUnique(fieldName, key);
    }
}
