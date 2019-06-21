package org.dhbw.mosbach.ai.tickets.database;

import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.User;
import org.dhbw.mosbach.ai.tickets.security.CDIRoleCheck;
import org.jboss.security.Base64Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

@Named("userDAO")
@Dependent
@CDIRoleCheck
@RolesAllowed(value = { Roles.ADMIN })
public class UserDAO extends BaseDAO<User, Long> {
    private static final long serialVersionUID = -6308185751264138344L;
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public UserDAO() {
        super();
    }

    private MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e) {
            logger.error("Could not find Message digest!", e);
            throw new RuntimeException(e);
        }
    }

    public void changePassword(User user, String password) {
        try {
            user.setPassword(Base64Encoder.encode(getMessageDigest().digest(password.getBytes())));
        } catch (final IOException e) {
            logger.error("Exception in base64encoder.encode", e);
        }
    }

    @Override
    @PermitAll
    public User findByUnique(String fieldName, Object key) {
        return super.findByUnique(fieldName, key);
    }

    public List<User> getUsersContainingName(String substring) {
        if ((substring != null) && (substring.length() >= 2)) {
            final String query = String.format("FROM %s u WHERE UPPER(u.name) LIKE :name", User.class.getName());

            return em.createQuery(query, User.class)
                    .setParameter("name", "%" + substring.toUpperCase() + "%")
                    .getResultList();
        }

        return Collections.emptyList();
    }

    public List<Role> getRoles() {

        final String query = String.format("FROM %s", Role.class.getName());

        return em.createQuery(query, Role.class).getResultList();
    }

    public List<String> getCompanies() {

        final String query = String.format("SELECT DISTINCT u.company FROM %s u", User.class.getName());

        return em.createQuery(query, String.class).getResultList();
    }

    public List<String> getLoginIds() {

        final String query = String.format("SELECT u.login_id FROM %s u", User.class.getName());

        return em.createQuery(query, String.class).getResultList();
    }
}
