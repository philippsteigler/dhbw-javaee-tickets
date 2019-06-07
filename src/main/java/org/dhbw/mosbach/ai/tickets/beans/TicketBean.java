package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.Ticket;
import org.dhbw.mosbach.ai.tickets.security.CDIRoleCheck;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Named
@SessionScoped
@CDIRoleCheck
public class TicketBean implements Serializable {
    private static final long serialVersionUID = -1843025922631961397L;

    @Inject
    private TicketDAO ticketDAO;

    @Inject
    private SecurityBean securityBean;

    private List<Ticket> tickets;
    private List<Ticket> currentList;

    private Ticket currentTicket;

    private String ticketSearchString = "";

    private List<Ticket> ticketSearchResult;

    private static final String DETAIL = "detail";

    /**
     * Initializes data structures. This method will be called after the instance
     * has been created.
     */
    @PostConstruct
    public void init()
    {
        this.tickets = ticketDAO.getAllFullyLoaded();
        this.currentTicket = null;
        this.currentList = null;
    }

    public void doSearch()
    {
        ticketSearchResult = tickets;
        for (Ticket ticket: tickets) {
            if (ticket.getSubject().matches("(.*)" + ticketSearchString + "(.*)")) {
                ticketSearchResult.add(ticket);
            }
        }
    }

    public void doEditorSearchHome()
    {
        currentList = getEditorsTicketsHome();
        ticketSearchResult = currentList;
        for (Ticket ticket: currentList) {
            if (ticket.getSubject().matches("(.*)" + ticketSearchString + "(.*)")) {
                ticketSearchResult.add(ticket);
            }
        }
    }

    private List<Ticket> getEditorsTicketsHome() {
        //TODO Entries durchsuchen
        return tickets.stream().filter(ticket -> ticket.getEditorId() == securityBean.getUser().getId()).collect(Collectors.toList());
    }

    public void doEditorSearchTickets()
    {
        currentList = tickets;
        ticketSearchResult = currentList;
        for (Ticket ticket: currentList) {
            if (ticket.getSubject().matches("(.*)" + ticketSearchString + "(.*)")) {
                ticketSearchResult.add(ticket);
            }
        }
    }

    public void doCustomerSearchHome()
    {
        currentList = getCustomersTicketsHome();
        ticketSearchResult = currentList;
        for (Ticket ticket: currentList) {
            if (ticket.getSubject().matches("(.*)" + ticketSearchString + "(.*)")) {
                ticketSearchResult.add(ticket);
            }
        }
    }

    private List<Ticket> getCustomersTicketsHome() {
        //TODO Load all tickets Customer has created
        return tickets.stream().filter(ticket -> ticket.getCustomerId() == securityBean.getUser().getId()).collect(Collectors.toList());
    }

    public void doCustomerSearchTickets()
    {
        currentList = getCustomersTicketsTickets();
        ticketSearchResult = currentList;
        for (Ticket ticket: currentList) {
            if (ticket.getSubject().matches("(.*)" + ticketSearchString + "(.*)")) {
                ticketSearchResult.add(ticket);
            }
        }
    }

    private List<Ticket> getCustomersTicketsTickets() {
        //TODO Load all tickets Customer can see
        return null;
    }

    public List<Ticket> getTickets()
    {
        return tickets;
    }

    public String detail(long id) {
        this.currentTicket = tickets.stream().filter(ticket -> ticket.getId() == id).collect(Collectors.toList()).get(0);
        return DETAIL;
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
}
