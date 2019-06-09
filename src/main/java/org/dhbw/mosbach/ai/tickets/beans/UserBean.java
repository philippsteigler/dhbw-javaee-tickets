package org.dhbw.mosbach.ai.tickets.beans;

import com.google.common.collect.ImmutableList;
import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.model.Entry;
import org.dhbw.mosbach.ai.tickets.model.Ticket;
import org.dhbw.mosbach.ai.tickets.model.User;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named
@SessionScoped
public class UserBean extends AbstractBean {
    private static final long serialVersionUID = -7105806000082771152L;

    @Inject
    private UserDAO userDAO;

    @Inject
    private SecurityBean securityBean;

    // TODO: REMOVE AFTER SQL
    private List<User> users;

    private User currentUser;

    private List<User> searchResult;
    private String searchString = "";

    private static final String VIEW_DETAILS = "admin-user-details";

    // TODO: REMOVE AFTER SQL
    @PostConstruct
    public void init() {
        this.users = userDAO.getAll();
    }

    private void suveUser(User user) {
        userDAO.persistOrMerge(user);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.saveSuccess");
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public List<User> getSearchResult() {
        return searchResult;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String viewUserDetails(User user) {
        this.currentUser = user;

        return VIEW_DETAILS;
    }

    public void fetchAllUsers() {
        if (searchString.isEmpty()) {
            searchResult = ImmutableList.copyOf(userDAO.getAll());
        } else {
            searchResult = ImmutableList.copyOf(userDAO.getUsersContainingSubject(searchString));
        }
    }

    // TODO: SQL fetchAllEditors
    public List<User> getEditors() {
        return users.stream().filter(user -> user.getRoles().stream().allMatch(role -> role.getName().equals("editor") && user.getId() != securityBean.getUser().getId())).collect(Collectors.toList());
    }

    public String getUserName(long id) {
        if (id == 0) { return "None";}
        else return users.stream().filter(user -> user.getId() == id).collect(Collectors.toList()).get(0).getName();
    }

    public String getUserCompany(long id) {
        return users.stream().filter(user -> user.getId() == id).collect(Collectors.toList()).get(0).getCompany();
    }
}
