package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.model.User;

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

    // Passwort welches neu gesetzt werden soll
    private String password;

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
    public String changePassword(String password) {
        if (securityBean.isAuthenticated()) {
            User user = securityBean.getUser();

            // Ändere das Passwort des aktuell eingeloggten Benutzers.
            userDAO.changePassword(user, password);

            // Speichere die Änderungen direkt in die Datenbank.
            userDAO.persistOrMerge(user);
            addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.changePassword");

            // Zur Bestätigung ausloggen und Eingabe des neuen Passworts verlangen.
            return securityBean.logout();
        }

        return "";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
