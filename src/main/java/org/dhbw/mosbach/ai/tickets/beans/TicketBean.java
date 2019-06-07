package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Ticket;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
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
public class TicketBean implements Serializable {
    private static final long serialVersionUID = -1843025922631961397L;

    @Inject
    private TicketDAO ticketDAO;

    private List<Ticket> tickets;

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
        this.ticketSearchResult = tickets;
    }

    public void doSearch()
    {
        //TODO
        // final List<Ticket> ticketSearchResultList = getMatchingTickets(ticketSearchString);
        // ticketSearchResult = ticketSearchResultList.isEmpty() ? null : ticketSearchResultList.get(0);

        ticketSearchResult = new ArrayList<>();
        for (Ticket ticket: tickets) {
            if (ticket.getSubject().matches("(.*)" + ticketSearchString + "(.*)")) {
                ticketSearchResult.add(ticket);
            }
        }
    }

    public List<Ticket> getTickets()
    {
        return tickets;
    }
}
