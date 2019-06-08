package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.EntryDAO;
import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Entry;
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
import javax.faces.application.FacesMessage;
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
public class TicketBean extends AbstractBean {
    private static final long serialVersionUID = -1843025922631961397L;

    @Inject
    private TicketDAO ticketDAO;

    @Inject
    private SecurityBean securityBean;

    private List<Ticket> tickets;
    private List<Ticket> currentList;

    private Ticket currentTicket;

    private List<Entry> currentEntries;

    private String ticketSearchString = "";

    private List<Ticket> ticketSearchResult;

    private static final String DETAIL = "detail";

    private boolean rendered = false;

    /**
     * Initializes data structures. This method will be called after the instance
     * has been created.
     */
    @PostConstruct
    public void init()
    {
        this.tickets = ticketDAO.getAllFullyLoaded();
        this.currentTicket = null;
        this.currentList = new ArrayList<>();
    }

    private void doSearch(List<Ticket> searchThis)
    {
        List<Ticket> disposableList = new ArrayList<>();
        for (Ticket ticket: searchThis) {
            if (ticket.getSubject().matches("(.*)" + ticketSearchString + "(.*)")) {
                disposableList.add(ticket);
            }
        }

        checkRender(disposableList.size());
        ticketSearchResult = disposableList;
    }

    private void checkRender(int size) {
        if (size < 1) {
            rendered = false;
        } else {
            rendered = true;
        }
    }

    public void doEditorSearchHome()
    {
        doSearch(getEditorsTicketsHome());
    }

    private List<Ticket> getEditorsTicketsHome() {
        //TODO Entries durchsuchen
        return tickets.stream().filter(ticket -> ticket.getEditorId() == securityBean.getUser().getId()).collect(Collectors.toList());
    }

    public void doEditorSearchTickets()
    {
        doSearch(tickets);
    }

    public void doCustomerSearchHome()
    {
       doSearch(getCustomersTicketsHome());
    }

    private List<Ticket> getCustomersTicketsHome() {
        //TODO Load all tickets Customer has created
        return tickets.stream().filter(ticket -> ticket.getCustomerId() == securityBean.getUser().getId()).collect(Collectors.toList());
    }

    public void doCustomerSearchTickets()
    {
        doSearch(getCustomersTicketsTickets());
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
        getTicketEntries(id);
        return DETAIL;
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

    public void save(Ticket ticket)
    {
        ticketDAO.persistOrMerge(ticket);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "Ticket erfolgreich gespeichert.");
    }

    public void create()
    {
        tickets.add(new Ticket());
    }

    public void delete(Ticket ticket)
    {
        ticketDAO.removeDetached(ticket);
        init();

        this.addFacesMessage(FacesMessage.SEVERITY_INFO, "Ticket erfolgreich gelöscht.");
    }

    public void setTickets(List<Ticket> tickets)
    {
        this.tickets = tickets;
    }

    public void setCurrentTicket(Ticket currentSelection)
    {
        this.currentTicket = currentSelection;
    }

    public void delegateTicket(){}

    public void addEntry() {}

    public void freeTicket() {}
}
