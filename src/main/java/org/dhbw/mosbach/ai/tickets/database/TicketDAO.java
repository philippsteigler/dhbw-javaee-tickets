package org.dhbw.mosbach.ai.tickets.database;

import org.dhbw.mosbach.ai.tickets.model.Ticket;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;

@Named("ticketDAO")
@Dependent
public class TicketDAO extends BaseDAO<Ticket, Long>{
    private static final long serialVersionUID = 7107642207441127133L;

    public TicketDAO() {
        super();
    }

    public List<Ticket> getAllTicketsForID(long id) {
        if (id >= 0) {
            final String query = String.format("FROM %s e WHERE e.%s = :editorId", Ticket.class.getName(), "editorId");

            return em.createQuery(query, Ticket.class)
                    .setParameter("editorId", id)
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getTicketsContainingSubjectForID(String substring, long id) {
        if ((substring != null) && (substring.length() >= 2) && (id >= 0)) {
            final String query = String.format("FROM %s e WHERE UPPER(e.%s) LIKE :subject AND e.%s = :editorId", Ticket.class.getName(), "subject", "editorId");

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .setParameter("editorId", id)
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getTicketsContainingSubject(String substring) {
        if ((substring != null) && (substring.length() >= 2)) {
            final String query = String.format("FROM %s e WHERE UPPER(e.%s) LIKE :subject", Ticket.class.getName(), "subject");

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .getResultList();
        }

        return Collections.emptyList();
    }
}
