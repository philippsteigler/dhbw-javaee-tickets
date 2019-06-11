package org.dhbw.mosbach.ai.tickets.beans;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class FilterBean extends AbstractBean {

    @Inject
    TicketEditorBean ticketEditorBean;

    private String selectedOption = "";

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        if (selectedOption.equals("all")) {
            this.selectedOption = "";
        } else {
            this.selectedOption = selectedOption;
        }
        ticketEditorBean.fetchAllTickets();
    }

}
