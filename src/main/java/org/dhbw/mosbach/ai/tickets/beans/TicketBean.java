package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.EntryDAO;
import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Entry;
import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named("ticketBean")
@SessionScoped
public class TicketBean extends AbstractBean {
    private static final long serialVersionUID = -1843025922631961397L;

    @Inject
    private TicketDAO ticketDAO;

    @Inject
    private EntryDAO entryDAO;

    @Inject
    private SecurityBean securityBean;

    @Inject UserBean userBean;

    private List<Ticket> tickets;

    private Ticket currentTicket;

    private List<Entry> currentEntries;

    private String ticketSearchString = "";

    private String entryContent;

    private List<Ticket> ticketSearchResult;

    private static final String VIEW_DETAIL = "detail";

    private boolean rendered = false;

    /**
     * Initializes data structures. This method will be called after the instance
     * has been created.
     */
    @PostConstruct
    public void init() {
        this.tickets = ticketDAO.getAllFullyLoaded();
        this.currentTicket = null;
    }

    List<Ticket> doSearch(List<Ticket> searchThis, String searchString) {
        List<Ticket> disposableList = new ArrayList<>();
        for (Ticket ticket: searchThis) {
            if (ticket.getSubject().matches("(.*)" + searchString + "(.*)")) {
                disposableList.add(ticket);
            }
        }

        return disposableList;
    }
    /*
    public void doCustomerSearchHome()
    {
       ticketSearchResult = doSearch(getCustomersTicketsHome(), ticketSearchString);
    }

    private List<Ticket> getCustomersTicketsHome() {
        //TODO Load all tickets Customer has created
        return tickets.stream().filter(ticket -> ticket.getCustomerId() == securityBean.getUser().getId()).collect(Collectors.toList());
    }

    public void doCustomerSearchTickets()
    {
        ticketSearchResult = doSearch(tickets.stream().filter(ticket -> userBean.getUserCompany(ticket.getCustomerId()).equals(securityBean.getUser().getCompany())).collect(Collectors.toList()), ticketSearchString);
    }
    */

    public List<Ticket> getTickets()
    {
        return tickets;
    }

    public String detail(long id) {
        this.currentTicket = tickets.stream().filter(ticket -> ticket.getId() == id).collect(Collectors.toList()).get(0);
        getTicketEntries(id);
        return VIEW_DETAIL;
    }

    private void getTicketEntries(long id) {
        this.currentEntries = currentTicket.getEntries();
    }

    public void setTicketSearchString(String ticketSearchString) {
        this.ticketSearchString = ticketSearchString;
    }

    public String getTicketSearchString() {
        return ticketSearchString;
    }

    public List<Ticket> getTicketSearchResult() {
        return ticketSearchResult;
    }

    public Ticket getCurrentTicket() {
        return currentTicket;
    }

    public List<Entry> getCurrentEntries() {
        return currentEntries;
    }


    public boolean isRendered() {
        return rendered;
    }

    private void saveTicket(Ticket ticket)
    {
        ticketDAO.persistOrMerge(ticket);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "ticket.saveSuccess");
    }

    private void saveEntry(Entry entry)
    {
        entryDAO.persistOrMerge(entry);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "entry.saveSuccess");
    }

    public void setTickets(List<Ticket> tickets)
    {
        this.tickets = tickets;
    }

    public void setCurrentTicket(Ticket currentSelection)
    {
        this.currentTicket = currentSelection;
    }

}
