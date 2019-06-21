package org.dhbw.mosbach.ai.tickets.view;

import org.dhbw.mosbach.ai.tickets.beans.SecurityBean;
import org.dhbw.mosbach.ai.tickets.beans.TicketEditorBean;
import org.dhbw.mosbach.ai.tickets.model.Ticket;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class EditorView implements Serializable {

    @Inject
    private SecurityBean securityBean;

    @Inject
    private TicketEditorBean ticketEditorBean;

    //render button depending on logged-in user and selected ticket
    public boolean renderButton(String button) {

        //Get current Ticket and EditorId
        Ticket currentTicket = ticketEditorBean.getCurrentTicket();
        long currentEditorId = securityBean.getUser().getId();

        //check specific requirements for each button
        switch(button){

            case "reopen":
                return currentTicket.getStatus() == Ticket.Status.closed;

            case "take":
                return currentTicket.getEditorId() == 0 && currentTicket.getStatus() == Ticket.Status.open;

            case "delegate":
                return (currentTicket.getEditorId() == 0 && currentTicket.getStatus() == Ticket.Status.open)
                        || (currentEditorId == currentTicket.getEditorId());

            case "addEntry":
            case "release":
            case "close":
                return currentEditorId == currentTicket.getEditorId();

            default:
                return false;
        }
    }
}
