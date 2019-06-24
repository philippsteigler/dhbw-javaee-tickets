package org.dhbw.mosbach.ai.tickets.beans;

import com.google.common.collect.ImmutableList;
import org.dhbw.mosbach.ai.tickets.database.EntryDAO;
import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Entry;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.Ticket;
import org.dhbw.mosbach.ai.tickets.security.CDIRoleCheck;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named
@SessionScoped
@CDIRoleCheck
@RolesAllowed(value = { Roles.ADMIN, Roles.CUSTOMER})
public class TicketCustomerBean extends AbstractBean {

    @Inject
    private TicketDAO ticketDAO;

    @Inject
    private EntryDAO entryDAO;

    @Inject
    private SecurityBean securityBean;

    @Inject
    private FilterBean filterBean;

    private static final String VIEW_MY_TICKETS = "customer-my-tickets";
    private static final String VIEW_DETAILS = "customer-ticket-details";

    private Ticket currentTicket;
    private List<Entry> currentEntries;

    private List<Ticket> searchResult;
    private String searchString = "";

    private String entryContent = "";
    private String ticketContent = "";
    private String ticketSubject = "";

    private void saveTicket(Ticket ticket) {
        ticketDAO.persistOrMerge(ticket);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "ticket.saveSuccess");
    }

    private void saveEntry(Entry entry) {
        entryDAO.persistOrMerge(entry);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "entry.saveSuccess");
    }

    // Getter und Setter für Suchfeld.
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public List<Ticket> getSearchResult() {
        return searchResult;
    }

    // Getter und Setter für Tickets.
    public Ticket getCurrentTicket() {
        return currentTicket;
    }

    public List<Entry> getCurrentEntries() {
        return currentEntries;
    }

    // Getter und Setter für Einträge.
    public void setEntryContent(String entryContent) {
        this.entryContent = entryContent;
    }

    public String getEntryContent() {
        return entryContent;
    }

    public void setTicketContent(String ticketContent) { this.ticketContent = ticketContent; }

    public String getTicketContent() { return ticketContent; }

    public void setTicketSubject(String ticketSubject) {this.ticketSubject = ticketSubject; }

    public String getTicketSubject() { return ticketSubject; }

    public String viewTicketDetails(Ticket ticket) {
        currentTicket = ticket;
        currentEntries = currentTicket.getEntries();

        return VIEW_DETAILS;
    }

    // Gibt alle Tickets zurück, die der aktuelle Kunde eingereicht hat.
    // Es kann im Betreff nach Zeichenketten gesucht und nach einem Status gefiltert werden - auch beides gleichzeitig!
    public void fetchMyTickets() {
        // Wenn das Suchfeld und die Filteroptionen leer sind...
        if (searchString.isEmpty() && filterBean.getSelectedOptionForAllTickets().isEmpty()) {
            // ...gib alle Tickets mit der eigenen ID zurück.
            searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCustomerID(securityBean.getUser().getId()));
        } else {
            // Ansonsten, wenn etwas im Suchfeld steht, die Filteroptionen jedoch leer sind...
            if (filterBean.getSelectedOptionForAllTickets().isEmpty()) {
                // ...gib alle Tickets mit der eigenen ID zurück, welche die passende Zeichenkette im Betreff haben.
                searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCustomerID(searchString, securityBean.getUser().getId()));
            } else {
                // Ansonsten, wenn das Suchfeld leer ist, aber eine der Filteroptionen ausgewählt wurde...
                if (searchString.isEmpty()) {
                    // ...dann suche in allen Ticket mit der eigenen ID nach den Tickets mit dem passenden Status.
                    searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCustomerID(securityBean.getUser().getId())).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                } else {
                    // Ansonsten suche in allen Tickets mit der eigenen ID, welche die passende Zeichenkette enthalten UND dem ausgewählten Status-Filter entsprechen.
                    searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCustomerID(searchString, securityBean.getUser().getId())).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                }
            }
        }
    }

    // Gibt alle Tickets zurück, die von der Firma des aktuellen Kunden eingereicht wurden.
    // Es kann im Betreff nach Zeichenketten gesucht und nach einem Status gefiltert werden - auch beides gleichzeitig!
    public void fetchAllTickets() {
        // Wenn das Suchfeld und die Filteroptionen leer sind...
        if (searchString.isEmpty() && filterBean.getSelectedOptionForAllTickets().isEmpty()) {
            // ...gib alle Tickets der eigenen Firma zurück.
            searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCompany(securityBean.getUser().getCompany()));
        } else {
            // Ansonsten, wenn etwas im Suchfeld steht, die Filteroptionen jedoch leer sind...
            if (filterBean.getSelectedOptionForAllTickets().isEmpty()) {
                // ...gib alle Tickets der eigenen Firma zurück, welche die passende Zeichenkette im Betreff haben.
                searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCompany(searchString, securityBean.getUser().getCompany()));
            } else {
                // Ansonsten, wenn das Suchfeld leer ist, aber eine der Filteroptionen ausgewählt wurde...
                if (searchString.isEmpty()) {
                    // ...dann suche in allen Ticket der eigenen Firma nach den Tickets mit dem passenden Status.
                    searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCompany(securityBean.getUser().getCompany())).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                } else {
                    // Ansonsten suche in allen Tickets der eigenen Firma, welche die passende Zeichenkette enthalten UND dem ausgewählten Status-Filter entsprechen.
                    searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCompany(searchString, securityBean.getUser().getCompany())).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                }
            }
        }
    }

    //erstellt ein neues Ticket
    public String newTicket(String content, String subject) {
        Ticket newTicket = new Ticket(subject, Ticket.Status.open, 0, securityBean.getUser().getId());
        Entry newEntry = new Entry(securityBean.getUser().getId(), content, new Date());
        newTicket.addEntry(newEntry);
        saveEntry(newEntry);
        saveTicket(newTicket);
        ticketContent = "";
        ticketSubject = "";

        return VIEW_MY_TICKETS;
    }

    //erstellt einen neuen Eintrag und fügt ihn dem aktuellen Ticket hinzu
    public void addEntryToTicket(String content) {
        Entry newEntry = new Entry(securityBean.getUser().getId(), content, new Date());
        currentTicket.addEntry(newEntry);
        saveEntry(newEntry);
        saveTicket(currentTicket);
        entryContent = "";
    }
}
