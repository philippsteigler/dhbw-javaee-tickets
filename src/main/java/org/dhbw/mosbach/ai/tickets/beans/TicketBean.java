package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.ejb.TicketDAOProxy;
import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
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
    @PostConstruct
    public void init()
    {
        this.tickets = ticketDAOProxy.getAllFullyLoaded();
        this.currentSelection = null;
    }

    @PermitAll
    public List<Ticket> getAllArticles()
    {
        return ticketDAOProxy.getAll();
    }
}
