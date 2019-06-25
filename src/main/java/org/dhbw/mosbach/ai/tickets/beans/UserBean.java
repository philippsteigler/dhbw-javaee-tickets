package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hilfsklasse zur nutzerfreundlichen Anzeige von Inhalten in der Darstellungsschicht.
 */
@Named
@ViewScoped
public class UserBean extends AbstractBean {

    // Notwendige CDI-Beans werden an dieser Stelle eingebunden.
    @Inject
    private UserDAO userDAO;

    @Inject
    private SecurityBean securityBean;

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);


    // Gib alle möglichen Editoren zurück. Wird im Frontend zum Delegieren von Tickets benötigt.
    // Der eigene User wird nicht zum Delegieren aufgelistet.
    public List<User> getEditors() {
        return userDAO.getAll().stream().filter(user -> user.getRoles().stream().allMatch(role -> role.getName().equals("editor")
                && user.getId() != securityBean.getUser().getId())).collect(Collectors.toList());
    }

    // Methode zur Übersetzung von Benutzer-IDs in Benutzernamen.
    // Wird zur schöneren Anzeige im Frontend benötigt, da für Tickets nur die IDs der User hinterlegt sind.
    public String getUserName(long id) {
        List<User> findUser = userDAO.getAll().stream().filter(user -> user.getId() == id).collect(Collectors.toList());

        if (!findUser.isEmpty()) {
            return findUser.get(0).getName();
        } else return "None";
    }

    // Methode zur Übersetzung von Benutzer-IDs in Unternehmen.
    // Wird zur schöneren Anzeige in den Ticket-Details benötigt, um die Firma eines Users anzuzeigen,
    // da nur dessen User-ID aus einem Ticket bekannt ist.
    public String getUserCompany(long id) {

        // Falls der Kunde nicht mehr exisitert wird ein IndexOutOfBounds Fehler abgefangen
        try {
            User findUser = userDAO.getAll().stream().filter(user -> user.getId() == id).collect(Collectors.toList()).get(0);

            if (findUser != null) {
                return findUser.getCompany();
            } else return "None";
        } catch (final IndexOutOfBoundsException e){
            logger.warn("Searched Company for deleted Customer: ", e);
        }

        return "None";
    }
}
