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

@Named
@SessionScoped
public class TicketEditorBean extends AbstractBean {

    @Inject
    private TicketDAO ticketDAO;

    @Inject
    private EntryDAO entryDAO;

    @Inject
    private SecurityBean securityBean;

    private List<Ticket> tickets;
    private Ticket currentTicket;
    private List<Entry> currentEntries;

    private boolean rendered = false;
    private List<Ticket> searchResult;
    private String searchString = "";
    private String entryContent = "";

    private static final String VIEW_DETAILS = "editor-ticket-details";
    private String RETURN_ADDRESS = "";

    @PostConstruct
    public void init() {
        this.tickets = ticketDAO.getAllFullyLoaded();
        this.currentTicket = null;
    }

    // Tickets erzeugen und löschen
    private void saveTicket(Ticket ticket) {
        ticketDAO.persistOrMerge(ticket);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "ticket.saveSuccess");
    }

    private void saveEntry(Entry entry) {
        entryDAO.persistOrMerge(entry);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "entry.saveSuccess");
    }

    /*
    public void createTicket() {
        tickets.add(new Ticket());
    }

    public void deleteTicket(Ticket ticket) {
        ticketDAO.removeDetached(ticket);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "ticket.deleteSuccess");
    }
     */




    // Search in Editors tickets
    private void checkRender(int size) {
        rendered = size >= 1;
    }

    public boolean isRendered() {
        return rendered;
    }

    private void doSearch(List<Ticket> searchThis) {
        List<Ticket> disposableList = new ArrayList<>();
        for (Ticket ticket: searchThis) {
            if (ticket.getSubject().matches("(.*)" + searchString + "(.*)")) {
                disposableList.add(ticket);
            }
        }

        checkRender(disposableList.size());
        searchResult = disposableList;
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

    // Filter tickets currently owned by the current editor
    public void fetchMyTickets() {
        doSearch(tickets.stream().filter(ticket -> ticket.getEditorId() == securityBean.getUser().getId()).collect(Collectors.toList()));
    }

    // Get all tickets
    public void fetchAllTickets() {
        doSearch(tickets);
    }








    // View ticket details
    public Ticket getCurrentTicket() {
        return currentTicket;
    }

    public List<Entry> getCurrentEntries() {
        return currentEntries;
    }

    private void fetchTicketEntries() {
        this.currentEntries = currentTicket.getEntries();
    }

    public String viewTicketDetails(long id, String returnAddress) {
        this.currentTicket = tickets.stream().filter(ticket -> ticket.getId() == id).collect(Collectors.toList()).get(0);
        RETURN_ADDRESS = returnAddress;
        fetchTicketEntries();

        return VIEW_DETAILS;
    }

    public void setEntryContent(String entryContent){ this.entryContent = entryContent; }

    public String getEntryContent(){ return entryContent; }


    // Edit ticket details
    public void addEntryToTicket(String content) {
        Entry newEntry = new Entry(securityBean.getUser().getId(), content, new Date());
        currentTicket.addEntry(newEntry);
        saveTicket(currentTicket);
        saveEntry(newEntry);

        //viewTicketDetails(currentTicket.getId(), RETURN_ADDRESS);
    }

    public String delegateTicket(long editorId){
        currentTicket.setEditorId(editorId);
        currentTicket.setStatusToInProcess();
        saveTicket(currentTicket);

        return RETURN_ADDRESS;
    }

    public String releaseTicket() {
        currentTicket.setStatusToOpen();
        currentTicket.setEditorId(0);
        saveTicket(currentTicket);

        return RETURN_ADDRESS;
    }
}
