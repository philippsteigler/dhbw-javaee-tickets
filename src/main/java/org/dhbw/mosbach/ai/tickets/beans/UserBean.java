package org.dhbw.mosbach.ai.tickets.beans;

import com.google.common.collect.ImmutableList;
import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.User;
import org.dhbw.mosbach.ai.tickets.view.AdminView;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Named("userBean")
@SessionScoped
public class    UserBean extends AbstractBean {
    private static final long serialVersionUID = -7105806000082771152L;

    @Inject
    private UserDAO userDAO;

    @Inject
    private SecurityBean securityBean;

    @Inject
    private AdminView adminView;

    private User currentUser;

    private List<User> searchResult;
    private String searchString = "";

    private List<Role> roles;

    private List<String> companies;

    private static final String VIEW_DETAILS = "admin-user-details";
    private static final String VIEW_USERS = "admin-all-users";

    public String newUser(String login_id, String name, String companyName, String email, String password, String role) {

        //check if login_id already exists
        if (checkIfLoginIdExist(login_id)) {

            //clear only login_id input field and print fatal error message
            adminView.setLogin_id("");
            addLocalizedFacesMessage(FacesMessage.SEVERITY_FATAL, "admin.loginId.duplicated");

            //stay on page
            return null;
        } else {

            //create new user and save new user
            final User user = new User(login_id, name, companyName, email);
            user.getRoles().add(parseRoles(role));
            userDAO.changePassword(user, password);
            saveUser(user);

            //set all input fields to default
            adminView.setName("");
            adminView.setLogin_id("");
            adminView.setEmail("");
            adminView.setRole("customer");
            adminView.setCompanyName("");
            adminView.setPassword("");

            //leave page and go back to user view
            return VIEW_USERS;
        }
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

    public String deleteUser(User user) {
        userDAO.removeDetached(user);
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.deleteSuccess");

        return VIEW_USERS;
    }

    public String deleteAccount() {
        userDAO.removeDetached(securityBean.getUser());
        addLocalizedFacesMessage(FacesMessage.SEVERITY_INFO, "user.deleteSuccess");

        return securityBean.logout();
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
            searchResult = ImmutableList.copyOf(userDAO.getUsersContainingName(searchString));
        }
    }

    public void fetchAllRoles() {
        roles = userDAO.getRoles();
    }

    public void fetchAllCompanies() {
        companies = new ArrayList<>();
        companies = userDAO.getCompanies();
        companies.remove("Ticket Master");
    }

    public List<User> getEditors() {
        return userDAO.getAll().stream().filter(user -> user.getRoles().stream().allMatch(role -> role.getName().equals("editor")
                && user.getId() != securityBean.getUser().getId())).collect(Collectors.toList());
    }

    public String getUserName(long id) {
        List<User> findUser = userDAO.getAll().stream().filter(user -> user.getId() == id).collect(Collectors.toList());
        if (!findUser.isEmpty()) {
            return findUser.get(0).getName();
        } else return "None";

    }

    public String getUserCompany(long id) {
        return userDAO.getAll().stream().filter(user -> user.getId() == id).collect(Collectors.toList()).get(0).getCompany();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }


    public List<String> getCompanies() {

        return companies;
    }

    private boolean checkIfLoginIdExist(String loginId) {

        //get all login_Ids from database and check if argument loginId already exists
        List<String> loginIds = userDAO.getLoginIds();

        return loginIds.contains(loginId);
    }

}
