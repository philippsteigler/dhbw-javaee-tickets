package org.dhbw.mosbach.ai.tickets.beans;

import com.google.common.collect.ImmutableList;
import org.dhbw.mosbach.ai.tickets.database.EntryDAO;
import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Entry;
import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class TicketCustomerBean extends AbstractBean {

    @Inject
    private TicketDAO ticketDAO;

    @Inject
    private EntryDAO entryDAO;

    @Inject
    private SecurityBean securityBean;

    private static final String VIEW_DETAILS = "customer-ticket-details";
    private static final String VIEW_NEW_TICKET = "customer-new-ticket";

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

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public List<Ticket> getSearchResult() {
        return searchResult;
    }

    public Ticket getCurrentTicket() {
        return currentTicket;
    }

    public List<Entry> getCurrentEntries() {
        return currentEntries;
    }

    public String viewTicketDetails(Ticket ticket) {
        currentTicket = ticket;
        currentEntries = currentTicket.getEntries();

        return VIEW_DETAILS;
    }

    public void fetchMyTickets() {
        if (searchString.isEmpty()) {
            searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCustomerID(securityBean.getUser().getId()));
        } else {
            searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCustomerID(searchString, securityBean.getUser().getId()));
        }
    }

    public void fetchAllTickets() {
        if (searchString.isEmpty()) {
            searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCompany(securityBean.getUser().getCompany()));
        } else {
            searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCompany(searchString, securityBean.getUser().getCompany()));
        }
    }

    // TODO: Check if ticket owner
    public void addEntryToTicket(String content) {
        Entry newEntry = new Entry(securityBean.getUser().getId(), content, new Date());
        currentTicket.addEntry(newEntry);
        saveEntry(newEntry);
        saveTicket(currentTicket);
        entryContent = "";
    }

    public void setEntryContent(String entryContent) {
        this.entryContent = entryContent;
    }

    public String getEntryContent() {
        return entryContent;
    }
}
