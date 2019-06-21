package org.dhbw.mosbach.ai.tickets.ejb;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.model.User;

import java.util.List;

/**
 * Proxy-Klasse zur initialen Integration von Testdaten.
 *
 * Diese Klasse spezialisiert Methoden des UserDAO, da über sie beim Starten der Anwendung bei Bedarf Testdaten mittels
 * Enterprise Java Beans erzeugt werden.
 */
@Stateless(name = "userDAOProxy")
@SpecialUserDAO
@PermitAll
public class UserDAOProxy extends UserDAO {
    private static final long serialVersionUID = 3572429638173844556L;

    // Weist einem Test-User ein Passwort zu.
    @PermitAll
    @Override
    public void changePassword(User user, String password) {
        super.changePassword(user, password);
    }

    // Speicheern eines einzelnen Test-Users in die Datenbank.
    @PermitAll
    @Override
    public void persist(User entity) {
        super.persist(entity);
    }

    // Speicheern mehrerer Test-User in die Datenbank.
    @PermitAll
    @Override
    public void persist(User... entities) {
        super.persist(entities);
    }

    // Abrufen aller User aus der Datenbank.
    @PermitAll
    @Override
    public List<User> getAll() {
        return super.getAll();
    }

    // Suche nach einem bestimmten Test-User in der Datenbank anhand eines beliebigen Attributs.
    @PermitAll
    @Override
    public User findByUnique(String fieldName, Object key) {
        return super.findByUnique(fieldName, key);
    }
}
