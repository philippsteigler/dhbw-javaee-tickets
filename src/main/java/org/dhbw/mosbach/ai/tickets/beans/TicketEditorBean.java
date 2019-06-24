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

/**
 * Klasse zum Umgang mit Tickets für die Rolle des Bearbeiters.
 *
 * Stellt Funktionen zum Lesen, Bearbeiten, Delegieren, Schließen, usw. von Tickets sowie Hinzufügen von
 * Ticket-Einträgen bereit.
 */
@Named
@SessionScoped
@CDIRoleCheck
@RolesAllowed(value = { Roles.ADMIN, Roles.EDITOR})
public class TicketEditorBean extends AbstractBean {

    @Inject
    private TicketDAO ticketDAO;

    @Inject
    private EntryDAO entryDAO;

    @Inject
    private SecurityBean securityBean;

    @Inject
    private FilterBean filterBean;

    private static final String VIEW_DETAILS = "editor-ticket-details";
    private String REDIRECT = "";

    private Ticket currentTicket;
    private List<Entry> currentEntries;

    private List<Ticket> searchResult;
    private String searchString = "";

    private String entryContent = "";

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

    public String viewTicketDetails(Ticket ticket, String redirect) {
        currentTicket = ticket;
        currentEntries = currentTicket.getEntries();
        REDIRECT = redirect;

        return VIEW_DETAILS;
    }

    // Gibt alle Tickets zurück, die dem aktuellen Bearbeiter zugewiesen sind.
    // Es kann im Betreff nach Zeichenketten gesucht werden.
    public void fetchMyTickets() {
        // Wenn das Suchfeld leer ist...
        if (searchString.isEmpty()) {
            // ...dann gib alle Tickets mit der eigenen ID zurück.
            searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForEditorID(securityBean.getUser().getId()));
        } else {
            // Ansonsten gib alle Tickets mit der eignen ID zurück, welche die passende Zeichenkette enthalten.
            searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForEditorID(searchString, securityBean.getUser().getId()));
        }
    }

    // Gibt alle Tickets existierenden zurück.
    // Es kann im Betreff nach Zeichenketten gesucht und nach einem Status gefiltert werden - auch beides gleichzeitig!
    public void fetchAllTickets() {
        // Wenn das Suchfeld und die Filteroptionen leer sind...
        if (searchString.isEmpty() && filterBean.getSelectedOptionForAllTickets().isEmpty()) {
            // ...dann gib alle Tickets im System zurück.
            searchResult = ImmutableList.copyOf(ticketDAO.getAll());
        } else {
            // Ansonsten, wenn etwas im Suchfeld steht, die Filteroptionen jedoch leer sind...
            if (filterBean.getSelectedOptionForAllTickets().isEmpty()) {
                // ...dann gib alle Tickets zurück, welche die passende Zeichenkette enthalten.
                searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubject(searchString));
            } else {
                // Ansonsten, wenn das Suchfeld leer ist, aber eine der Filteroptionen ausgewählt wurde...
                if (searchString.isEmpty()) {
                    // ...dann suche in allen Ticket nach den Tickets mit dem passenden Status.
                    searchResult = ImmutableList.copyOf(ticketDAO.getAll()).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                } else {
                    // Ansonsten suche in allen Tickets, welche die passende Zeichenkette enthalten UND dem ausgewählten Status-Filter entsprechen.
                    searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubject(searchString)).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                }
            }
        }
    }

    // Ticket an den Bearbeiter mit der übergebenen editorId delegieren
    public String delegateTicket(long editorId){
        currentTicket.setEditorId(editorId);
        currentTicket.setStatusToInProcess();
        saveTicket(currentTicket);

        currentTicket = null;
        return REDIRECT;
    }

    // Ticket freigeben. Staus wird auf offen und EditorId auf 0 gesetzt
    public String releaseTicket() {
        currentTicket.setStatusToOpen();
        currentTicket.setEditorId(0);
        saveTicket(currentTicket);

        currentTicket = null;
        return REDIRECT;
    }

    //  Ticket nehmen. Status wird auf in Bearbeitung gesetzt und die übergebene id als EditorId gesetzt
    public String takeTicket(long id) {
        currentTicket.setStatusToInProcess();
        currentTicket.setEditorId(id);
        saveTicket(currentTicket);

        currentTicket = null;
        return REDIRECT;
    }

    // Ticket schließen. Status wird auf offen gesetzt und EditorId auf 0
    public String closeTicket() {
        currentTicket.setStatusToClose();
        currentTicket.setEditorId(0);
        saveTicket(currentTicket);

        currentTicket = null;
        return REDIRECT;
    }

    // ein geschlossenes Ticket kann wieder geöffnet werden
    // Status wird auf offen gesetzt
    public void reopenTicket() {
        currentTicket.setStatusToOpen();
        saveTicket(currentTicket);
    }

    //vergleiche: TicketCustomerBean
    public void addEntryToTicket(String content) {
        Entry newEntry = new Entry(securityBean.getUser().getId(), content, new Date());
        currentTicket.addEntry(newEntry);
        saveEntry(newEntry);
        saveTicket(currentTicket);
        entryContent = "";
    }
}
