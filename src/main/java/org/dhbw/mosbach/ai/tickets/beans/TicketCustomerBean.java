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
        if (searchString.isEmpty() && filterBean.getSelectedOptionForAllTickets().isEmpty()) {
            searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCustomerID(securityBean.getUser().getId()));
        } else {
            if (filterBean.getSelectedOptionForAllTickets().isEmpty()) {
                searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCustomerID(searchString, securityBean.getUser().getId()));
            } else {
                if (searchString.isEmpty()) {
                    searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCustomerID(securityBean.getUser().getId())).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                } else {
                    searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCustomerID(searchString, securityBean.getUser().getId())).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                }
            }
        }
    }

    public void fetchAllTickets() {
        if (searchString.isEmpty() && filterBean.getSelectedOptionForAllTickets().isEmpty()) {
            searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCompany(securityBean.getUser().getCompany()));
        } else {
            if (filterBean.getSelectedOptionForAllTickets().isEmpty()) {
                searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCompany(searchString, securityBean.getUser().getCompany()));
            } else {
                if (searchString.isEmpty()) {
                    searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForCompany(securityBean.getUser().getCompany())).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                } else {
                    searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForCompany(searchString, securityBean.getUser().getCompany())).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                }
            }
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

    public String newTicket(String content, String subject) {
        Ticket newTicket = new Ticket(subject, Ticket.Status.open, content, 0, securityBean.getUser().getId());
        Entry newEntry = new Entry(securityBean.getUser().getId(), content, new Date());
        newTicket.addEntry(newEntry);
        saveEntry(newEntry);
        saveTicket(newTicket);
        ticketContent = "";
        ticketSubject = "";

        return "customer-my-tickets";
    }

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
}
