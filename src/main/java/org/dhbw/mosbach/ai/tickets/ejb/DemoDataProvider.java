package org.dhbw.mosbach.ai.tickets.ejb;

import org.dhbw.mosbach.ai.tickets.model.Role;
import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.dhbw.mosbach.ai.tickets.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;
import java.util.Arrays;

@Startup
@Singleton
@RunAs(Roles.ADMIN)
class DemoDataProvider {
	private static final Logger logger = LoggerFactory.getLogger(DemoDataProvider.class);

	@PersistenceContext
	private EntityManager em;

	@Resource
	private EJBContext context;

	@Resource
	private TimerService timerService;

	@EJB
	private UserDAOProxy userDAOProxy;

	@PostConstruct
	private void init() {
		timerService.createSingleActionTimer(1, new TimerConfig("ddp", false));
	}

	@Timeout
	private void timer() {
		logger.info("In DemoDataProvider.init");

		final Principal callerPrincipal = context.getCallerPrincipal();
		final boolean isAdmin = context.isCallerInRole(Roles.ADMIN);
		logger.info("Principal: {}, admin: {}", callerPrincipal, isAdmin);

		// Check whether any data exists
		final Long userCount = (Long) em.createQuery("SELECT COUNT(u) FROM User u").getSingleResult();

		if (userCount == 0)
			createUsers();
	}

	private void createUsers() {
		final Role adminRole = new Role(Roles.ADMIN, "Administrator");
		final Role editorRole = new Role(Roles.EDITOR, "Editor");
		em.persist(adminRole);
		em.persist(editorRole);

		createUser("root", "Root", "toor", adminRole, editorRole);
		createUser("admin", "The Admin", "admin", adminRole);
		createUser("editor", "Future Man", "tiger", editorRole);
	}

	private User createUser(String login, String userName, String password, Role... userRoles) {
		final User user = new User(login, userName);
		user.getRoles().addAll(Arrays.asList(userRoles));
		userDAOProxy.changePassword(user, password);
		userDAOProxy.persist(user);

		return user;
	}
}
