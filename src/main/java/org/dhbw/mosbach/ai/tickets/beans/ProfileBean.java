package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.Ticket;
import org.dhbw.mosbach.ai.tickets.model.User;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Max;
import java.util.List;

/**
 * Klasse zur Verwaltung des eigenen Benutzeraccounts über die Ansicht "Mein Profil".
 */
@Named
@ViewScoped
public class ProfileBean extends AbstractBean {

    // Notwendige CDI-Beans werden an dieser Stelle eingebunden.
    @Inject
    private UserDAO userDAO;

    @Inject
    private TicketDAO ticketDAO;

    @Inject
    private SecurityBean securityBean;
    
    // Passwort welches neu gesetzt werden soll
    private String password;

    // Methode zum Löschen des eigenen Benutzerkontos über das UserDAO.
    // Darf von jedem Benutzer ausgeführt werden.
    @PermitAll
    public String deleteAccount() {
        if (securityBean.isAuthenticated()) {
            // Tickets von Bearbeiter freigeben
            releaseTicketsIfEditorIsDeleted(securityBean.getUser());

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


    // Falls ein Bearbeiter gelöscht wird, sollen alle Tickets die er in Bearbeitung hat in den Status offen gesetzt werden
    @RolesAllowed(value = { Roles.EDITOR })
    private void releaseTicketsIfEditorIsDeleted(User deletedEditor) {

        // Alle Tickets des zu löschenden Bearbeiters laden
        List<Ticket> tickets = ticketDAO.getAllTicketsForEditorID(deletedEditor.getId());

        for (Ticket ticket : tickets) {
            ticket.setStatusToOpen();
            ticket.setEditorId(0);

            ticketDAO.persistOrMerge(ticket);
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
