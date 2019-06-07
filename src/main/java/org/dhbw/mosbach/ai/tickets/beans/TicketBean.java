package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@ViewScoped
public class TicketBean extends AbstractBean {
    private static final long serialVersionUID = -1787635692784520008L;

    @Inject
    private TicketDAO ticketDAO;

    private List<Ticket> tickets;

    private Ticket currentSelection;

    /**
     * Initializes data structures. This method will be called after the instance
     * has been created.
     */
    @PostConstruct
    public void init()
    {
        this.tickets = ticketDAO.getAllFullyLoaded();
        this.currentSelection = null;
    }

    public List<Ticket> getTickets()
    {
        return tickets;
    }
}
