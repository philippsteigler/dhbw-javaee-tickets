package org.dhbw.mosbach.ai.tickets.database;

import org.dhbw.mosbach.ai.tickets.model.Ticket;
import org.dhbw.mosbach.ai.tickets.model.User;

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

    public List<Ticket> getTicketsContainingSubject(String substring) {
        if ((substring != null) && (substring.length() >= 1)) {
            final String query = String.format("FROM %s t WHERE UPPER(t.subject) LIKE :subject", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getAllTicketsForEditorID(long id) {
        if (id >= 0) {
            final String query = String.format("FROM %s t WHERE t.editorId = :editorId", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("editorId", id)
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getTicketsContainingSubjectForEditorID(String substring, long id) {
        if ((substring != null) && (substring.length() >= 1) && (id >= 0)) {
            final String query = String.format("FROM %s t WHERE UPPER(t.subject) LIKE :subject AND t.editorId = :editorId", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .setParameter("editorId", id)
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getAllTicketsForCustomerID(long id) {
        if (id >= 0) {
            final String query = String.format("FROM %s t WHERE t.customerId = :customerId", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("customerId", id)
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getTicketsContainingSubjectForCustomerID(String substring, long id) {
        if ((substring != null) && (substring.length() >= 1) && (id >= 0)) {
            final String query = String.format("FROM %s t WHERE UPPER(t.subject) LIKE :subject AND t.customerId = :customerId", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .setParameter("customerId", id)
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getAllTicketsForCompany(String company) {
        if (company != null) {
            final String query = String.format("FROM %s t WHERE t.customerId IN (SELECT u.id FROM %s u WHERE u.company LIKE :company)", Ticket.class.getName(), User.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("company", company)
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Ticket> getTicketsContainingSubjectForCompany(String substring, String company) {
        if ((substring != null) && (substring.length() >= 1) && (company != null)) {
            final String query = String.format("FROM %s t WHERE UPPER(t.subject) LIKE :subject AND t.customerId IN (SELECT u.id FROM %s u WHERE u.company LIKE :company)", Ticket.class.getName(), User.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .setParameter("company", company)
                    .getResultList();
        }

        return Collections.emptyList();
    }
}
