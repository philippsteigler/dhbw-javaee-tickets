package org.dhbw.mosbach.ai.tickets.ejb;

import org.dhbw.mosbach.ai.tickets.model.*;
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
import java.util.Date;

@Startup
@Singleton
@RunAs(Roles.ADMIN)
public class DemoDataProvider {
	private static final Logger logger = LoggerFactory.getLogger(DemoDataProvider.class);

	@PersistenceContext
	private EntityManager em;

	@Resource
	private EJBContext context;

	@Resource
	private TimerService timerService;

	@EJB
	private UserDAOProxy userDAOProxy;

	@EJB
	private TicketDAOProxy ticketDAOProxy;

	@EJB
	private EntryDAOProxy entryDAOProxy;

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
		final Long ticketCount = (Long) em.createQuery("SELECT COUNT(t) FROM Ticket t").getSingleResult();

		if (userCount == 0)
			createUsers();

		if (ticketCount == 0)
			createTickets();
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

	private void createTickets() {
		createTicket("computer is broken", Ticket.Status.open, 0, "otto", "no content", new Date());
		createTicket("ie11 shortcuts", Ticket.Status.open, 0, "wulf", "good content", new Date());
	}

	private Ticket createTicket(String subject, Ticket.Status status, long editorId, String creator, String content, Date createDate){
		final Ticket ticket = new Ticket(subject, status, editorId);
		final Entry entry = new Entry(creator, content, createDate);
		ticket.addEntry(entry);
		ticketDAOProxy.persist(ticket);
		entryDAOProxy.persist(entry);
		return ticket;
	}

}
