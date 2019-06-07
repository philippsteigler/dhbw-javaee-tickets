package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Ticket;
import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class TicketBean extends AbstractBean {
    private static final long serialVersionUID = -2033869353508049867L;

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

    public void save(Ticket ticket)
    {
        ticketDAO.persistOrMerge(ticket);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "Ticket saved.");
    }

    public void create()
    {
        tickets.add(new Ticket());
    }

    public void delete(Ticket ticket)
    {
        ticketDAO.removeDetached(ticket);
        init();

        this.addFacesMessage(FacesMessage.SEVERITY_INFO, "Ticket deleted.");
    }

    public void setTickets(List<Ticket> tickets)
    {
        this.tickets = tickets;
    }

    public Ticket getCurrentSelection()
    {
        return currentSelection;
    }

    public void setCurrentSelection(Ticket currentSelection)
    {
        this.currentSelection = currentSelection;
    }
}
