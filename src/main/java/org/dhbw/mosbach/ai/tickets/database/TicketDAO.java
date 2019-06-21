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

    // Datenbankabfrage: Alle Tickets, die eine bestimmte Zeichenketter im Betreff enthalten.
    // Für die Ausgabe von Tickets in der Liste unter Editor - All Tickets.
    public List<Ticket> getTicketsContainingSubject(String substring) {
        if ((substring != null) && (substring.length() >= 1)) {
            // Filtere die Tickets mit dem gewünschten Betreff heraus.
            final String query = String.format("FROM %s t WHERE UPPER(t.subject) LIKE :subject", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .getResultList();
        }

        // Falls kein Ergebnis gefunden wurde, gib eine leere Liste zurück.
        return Collections.emptyList();
    }

    // Datenbankabfrage: Alle Tickets, die einem bestimmten Bearbeiter zugewiesen sind.
    // Für die Ausgabe von Tickets in der Liste unter Editor - My Tickets.
    public List<Ticket> getAllTicketsForEditorID(long id) {
        if (id >= 0) {
            // Suche zunächst nach allen Tickets, dessen Bearbeiter der gewünschten ID entspricht.
            final String query = String.format("FROM %s t WHERE t.editorId = :editorId", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("editorId", id)
                    .getResultList();
        }

        // Falls kein Ergebnis gefunden wurde, gib eine leere Liste zurück.
        return Collections.emptyList();
    }

    // Datenbankabfrage: Alle Tickets, die einem bestimmten Bearbeiter zugewiesen sind und eine bestimmte Zeichenketter im Betreff enthalten.
    // Für die Ausgabe von Tickets in der Liste unter Editor - My Tickets.
    public List<Ticket> getTicketsContainingSubjectForEditorID(String substring, long id) {
        if ((substring != null) && (substring.length() >= 1) && (id >= 0)) {
            // Suche zunächst nach allen Tickets, dessen Bearbeiter der gewünschten ID entspricht.
            // Filtere anschließend die Tickets mit dem gewünschten Betreff heraus.
            final String query = String.format("FROM %s t WHERE UPPER(t.subject) LIKE :subject AND t.editorId = :editorId", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .setParameter("editorId", id)
                    .getResultList();
        }

        // Falls kein Ergebnis gefunden wurde, gib eine leere Liste zurück.
        return Collections.emptyList();
    }

    // Datenbankabfrage: Alle Tickets, die von einem bestimmten Kunden eingereicht wurden
    // Für die Ausgabe von Tickets in der Liste unter Customer - My Tickets.
    public List<Ticket> getAllTicketsForCustomerID(long id) {
        if (id >= 0) {
            // Suche zunächst nach allen Tickets, dessen Ersteller die gewünschte ID hinterlegt hat.
            final String query = String.format("FROM %s t WHERE t.customerId = :customerId", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("customerId", id)
                    .getResultList();
        }

        // Falls kein Ergebnis gefunden wurde, gib eine leere Liste zurück.
        return Collections.emptyList();
    }

    // Datenbankabfrage: Alle Tickets, die von einem bestimmten Kunden eingereicht wurden und eine bestimmte Zeichenketter im Betreff enthalten.
    // Für die Ausgabe von Tickets in der Liste unter Customer - My Tickets.
    public List<Ticket> getTicketsContainingSubjectForCustomerID(String substring, long id) {
        if ((substring != null) && (substring.length() >= 1) && (id >= 0)) {
            // Suche zunächst nach allen Tickets, dessen Ersteller die gewünschte ID hinterlegt hat.
            // Filtere anschließend die Tickets mit dem gewünschten Betreff heraus.
            final String query = String.format("FROM %s t WHERE UPPER(t.subject) LIKE :subject AND t.customerId = :customerId", Ticket.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .setParameter("customerId", id)
                    .getResultList();
        }

        // Falls kein Ergebnis gefunden wurde, gib eine leere Liste zurück.
        return Collections.emptyList();
    }

    // Datenbankabfrage: Alle Tickets, die von einem User aus einer bestimmten Firma eingereicht wurden.
    // Für die Ausgabe von Tickets in der Liste unter Customer - Company Tickets.
    public List<Ticket> getAllTicketsForCompany(String company) {
        if (company != null) {
            // Suche hierfür nach allen Tickets, dessen Ersteller die gewünschte Firma hinterlegt hat.
            final String query = String.format("FROM %s t WHERE t.customerId IN (SELECT u.id FROM %s u WHERE u.company LIKE :company)", Ticket.class.getName(), User.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("company", company)
                    .getResultList();
        }

        // Falls kein Ergebnis gefunden wurde, gib eine leere Liste zurück.
        return Collections.emptyList();
    }

    // Datenbankabfrage: Alle Tickets, die von einem User aus einer bestimmten Firma eingereicht wurden und eine bestimmte Zeichenketter im Betreff enthalten.
    // Für die Ausgabe von Tickets in der Liste unter Customer - Company Tickets.
    public List<Ticket> getTicketsContainingSubjectForCompany(String substring, String company) {
        if ((substring != null) && (substring.length() >= 1) && (company != null)) {
            // Suche zunächst nach allen Tickets, dessen Ersteller die gewünschte Firma hinterlegt hat.
            // Filtere anschließend die Tickets mit dem gewünschten Betreff heraus.
            final String query = String.format("FROM %s t WHERE UPPER(t.subject) LIKE :subject AND t.customerId IN (SELECT u.id FROM %s u WHERE u.company LIKE :company)", Ticket.class.getName(), User.class.getName());

            return em.createQuery(query, Ticket.class)
                    .setParameter("subject", "%" + substring.toUpperCase() + "%")
                    .setParameter("company", company)
                    .getResultList();
        }

        // Falls kein Ergebnis gefunden wurde, gib eine leere Liste zurück.
        return Collections.emptyList();
    }
}
