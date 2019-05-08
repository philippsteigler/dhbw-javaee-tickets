package org.dhbw.mosbach.ai.tickets.ejb.demo;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

import org.dhbw.mosbach.ai.tickets.database.UserDAO;
import org.dhbw.mosbach.ai.tickets.model.User;

@Stateless(name = "UserDAOProxy")
@SpecialUserDAO
@PermitAll
public class UserDAOProxy extends UserDAO {
    private static final long serialVersionUID = 1L;

    @PermitAll
    @Override
    public void changePassword(User user, String password) {
        super.changePassword(user, password);
    }

    @PermitAll
    @Override
    public void persist(User entity) {
        super.persist(entity);
    }

    @PermitAll
    @Override
    public void persist(User... entities) {
        super.persist(entities);
    }

    @PermitAll
    @Override
    public User findByUnique(String fieldName, Object key) {
        return super.findByUnique(fieldName, key);
    }
}
