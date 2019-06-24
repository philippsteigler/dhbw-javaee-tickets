package org.dhbw.mosbach.ai.tickets.database;

import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.User;
import org.jboss.security.Base64Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

/**
 * Data-Access-Object (DAO) für die Verwaltung von User-Objekten in der Datenbank.
 * Über diese Klasse werden User in die Datenbank gespeichert und aus der Datenbank gelesen.
 */
@Named("userDAO")
@Dependent
public class UserDAO extends BaseDAO<User, Long> {
    private static final long serialVersionUID = -6308185751264138344L;
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public UserDAO() {
        super();
    }

    // Methode zur Berechnung eines Passwort-Hashes, da in der Datenbank keine Passwörter im Klartext gespeichert werden.
    @RolesAllowed(value = { Roles.ADMIN, Roles.EDITOR, Roles.CUSTOMER })
    private MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e) {
            logger.error("Could not find Message digest!", e);
            throw new RuntimeException(e);
        }
    }

    // Methode zum Setzen/Ändern des Passworts für einen Benutzer.
    @RolesAllowed(value = { Roles.ADMIN, Roles.EDITOR, Roles.CUSTOMER })
    public void changePassword(User user, String password) {
        try {
            user.setPassword(Base64Encoder.encode(getMessageDigest().digest(password.getBytes())));
        } catch (final IOException e) {
            logger.error("Exception in base64encoder.encode", e);
        }
    }

    // Suche nach einem bestimmten Test-User in der Datenbank anhand eines beliebigen Attributs.
    @Override
    public User findByUnique(String fieldName, Object key) {
        return super.findByUnique(fieldName, key);
    }

    // Methode zum Suchen von Benutzern anhand ihres Names.
    //
    // Wird nur vom Admin bei der Suche in der Benutzer-Liste verwendet.
    @RolesAllowed(value = { Roles.ADMIN })
    public List<User> getUsersContainingName(String substring) {
        // Sofern der Übergabewert Zeichen enthält...
        if ((substring != null) && (substring.length() >= 1)) {
            // ...suche nach allen Benutzern in der Datenbank, die diese Zeichenkette enthalten.
            final String query = String.format("FROM %s u WHERE UPPER(u.name) LIKE :name", User.class.getName());

            // Gib eine Liste von passenden Benutzern zurück.
            return em.createQuery(query, User.class)
                    .setParameter("name", "%" + substring.toUpperCase() + "%")
                    .getResultList();
        }

        // Falls kein Benutzer gefunden wurde, gib eine Leere Liste zurück.
        return Collections.emptyList();
    }

    // Methode zum Auslesen aller existieren Rollen.
    //
    // Nur der Administrator darf auf diese kritischen Daten zugreifen!
    @RolesAllowed(value = { Roles.ADMIN })
    public List<Role> getRoles() {
        // Suche nach allen Rollen in der Datenbank...
        final String query = String.format("FROM %s", Role.class.getName());

        // ...und gib diese zurück.
        return em.createQuery(query, Role.class).getResultList();
    }

    // Methode zum Auslesen aller existieren Unternehmen.
    // Wird beim anlegen von neuen Benutzern verwendet, weil da bereits existierende Unternehmen vorgeschlagen werden.
    //
    // Nur der Administrator darf auf diese kritischen Daten zugreifen!
    @RolesAllowed(value = { Roles.ADMIN })
    public List<String> getCompanies() {
        // Suche in der Liste von Benutzern nach Unternehmen und filtere einzigartige Ergebnisse...
        final String query = String.format("SELECT DISTINCT u.company FROM %s u", User.class.getName());

        // ...und gib diese zurück.
        return em.createQuery(query, String.class).getResultList();
    }

    // Methode zum Auslesen aller existieren Login-IDs.
    // Wird beim Login verwendet um zu sehen, ob die behauptete Login-ID in der Datenbank existiert.
    //
    // Nur der Administrator darf auf diese kritischen Daten zugreifen!
    @RolesAllowed(value = { Roles.ADMIN })
    public List<String> getLoginIds() {
        // Suche nach allen Login-IDs in der Datenbank...
        final String query = String.format("SELECT u.login_id FROM %s u", User.class.getName());

        // ...und gib diese zurück.
        return em.createQuery(query, String.class).getResultList();
    }
}
