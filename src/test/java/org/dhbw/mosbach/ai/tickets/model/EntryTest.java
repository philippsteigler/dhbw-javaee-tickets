package org.dhbw.mosbach.ai.tickets.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class EntryTest {

    private static final long ENTRY_CREATOR_ID = 13;
    private static final String ENTRY_CONTENT = "Test Content";


    @Test
    public void testObjectCreation()
    {
        final Entry entry = new Entry(ENTRY_CREATOR_ID, ENTRY_CONTENT, new Date());

        Assert.assertEquals(0, entry.getId());
        Assert.assertEquals(ENTRY_CREATOR_ID, entry.getCreatorID());
        Assert.assertEquals(ENTRY_CONTENT, entry.getContent());
    }
}
