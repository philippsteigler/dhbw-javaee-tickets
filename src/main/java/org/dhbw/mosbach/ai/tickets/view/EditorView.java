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

    private boolean renderTakeButton;
    private boolean renderReleaseButton;
    private boolean renderAddEntryButton;
    private boolean renderDelegateButton;
    private boolean renderCloseTicket;

    public boolean isRenderAddEntryButton() {
        return renderAddEntryButton;
    }

    public boolean isRenderReleaseButton() {
        return renderReleaseButton;
    }

    public boolean isRenderTakeButton() {
        return renderTakeButton;
    }

    public boolean isRenderCloseTicket() {
        return renderCloseTicket;
    }

    public boolean isRenderDelegateButton() {
        return renderDelegateButton;
    }

    //called in .xhtml to
    public void renderButton(){

        long editorId = securityBean.getUser().getId();
        Ticket currentTicket = ticketEditorBean.getCurrentTicket();


        //guarantees that all booleans are set to false
        renderTakeButton = false;
        renderReleaseButton = false;
        renderAddEntryButton = false;
        renderDelegateButton = false;
        renderCloseTicket = false;

        //ticket of logged in editor
        if (editorId == currentTicket.getId() && currentTicket.getStatus() == Ticket.Status.inProcess) {
            renderAddEntryButton = true;

        }


    }
}
