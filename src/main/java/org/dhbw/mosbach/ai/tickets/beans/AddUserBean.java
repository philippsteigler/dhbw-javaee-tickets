package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.ejb.MailService;
import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.User;
import org.dhbw.mosbach.ai.tickets.security.CDIRoleCheck;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse zum anlegen neuer Benutzer durch einen Administrator.
 */
@Named
@SessionScoped
@CDIRoleCheck
@RolesAllowed(value = { Roles.ADMIN })
public class AddUserBean extends AbstractBean {

    // Notwendige Beans werden an dieser Stelle eingebunden.
    @Inject
    private UserDAO userDAO;

    @EJB
    private MailService mailService;

    private String login_id;
    private String name;
    private String companyName = "";
    private String email;
    private String password;
    private String role = "customer";

    private List<Role> roles;
    private List<String> companies;

    private static final String VIEW_USERS = "admin-all-users";

    // Getter and Setter
    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getCompanies() {
        return companies;
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

    // Methode zum Anlegen neuer Benutzer von der Webseite aus.
    //
    // Alle Eingaben der Web-Maske werden hier übernommen und verarbeitet.
    // Nur Admins dürfen neue Benutzer anlegen!
    @RolesAllowed(value = { Roles.ADMIN })
    public String newUser(String login_id, String name, String companyName, String email, String password, String role) {

        // überprüft ob die login_id bereits existiert (Validierung ist notwendig, da die login_id einzigartig ist)
        if (checkIfLoginIdExist(login_id)) {

            // leert das Input Feld und gibt Fehlernachricht aus
            this.login_id = "";
            addLocalizedFacesMessage(FacesMessage.SEVERITY_FATAL, "admin.loginId.duplicated");

            // Redirect auf die gleiche Seite.
            return null;
        } else {

            // erstellt neuen Benutzer und speichert ihn
            final User user = new User(login_id, name, companyName, email);
            user.getRoles().add(parseRoles(role));
            userDAO.changePassword(user, password);

            // Anschließend den neuen User in die Datenbank speichern.
            userDAO.persistOrMerge(user);
            addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.saveSuccess");

            // Bestätigungsmail versenden
            mailService.sendConfirmationMail(email, login_id, password);

            // Setze alle Eingabefelder der Web-Maske zurück.
            this.name = "";
            this.login_id = "";
            this.email = "";
            this.role = "customer";
            this.companyName = "";
            this.password = "";

            // Seite verlassen und auf die Benutzerübersicht zurückkehren
            return VIEW_USERS;
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
}
