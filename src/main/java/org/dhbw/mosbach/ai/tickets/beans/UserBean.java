package org.dhbw.mosbach.ai.tickets.beans;

import com.google.common.collect.ImmutableList;
import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.ejb.MailService;
import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.User;
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
public class UserBean extends AbstractBean {
    private static final long serialVersionUID = -7105806000082771152L;

    // Notwendige CDI-Beans werden an dieser Stelle eingebunden.
    @Inject
    private UserDAO userDAO;

    @Inject
    private SecurityBean securityBean;

    @Inject
    private AdminView adminView;

    @EJB
    private MailService mailService;

    // Aktuelles User-Objekt wird in dieser Variable zur Verarbeitung gehalten
    private User currentUser;

    // Bei der Suche in der Benutzer-Liste wird die Eingabe des Suchfeldes hier gespeichert und ausgewertet.
    private List<User> searchResult;
    private String searchString = "";

    private List<Role> roles;
    private List<String> companies;

    // Kürzel für Redirects im Frontend, definiert in der Datei faces-config.xml
    private static final String VIEW_DETAILS = "admin-user-details";
    private static final String VIEW_USERS = "admin-all-users";

    // Methode zum Anlegen neuer Benutzer von der Webseite aus.
    //
    // Alle Eingaben der Web-Maske werden hier übernommen und verarbeitet.
    // Nur Admins dürfen neue Benutzer anlegen!
    @RolesAllowed(value = { Roles.ADMIN })
    public String newUser(String login_id, String name, String companyName, String email, String password, String role) {

        // überprüft ob die login_id bereits existiert (Validierung ist notwendig, da die login_id einzigartig ist)
        if (checkIfLoginIdExist(login_id)) {

            // leert das Input Feld und gibt Fehlernachricht aus
            adminView.setLogin_id("");
            addLocalizedFacesMessage(FacesMessage.SEVERITY_FATAL, "admin.loginId.duplicated");

            // Redirect auf die gleiche Seite.
            return null;
        } else {

            // erstellt neuen Benutzer und speichert ihn
            final User user = new User(login_id, name, companyName, email);
            user.getRoles().add(parseRoles(role));
            userDAO.changePassword(user, password);
            saveUser(user);

            // Bestätigungsmail versenden
            mailService.sendConfirmationMail(email, login_id, password);

            // Setze alle Eingabefelder der Web-Maske zurück.
            adminView.setName("");
            adminView.setLogin_id("");
            adminView.setEmail("");
            adminView.setRole("customer");
            adminView.setCompanyName("");
            adminView.setPassword("");

            // Seite verlassen und auf die Benutzerübersicht zurückkehren
            return VIEW_USERS;
        }
    }

    // Nur der Administrator darf auf diese kritischen Daten zugreifen!
    @RolesAllowed(value = { Roles.ADMIN })
    private boolean checkIfLoginIdExist(String loginId) {

        // alle login_ids aus der Datenbank laden und überprüfen, ob die übergebene loginId bereits existiert
        List<String> loginIds = userDAO.getLoginIds();

        return loginIds.contains(loginId);
    }

    // Methode zum Parsen von Rollen aus der Web-Maske beim Anlegen neuer Benutzer.
    // Diese werden als Typ String vom Frontend übergeben und müssen auf existierende Rollen der Datenbank übertragen werden.
    private Role parseRoles(String role){

        // Durchlaufe alle übergebenen Rollen.
        for (Role roleFromDatabase : roles) {

            // Sofern für die Zeichenkette eine passende Rolle existiert, gib diese zurück.
            if (role.equals(roleFromDatabase.getName())) {
                return roleFromDatabase;
            }
        }

        // Ansonsten gib Null zurück, wenn keine passende Rolle existiert.
        return null;
    }

    // Methode zum Speichern des neuen Users in die Datenbank über das UserDAO.
    //
    // Nur Admins dürfen über diese Methode neue User in die Datenbank speichern!
    @RolesAllowed(value = { Roles.ADMIN })
    private void saveUser(User user) {
        userDAO.persistOrMerge(user);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.saveSuccess");
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

    public List<String> getCompanies() {
        return companies;
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

    // Methode zum Abrufen aller verfügbaren Rollen.
    // Wird für die anzeige der entsprechenden Rollen in der Benutzer-Liste und -Detailansicht benötigt.
    //
    // Nur der Administrator darf auf diese kritischen Daten zugreifen!
    @RolesAllowed(value = { Roles.ADMIN })
    public void fetchAllRoles() {
        roles = userDAO.getRoles();
    }

    // Methode zum Abrufen aller existierenden Unternehmen.
    // Wird für Vorschläge beim Anlegen neuer Benutzer benötigt.
    //
    // Nur der Administrator darf auf diese kritischen Daten zugreifen!
    @RolesAllowed(value = { Roles.ADMIN })
    public void fetchAllCompanies() {
        companies = new ArrayList<>();
        companies = userDAO.getCompanies();
        companies.remove("Ticket Master");
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
