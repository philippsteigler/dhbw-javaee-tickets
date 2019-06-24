package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.UserDAO;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class ProfileBean extends AbstractBean {

    // Notwendige CDI-Beans werden an dieser Stelle eingebunden.
    @Inject
    private UserDAO userDAO;

    @Inject
    private SecurityBean securityBean;

    // Methode zum Löschen des eigenen Benutzerkontos über das UserDAO.
    //
    // Darf von jedem Benutzer ausgeführt werden.
    @PermitAll
    public String deleteAccount() {
        // Lösche den eigenen Benutzer aus der Datenbank.
        userDAO.removeDetached(securityBean.getUser());
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.deleteSuccess");

        // Nach dem Löschen direkt ausloggen.
        return securityBean.logout();
    }
}
