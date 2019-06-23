package org.dhbw.mosbach.ai.tickets.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class TicketTest {

    private static final String TICKET_SUBJECT = "Test Subject";
    private static final Ticket.Status TICKET_STATUS = Ticket.Status.open;
    private static final long TICKET_EDITOR_ID = 0;
    private static final long TICKET_CUSTOMER_ID = 42;
    private static final String ENTRY_CONTENT = "Test Content";


    @Test
    public void testObjectCreation()
    {
        final Ticket ticket = new Ticket(TICKET_SUBJECT, TICKET_STATUS, TICKET_EDITOR_ID, TICKET_CUSTOMER_ID);
        final Entry entry = new Entry(TICKET_CUSTOMER_ID, ENTRY_CONTENT, new Date());
        ticket.addEntry(entry);

        Assert.assertEquals(0, ticket.getId());
        Assert.assertEquals(TICKET_SUBJECT, ticket.getSubject());
        Assert.assertEquals(TICKET_STATUS, ticket.getStatus());
        Assert.assertEquals(TICKET_EDITOR_ID, ticket.getEditorId());
        Assert.assertEquals(TICKET_CUSTOMER_ID, ticket.getCustomerId());

        Assert.assertSame(entry, ticket.getEntries().get(0));
    }
}
