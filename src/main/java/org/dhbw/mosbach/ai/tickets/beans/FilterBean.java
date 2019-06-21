package org.dhbw.mosbach.ai.tickets.beans;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.util.Objects;

@Named
@SessionScoped
public class FilterBean extends AbstractBean {
    private String selectedOption = "";

    public String getSelectedOptionForAllTickets() {
        return selectedOption;
    }

    public void setSelectedOptionForAllTickets(String selectedOption) {
        this.selectedOption = Objects.requireNonNullElse(selectedOption, "");
    }
}
