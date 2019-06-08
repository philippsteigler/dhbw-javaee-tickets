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

@Named
@RequestScoped
public class SecurityBean {
    private static final Logger logger = LoggerFactory.getLogger(SecurityBean.class);

    @Inject
    private UserDAO userDAO;

    @Inject
    private HttpServletRequest request;
    private User loggedInUser;

    private static final String HOME = "home";

    private HttpServletRequest getRequest() {
        if (request != null) {
            return request;
        }

        final FacesContext facesContext = FacesContext.getCurrentInstance();

        return (HttpServletRequest) ((facesContext != null) ? FacesContext.getCurrentInstance().getExternalContext().getRequest() : null);
    }

    private Optional<Principal> getPrincipal() {
        return Optional.ofNullable(Objects.requireNonNull(getRequest()).getUserPrincipal());
    }

    public String getLoginName() {
        return getPrincipal().map(Principal::getName).orElse("");
    }

    public boolean isAuthenticated() {
        return getPrincipal().isPresent();
    }

    public boolean isUserInRole(String role) {
        return Objects.requireNonNull(getRequest()).isUserInRole(role);
    }

    public User getUser() {
        final Optional<Principal> principal = getPrincipal();

        if (principal.isPresent()) {
            if ((loggedInUser == null) || !principal.get().getName().equals(loggedInUser.getLoginID())) {
                loggedInUser = userDAO.findByUnique("login_id", principal.get().getName());
            }

            return loggedInUser;
        }

        return null;
    }

    public String login() {
        loggedInUser = null;
        return "/loginsuccess.xhtml?faces-redirect=true";
    }

    public String logout() {
        final HttpServletRequest request = getRequest();
        loggedInUser = null;

        try {
            request.logout();
            request.getSession().invalidate();
        } catch (final ServletException e) {
            logger.warn("Exception during logout", e);
        }

        return HOME;
    }
}
