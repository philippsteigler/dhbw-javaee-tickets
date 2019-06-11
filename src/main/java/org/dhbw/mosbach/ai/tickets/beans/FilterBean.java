package org.dhbw.mosbach.ai.tickets.beans;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class FilterBean extends AbstractBean {

    private String selectedOption = "";

    public String getSelectedOptionForAllTickets() {
        return selectedOption;
    }

    public void setSelectedOptionForAllTickets(String selectedOption) {
        if (selectedOption.equals("all")) {
            this.selectedOption = "";
        } else {
            this.selectedOption = selectedOption;
        }
    }
}
