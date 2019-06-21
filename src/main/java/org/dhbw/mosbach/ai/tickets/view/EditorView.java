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

    // Button wird abhängig vom eingeloggten Bearbeiter und dem ausgewählten Ticket dargestellt
    public boolean renderButton(String button) {

        //hole aktuelles Ticket und eingeloggten Bearbeiter
        Ticket currentTicket = ticketEditorBean.getCurrentTicket();
        long currentEditorId = securityBean.getUser().getId();

        //für jeden Button werden spezielle Anforderungen überprüft
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
