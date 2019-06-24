package org.dhbw.mosbach.ai.tickets.beans;

import com.google.common.collect.ImmutableList;
import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.ejb.MailService;
import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.User;
import org.dhbw.mosbach.ai.tickets.security.CDIRoleCheck;
import org.dhbw.mosbach.ai.tickets.view.AdminView;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Klasse zur Verarbeitung von Benutzern.
 */
@Named
@SessionScoped
@CDIRoleCheck
@RolesAllowed(value = { Roles.ADMIN })
public class AdminBean extends AbstractBean {
    private static final long serialVersionUID = -7105806000082771152L;

    // Notwendige CDI-Beans werden an dieser Stelle eingebunden.
    @Inject
    private UserDAO userDAO;

    @Inject
    private SecurityBean securityBean;

    // Aktuelles User-Objekt wird in dieser Variable zur Verarbeitung gehalten
    private User currentUser;

    // Bei der Suche in der Benutzer-Liste wird die Eingabe des Suchfeldes hier gespeichert und ausgewertet.
    private List<User> searchResult;
    private String searchString = "";

    // Kürzel für Redirects im Frontend, definiert in der Datei faces-config.xml
    private static final String VIEW_DETAILS = "admin-user-details";
    private static final String VIEW_USERS = "admin-all-users";

    // Getter und Setter
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public List<User> getSearchResult() {
        return searchResult;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // Methode zum Löschen eines Users in die Datenbank über das UserDAO.
    //
    // Diese Methode kann nur vom Admin über die Detailansicht aufgerufen werden!
    @RolesAllowed(value = { Roles.ADMIN })
    public String deleteUser(User user) {
        userDAO.removeDetached(user);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.deleteSuccess");

        // Nach dem Löschen zurück von der Dateilansicht zur Benutzer-Liste.
        return VIEW_USERS;
    }

    // Speicher den aktuellen User, dessen Details angezeigt werden sollen.
    // Anschließend wird auf die Detailansicht des ausgewählten Users weitergeleitet.
    public String viewUserDetails(User user) {
        this.currentUser = user;

        return VIEW_DETAILS;
    }

    // Methode zur Auflistung aller User, die in der Datenbank hinterlegt sind.
    //
    // Nur der Administrator darf auf diese kritischen Daten zugreifen!
    @RolesAllowed(value = { Roles.ADMIN })
    public void fetchAllUsers() {
        if (searchString.isEmpty()) {
            searchResult = ImmutableList.copyOf(userDAO.getAll());
        } else {
            searchResult = ImmutableList.copyOf(userDAO.getUsersContainingName(searchString));
        }
    }

    // Gib alle möglichen Editoren zurück. Wird im Frontend zum Delegieren von Tickets benötigt.
    // Der eigene User wird nicht zum Delegieren aufgelistet.
    public List<User> getEditors() {
        return userDAO.getAll().stream().filter(user -> user.getRoles().stream().allMatch(role -> role.getName().equals("editor")
                && user.getId() != securityBean.getUser().getId())).collect(Collectors.toList());
    }

    // Methode zur Übersetzung von Benutzer-IDs in Benutzernamen.
    //
    // Wird zur schöneren Anzeige im Frontend benötigt, da für Tickets nur die IDs der User hinterlegt sind.
    public String getUserName(long id) {
        List<User> findUser = userDAO.getAll().stream().filter(user -> user.getId() == id).collect(Collectors.toList());
        if (!findUser.isEmpty()) {
            return findUser.get(0).getName();
        } else return "None";

    }

    // Methode zur Übersetzung von Benutzer-IDs in Unternehmen.
    //
    // Wird zur schöneren Anzeige in den Ticket-Details benötigt, um die Firma eines Users anzuzeigen, da nur dessen
    // User-ID aus einem Ticket bekannt ist.
    public String getUserCompany(long id) {
        return userDAO.getAll().stream().filter(user -> user.getId() == id).collect(Collectors.toList()).get(0).getCompany();
    }
}
