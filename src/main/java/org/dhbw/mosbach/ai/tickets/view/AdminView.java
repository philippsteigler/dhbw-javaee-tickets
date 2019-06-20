package org.dhbw.mosbach.ai.tickets.view;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named("adminView")
@SessionScoped
public class AdminView implements Serializable {

    private String login_id;
    private String name;
    private String companyName = "";
    private String email;
    private String password;
    private String role = "customer";

    public boolean disableCompanyInput(String role) {
        if (role == null){
            return true;
        }
        else if (!role.equals("customer")) {
            setCompanyName("Ticket Master");
            return true;
        } else {

            if (companyName.equals("Ticket Master")) {
                setCompanyName("");
            }

            return false;
        }
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
