package org.dhbw.mosbach.ai.tickets.beans;

import java.security.Principal;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Klasse zur Verwaltung einer aktiven Browser-Sitzung.
 *
 * Wird fürs Login/Logout und zur Verwaltung des eingeloggten Benutzers benötigt.
 */
@Named
@RequestScoped
public class SecurityBean {
    private static final Logger logger = LoggerFactory.getLogger(SecurityBean.class);

    // Notwendige CDI-Beans werden an dieser Stelle eingebunden.
    @Inject
    private UserDAO userDAO;

    @Inject
    private HttpServletRequest request;
    private User loggedInUser;

    // Kürzel für Redirects im Frontend.
    private final static String VIEW_LOGIN_SUCCESS = "/pages/loginsuccess.xhtml?faces-redirect=true";

    // Methode zur Verarbeitung der aktuellen Anfrage aus dem Frontend.
    private HttpServletRequest getRequest() {
        if (request != null) {
            return request;
        }

        final FacesContext facesContext = FacesContext.getCurrentInstance();

        return (HttpServletRequest) ((facesContext != null) ? FacesContext.getCurrentInstance().getExternalContext().getRequest() : null);
    }

    // Gib den Benutzer der aktuellen Browser-Sitzung zurück.
    private Optional<Principal> getPrincipal() {
        return Optional.ofNullable(Objects.requireNonNull(getRequest()).getUserPrincipal());
    }

    // Gib den Namen des derzeitigen Benutzers zurück.
    public String getLoginName() {
        return getPrincipal().map(Principal::getName).orElse("");
    }

    // Methode zur Überprüfung, ob derzeit ein Benutzer angemeldet ist.
    public boolean isAuthenticated() {
        return getPrincipal().isPresent();
    }

    // Methode zur Überprüfung der Rolle des aktuellen Benutzers.
    public boolean isUserInRole(String role) {
        return Objects.requireNonNull(getRequest()).isUserInRole(role);
    }

    // Methode zur Rückgabe des aktuell eingeloggten Benutzers.
    public User getUser() {
        final Optional<Principal> principal = getPrincipal();

        // Sofern derzeit ein User angemeldet ist, gib diesen zurück.
        if (principal.isPresent()) {

            // Der vollständige Benutzer wird anhand seiner eindeutigen Login-ID aus der Datenbank geladen.
            if ((loggedInUser == null) || !principal.get().getName().equals(loggedInUser.getLoginID())) {
                loggedInUser = userDAO.findByUnique("login_id", principal.get().getName());
            }

            return loggedInUser;
        }

        return null;
    }

    // Methode zur Validierung der eingegebenen User-Credentials. Es wird eine neue Sitzung erzeugt.
    // Die Validierung findet über den Wildfly-Application-Server statt!
    public String login() {
        loggedInUser = null;
        return VIEW_LOGIN_SUCCESS;
    }

    // Methode zum Beenden einer aktiven Sitzung.
    // Der aktuelle User wird zurückgesetzt, anschließend wird auf die Logout-Seite weitergeleitet.
    public String logout() {
        final HttpServletRequest request = getRequest();
        loggedInUser = null;

        try {
            request.logout();
            request.getSession().invalidate();
        } catch (final ServletException e) {
            logger.warn("Exception during logout", e);
        }

        return VIEW_LOGIN_SUCCESS;
    }
}
