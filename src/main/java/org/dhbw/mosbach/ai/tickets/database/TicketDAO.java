package org.dhbw.mosbach.ai.tickets.database;

import org.dhbw.mosbach.ai.tickets.model.Ticket;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named("ticketDAO")
@Dependent
public class TicketDAO extends BaseDAO<Ticket, Long>{
    private static final long serialVersionUID = 7107642207441127133L;

    public TicketDAO()
    {
        super();
    }

}