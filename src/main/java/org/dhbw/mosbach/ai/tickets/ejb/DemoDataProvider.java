package org.dhbw.mosbach.ai.tickets.ejb;

import org.dhbw.mosbach.ai.tickets.database.EntryDAO;
import org.dhbw.mosbach.ai.tickets.database.TicketDAO;
import org.dhbw.mosbach.ai.tickets.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RunAs;
import javax.ejb.*;
import javax.inject.Inject;
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

	@Inject
	private TicketDAO ticketDAO;

	@Inject
	private EntryDAO entryDAO;

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
		createTicket("The Grinch hated Christmas", Ticket.Status.open,"How the Grinch Stole Christmas!",0, 0, new Date());
		createTicket("We should take Bikini Bottom and push it somewhere else", Ticket.Status.closed, "SpongeBob SquarePants",0, 0,  new Date());
		createTicket("Computer is broken", Ticket.Status.closed,"No Content",  0, 0, new Date());
		createTicket("IE11 is broken", Ticket.Status.inProcess, "What's the difference between snowmen and snowladies? Snowballs", 0, 0, new Date());
		createTicket("Windows is broken 1", Ticket.Status.inProcess, "I am a nobody, nobody is perfect, therefore I am perfect.",0, 0,  new Date());
		createTicket("Windows is broken 2", Ticket.Status.open, "I wondered why the frisbee was getting bigger, and then it hit me.",0, 0,  new Date());
		createTicket("Windows is broken 3", Ticket.Status.open, "If con is the opposite of pro, it must mean Congress is the opposite of progress?", 0, 0, new Date());
		createTicket("How do you make holy water?", Ticket.Status.open, "You boil the hell out of it.", 0, 0, new Date());
		createTicket("What do you call a fat psychic?", Ticket.Status.open, "A four chin teller.", 0, 0, new Date());
		createTicket("Light travels faster than sound", Ticket.Status.open, "This is why some people appear bright until they speak.", 0, 0, new Date());
		createTicket("I used to like my neighbors", Ticket.Status.open, "Until they put a password on their Wi-Fi.", 0, 0, new Date());
		createTicket("I once farted in an elevator", Ticket.Status.open, "I once farted in an elevator, it was wrong on so many levels.",0, 0,  new Date());
		createTicket("What do you call a bear with no teeth?", Ticket.Status.open, "A gummy bear!", 0, 0, new Date());
		createTicket("Just deleted the internet", Ticket.Status.open, "Please Help", 0, 0, new Date());
	}

	private Ticket createTicket(String subject, Ticket.Status status, String content, long editorId, long customerId, Date createDate){
		final Ticket ticket = new Ticket(subject, status, content, editorId, customerId);
		final Entry entry = new Entry(customerId, content, createDate);
		ticket.addEntry(entry);
		ticketDAO.persist(ticket);
		entryDAO.persist(entry);
		return ticket;
	}

}