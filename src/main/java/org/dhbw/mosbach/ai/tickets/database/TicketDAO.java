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
            return em.createQuery("FROM Ticket t WHERE t.editorId = :editorId", Ticket.class)
                    .setParameter("editorId", id)
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getTicketsContainingSubjectForID(String substring, long id) {
        if ((substring != null) && (substring.length() >= 2) && (id >= 0)) {
            return em.createQuery("FROM Ticket t WHERE UPPER(t.subject) LIKE :subject AND t.editorId = :editorId", Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .setParameter("editorId", id)
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getTicketsContainingSubject(String substring) {
        if ((substring != null) && (substring.length() >= 2)) {
            return em.createQuery("FROM Ticket t WHERE UPPER(t.subject) LIKE :subject", Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .getResultList();
        }

        return Collections.emptyList();
    }
}
