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
    private String loginID;

    @Column(length = 256)
    private String password;

    @Column(length = 64)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = Sets.newHashSet();

    @OneToOne(optional = false, fetch = FetchType.EAGER, mappedBy = "user")
    private UserData userData;

    public User() {
        super();
    }

    public User(String loginID, String name) {
        super();
        this.loginID = loginID;
        this.name = name;
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
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
