package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.ejb.TicketDAOProxy;
import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class TicketBean implements Serializable {
    private static final long serialVersionUID = -1843025922631961397L;

    @EJB
    private TicketDAOProxy ticketDAOProxy;

    private List<Ticket> tickets;

    private Ticket currentSelection;

    /**
     * Initializes data structures. This method will be called after the instance
     * has been created.
     */
    /*@PostConstruct
    public void init()
    {
        this.tickets = ticketDAOProxy.getAllFullyLoaded();
        this.currentSelection = null;
    }*/

    @PermitAll
    public List<Ticket> getAllArticles()
    {
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
}
