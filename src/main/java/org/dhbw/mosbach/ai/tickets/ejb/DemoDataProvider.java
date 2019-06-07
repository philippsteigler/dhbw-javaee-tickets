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
		final Role customerRole = new Role(Roles.EDITOR, "Kunde");
		em.persist(adminRole);
		em.persist(editorRole);
		em.persist(customerRole);

		createUser("root", "Root", "Ticket Master", "toor", adminRole, editorRole);
		createUser("admin", "The Admin", "Ticket Master","admin", adminRole);
		createUser("editor1", "Wiz Khalifa", "Ticket Master","mosbach", editorRole);
		createUser("editor2", "Dirk Saller", "Ticket Master","mosbach", editorRole);
		createUser("customer1", "Edwin Kopf", "IBM","mosbach", customerRole);
		createUser("customer2", "Jend Weidmann", "Deutsche Bundesbank","mosbach", customerRole);
	}

	private User createUser(String login, String userName, String companyName, String password, Role... userRoles) {
		final User user = new User(login, userName, companyName);
		user.getRoles().addAll(Arrays.asList(userRoles));
		userDAOProxy.changePassword(user, password);
		userDAOProxy.persist(user);

		return user;
	}

	private void createTickets() {
		createTicket("The Grinch hated Christmas", Ticket.Status.open, 0, "Dr. Seuss", "How the Grinch Stole Christmas!", new Date());
		createTicket("We should take Bikini Bottom and push it somewhere else", Ticket.Status.closed, 0, "Patrick", "SpongeBob SquarePants", new Date());
		createTicket("Computer is broken", Ticket.Status.closed, 0, "Maria", "No Content", new Date());
		createTicket("IE11 is broken", Ticket.Status.inProcess, 0, "Hank", "What's the difference between snowmen and snowladies? Snowballs", new Date());
		createTicket("Windows is broken 1", Ticket.Status.inProcess, 0, "Robert", "I am a nobody, nobody is perfect, therefore I am perfect.", new Date());
		createTicket("Windows is broken 2", Ticket.Status.open, 0, "John", "I wondered why the frisbee was getting bigger, and then it hit me.", new Date());
		createTicket("Windows is broken 3", Ticket.Status.open, 0, "Maya", "If con is the opposite of pro, it must mean Congress is the opposite of progress?", new Date());
		createTicket("How do you make holy water?", Ticket.Status.open, 0, "Frank", "You boil the hell out of it.", new Date());
		createTicket("What do you call a fat psychic?", Ticket.Status.open, 0, "Wulf", "A four chin teller.", new Date());
		createTicket("Light travels faster than sound", Ticket.Status.open, 0, "Tiger", "This is why some people appear bright until they speak.", new Date());
		createTicket("I used to like my neighbors", Ticket.Status.open, 0, "Fish", "Until they put a password on their Wi-Fi.", new Date());
		createTicket("I once farted in an elevator", Ticket.Status.open, 0, "Damian", "I once farted in an elevator, it was wrong on so many levels.", new Date());
		createTicket("What do you call a bear with no teeth?", Ticket.Status.open, 0, "Mitch", "A gummy bear!", new Date());
		createTicket("Just deleted the internet", Ticket.Status.open, 0, "Lily", "Please Help", new Date());
	}

	private Ticket createTicket(String subject, Ticket.Status status, long editorId, String creator, String content, Date createDate){
		final Ticket ticket = new Ticket(subject, status, editorId);
		final Entry entry = new Entry(creator, content, createDate);
		ticket.addEntry(entry);
		ticketDAO.persist(ticket);
		entryDAO.persist(entry);
		return ticket;
	}

}