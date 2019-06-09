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


    private long currentEditorId() {
        return securityBean.getUser().getId();
    }

    private Ticket currentTicket() {
        return ticketEditorBean.getCurrentTicket();
    }

    public boolean renderButton(String button) {

        Ticket currentTicket = currentTicket();

        switch(button){

            case "take":
                return currentTicket.getEditorId() == 0 && currentTicket.getStatus() == Ticket.Status.open;

            case "delegate":
                return (currentTicket.getEditorId() == 0 && currentTicket.getStatus() == Ticket.Status.open)
                        || (currentEditorId() == currentTicket().getEditorId());

            case "addEntry":
            case "release":
                return currentEditorId() == currentTicket().getEditorId();

            default:
                return false;
        }
    }
}
