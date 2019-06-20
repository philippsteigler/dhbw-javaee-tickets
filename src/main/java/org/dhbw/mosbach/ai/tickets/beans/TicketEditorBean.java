package org.dhbw.mosbach.ai.tickets.beans;

import com.google.common.collect.ImmutableList;
import org.dhbw.mosbach.ai.tickets.database.EntryDAO;
import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.Entry;
import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
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

    @Inject
    private FilterBean filterBean;

    private static final String VIEW_DETAILS = "editor-ticket-details";
    private String REDIRECT = "";

    private Ticket currentTicket;
    private List<Entry> currentEntries;

    private List<Ticket> searchResult;
    private String searchString = "";

    private String entryContent = "";

    private void saveTicket(Ticket ticket) {
        ticketDAO.persistOrMerge(ticket);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "ticket.saveSuccess");
    }

    private void saveEntry(Entry entry) {
        entryDAO.persistOrMerge(entry);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "entry.saveSuccess");
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

    public Ticket getCurrentTicket() {
        return currentTicket;
    }

    public List<Entry> getCurrentEntries() {
        return currentEntries;
    }

    public String viewTicketDetails(Ticket ticket, String redirect) {
        currentTicket = ticket;
        currentEntries = currentTicket.getEntries();
        REDIRECT = redirect;

        return VIEW_DETAILS;
    }

    public void fetchMyTickets() {
        if (searchString.isEmpty()) {
            searchResult = ImmutableList.copyOf(ticketDAO.getAllTicketsForEditorID(securityBean.getUser().getId()));
        } else {
            searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubjectForEditorID(searchString, securityBean.getUser().getId()));
        }
    }

    public void fetchAllTickets() {
        if (searchString.isEmpty() && filterBean.getSelectedOptionForAllTickets().isEmpty()) {
            searchResult = ImmutableList.copyOf(ticketDAO.getAll());
        } else {
            if (filterBean.getSelectedOptionForAllTickets().isEmpty()) {
                searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubject(searchString));
            } else {
                if (searchString.isEmpty()) {
                    searchResult = ImmutableList.copyOf(ticketDAO.getAll()).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                } else {
                    searchResult = ImmutableList.copyOf(ticketDAO.getTicketsContainingSubject(searchString)).stream().filter(
                            ticket -> filterBean.getSelectedOptionForAllTickets().equals(ticket.getStatus().toString())
                    ).collect(Collectors.toList());
                }
            }
        }
    }

    public void addEntryToTicket(String content) {
        Entry newEntry = new Entry(securityBean.getUser().getId(), content, new Date());
        currentTicket.addEntry(newEntry);
        saveEntry(newEntry);
        saveTicket(currentTicket);
        entryContent = "";
    }

    public void setEntryContent(String entryContent) {
        this.entryContent = entryContent;
    }

    public String getEntryContent() {
        return entryContent;
    }

    public String delegateTicket(long editorId){
        currentTicket.setEditorId(editorId);
        currentTicket.setStatusToInProcess();
        saveTicket(currentTicket);

        currentTicket = null;
        return REDIRECT;
    }

    public String releaseTicket() {
        // TODO: Only release MY tickets
        currentTicket.setStatusToOpen();
        currentTicket.setEditorId(0);
        saveTicket(currentTicket);

        currentTicket = null;
        return REDIRECT;
    }

    public String takeTicket(long id) {
        currentTicket.setStatusToInProcess();
        currentTicket.setEditorId(id);
        saveTicket(currentTicket);

        currentTicket = null;
        return REDIRECT;
    }

    public String closeTicket() {
        currentTicket.setStatusToClose();
        currentTicket.setEditorId(0);
        saveTicket(currentTicket);

        currentTicket = null;
        return REDIRECT;
    }

    public void reopenTicket() {
        currentTicket.setStatusToOpen();
        saveTicket(currentTicket);
    }



}
