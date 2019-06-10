package org.dhbw.mosbach.ai.tickets.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.collect.Sets;

@Entity
public class User {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, length = 32, unique = true)
    private String login_id;

    @Column(length = 256)
    private String password;

    @Column(length = 64)
    private String name;

    @Column(length = 64)
    private String company;

    @Column(length = 64)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = Sets.newHashSet();

    public User() {
        super();
    }

    public User(String login_id, String name, String company, String email) {
        super();
        this.login_id = login_id;
        this.name = name;
        this.company = company;
        this.email = email;
    }

    @XmlTransient
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @XmlAttribute(required = true)
    public String getLoginID() {
        return login_id;
    }

    public void setLoginID(String login_id) {
        this.login_id = login_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
