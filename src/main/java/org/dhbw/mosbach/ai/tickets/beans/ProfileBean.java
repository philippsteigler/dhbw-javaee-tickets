package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.UserDAO;

import javax.annotation.security.PermitAll;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ViewScoped
public class ProfileBean extends AbstractBean {

    // Notwendige CDI-Beans werden an dieser Stelle eingebunden.
    @Inject
    private UserDAO userDAO;

    @Inject
    private SecurityBean securityBean;

    // Methode zum Löschen des eigenen Benutzerkontos über das UserDAO.
    // Darf von jedem Benutzer ausgeführt werden.
    @PermitAll
    public String deleteAccount() {
        if (securityBean.isAuthenticated()) {
            // Lösche den eigenen Benutzer aus der Datenbank.
            userDAO.removeDetached(securityBean.getUser());
            addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.deleteSuccess");

            // Nach dem Löschen direkt ausloggen.
            return securityBean.logout();
        }

        return "";
    }

    // Methode zum Ändern des eigenen Passworts.
    // Darf von jedem Benutzer ausgeführt werden.
    @PermitAll
    public void changePassword(String password) {
        if (securityBean.isAuthenticated()) {
            // Ändere das Passwort des aktuell eingeloggten Benutzers.
            userDAO.changePassword(securityBean.getUser(), password);

            // Speichere die Änderungen direkt in die Datenbank.
            userDAO.persistOrMerge(securityBean.getUser());
            addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.saveSuccess");
        }
    }
}
