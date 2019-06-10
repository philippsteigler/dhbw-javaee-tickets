package org.dhbw.mosbach.ai.tickets.view;

import org.dhbw.mosbach.ai.tickets.beans.SecurityBean;
import org.dhbw.mosbach.ai.tickets.beans.TicketEditorBean;
import org.dhbw.mosbach.ai.tickets.beans.UserBean;
import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.User;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.*;

@Named
@SessionScoped
public class GeneralView implements Serializable {

    @Inject
    private SecurityBean securityBean;

    private String name = "";
    private String userName = "";
    private String email = "";
    private String company = "";
    private Set<Role> roles = new HashSet<>();



    public void getUserToDisplay(){
        User user = securityBean.getUser();

        name = user.getName();
        userName = user.getLoginID();
        email = user.getEmail();
        company = user.getCompany();
        roles = user.getRoles();
    }


    public String formatDate(Date date){
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);

        return dateFormat.format(date);
    }

    //Getter and Setter


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
