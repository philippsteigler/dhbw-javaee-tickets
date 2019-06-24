package org.dhbw.mosbach.ai.tickets.view;

import org.dhbw.mosbach.ai.tickets.beans.AdminBean;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class AdminView implements Serializable {

    @Inject
    private AdminBean adminBean;

    // abhängig der ausgewählten Rolle wird die Unternehmensauswahl deaktiviert
    public boolean disableCompanySelection(String role) {
        if (role == null){
            return true;

        } else if (!role.equals("customer")) {

            // wenn als Rolle Admin oder Editor gewählt wird, wird der Unternehmensname auf "Ticket Master" gesetzt
            // und die Unternehmensauswahl deaktiviert
            adminBean.setCompanyName("Ticket Master");
            return true;
        } else {

            // wenn im aktuellen companyName noch "Ticket Master" steht, wird der Wert entfernt
            if (adminBean.getCompanyName().equals("Ticket Master")) {
                adminBean.setCompanyName("");
            }

            return false;
        }
    }
}
