package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class TicketBean implements Serializable {
    private static final long serialVersionUID = -1843025922631961397L;

    @Inject
    private TicketDAO ticketDAO;

    private List<Ticket> tickets;

    private Ticket currentSelection;

    private String ticketSearchString = "";

    private List<Ticket> ticketSearchResult;

    private static final String TICKET_CLICK = "dashboard";

    /**
     * Initializes data structures. This method will be called after the instance
     * has been created.
     */
    @PostConstruct
    public void init()
    {
        this.tickets = ticketDAO.getAllFullyLoaded();
        this.currentSelection = null;
    }*/

    public void doSearch()
    {
        //TODO
        // final List<Ticket> ticketSearchResultList = getMatchingTickets(ticketSearchString);
        // ticketSearchResult = ticketSearchResultList.isEmpty() ? null : ticketSearchResultList.get(0);

        List<Ticket> ticketList = new ArrayList<>();
        for(int i = 0; i < 30; i+=3) {
            Ticket ticket1 = new Ticket("Roman", Ticket.Status.open, i);
            Ticket ticket2 = new Ticket("Jarno", Ticket.Status.closed, i+1);
            Ticket ticket3 = new Ticket("Philipp", Ticket.Status.inProcess, i+2);
            ticketList.add(ticket1);
            ticketList.add(ticket2);
            ticketList.add(ticket3);
        }

        ticketSearchResult = new ArrayList<>();
        for (Ticket ticket: ticketList) {
            if (ticket.getSubject().matches("(.*)" + ticketSearchString + "(.*)")) {
                ticketSearchResult.add(ticket);
            }
        }
    }

    public List<Ticket> getTickets()
    {
        return tickets;
        List<Ticket> ticketList = new ArrayList<>();
        for(int i = 0; i < 30; i+=3) {
            Ticket ticket1 = new Ticket("Test", Ticket.Status.open, i);
            Ticket ticket2 = new Ticket("Test", Ticket.Status.closed, i+1);
            Ticket ticket3 = new Ticket("Test", Ticket.Status.inProcess, i+2);
            ticketList.add(ticket1);
            ticketList.add(ticket2);
            ticketList.add(ticket3);
        }

        return ticketList;
    }

    public String ticketClick() {
        return TICKET_CLICK;
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
}
