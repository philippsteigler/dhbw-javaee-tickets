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

/**
 * Klasse zur Modellierung von Benutzern, die sich im Ticket-System einloggen können.
 * Ein Benutzer besitzt folgende Attribute:
 *
 * - id: eindeutiger Primärschlüssel
 * - login_id: eindeutiger Benutzername für Login
 * - password: Passwort zum Schutz des Kontos
 * - name: vollständiger Name des Benutzers
 * - company: Bezeichnung für das Unternehmen eines Benutzers ("Ticket Master" bei Mitarbeitern)
 * - email: E-Mail-Adresse des Benutzers für Benachrichtigungen
 * - roles: Role des Benutzers, entweder Admin, Editor oder Customer (1 und 2 für Mitarbeiter, 3 für externen Kunden)
 */
@Entity
public class User {

    // Attribute analog zu Spalten ein der User-Tabelle.
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

    // Für neue Benutzer werden anfangs Login-ID, Name, Unternehmen und E-Mail-Adresse vergeben.
    // Der Primärschlüssel "ID" wird automatisch erzeugt.
    //
    // Passwort und Rolle werden anderweitig über den UserDAO gesetzt.
    // (Manuell bei Demo-Daten, über eine Form beim Anlegen durch einen Administrator)
    public User(String login_id, String name, String company, String email) {
        super();
        this.login_id = login_id;
        this.name = name;
        this.company = company;
        this.email = email;
    }

    // Getter und Setter
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
