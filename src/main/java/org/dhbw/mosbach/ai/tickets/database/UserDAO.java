package org.dhbw.mosbach.ai.tickets.database;

import org.dhbw.mosbach.ai.tickets.security.CDIRoleCheck;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.User;
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

@Named("userDAO")
@Dependent
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

    @RolesAllowed(value = { Roles.ADMIN })
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
}
