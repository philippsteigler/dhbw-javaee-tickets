package org.dhbw.mosbach.ai.tickets.beans;

import com.google.common.collect.ImmutableList;
import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.User;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Named
@SessionScoped
public class UserBean extends AbstractBean {
    private static final long serialVersionUID = -7105806000082771152L;

    @Inject
    private UserDAO userDAO;

    @Inject
    private SecurityBean securityBean;

    private User currentUser;

    private List<User> searchResult;
    private String searchString = "";

    private String login_id;
    private String name;
    private String companyName;
    private String email;
    private String password;
    private String role;

    private List<Role> roles;

    private static final String VIEW_DETAILS = "admin-user-details";
    private static final String VIEW_USERS = "admin-all-users";

    public String newUser(String login_id, String name, String companyName, String email, String password, String role) {
        final User user = new User(login_id, name, companyName, email);
        user.getRoles().add(parseRoles(role));
        userDAO.changePassword(user, password);
        saveUser(user);

        return VIEW_USERS;
    }

    private Role parseRoles(String role){

        for (Role roleFromDatabase : roles) {
            if (role.equals(roleFromDatabase.getName())) {
                return roleFromDatabase;
            }
        }

        return null;
    }

    private void saveUser(User user) {
        userDAO.persistOrMerge(user);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.saveSuccess");
    }

    public void deleteUser(User user) {
        userDAO.removeDetached(user);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.deleteSuccess");
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

    public void fetchAllRoles() {
        roles = userDAO.getRoles();
    }

    public List<User> getEditors() {
        return userDAO.getAll().stream().filter(user -> user.getRoles().stream().allMatch(role -> role.getName().equals("editor")
                && user.getId() != securityBean.getUser().getId())).collect(Collectors.toList());
    }

    public String getUserName(long id) {
        if (id == 0) {
            return "None";
        } else {
            return userDAO.getAll().stream().filter(user -> user.getId() == id).collect(Collectors.toList()).get(0).getName();
        }
    }

    public String getUserCompany(long id) {
        return userDAO.getAll().stream().filter(user -> user.getId() == id).collect(Collectors.toList()).get(0).getCompany();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

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
}
