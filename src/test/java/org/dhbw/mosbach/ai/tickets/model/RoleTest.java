package org.dhbw.mosbach.ai.tickets.model;

import org.junit.Assert;
import org.junit.Test;

public class RoleTest {

    private static final String ROLE_NAME = "TestName";
    private static final String ROLE_DESCRIPTION = "TestDescription";


    @Test
    public void testObjectCreation()
    {
        final Role role = new Role(ROLE_NAME, ROLE_DESCRIPTION);

        Assert.assertEquals(0, role.getId());
        Assert.assertEquals(ROLE_NAME, role.getName());
        Assert.assertEquals(ROLE_DESCRIPTION, role.getDescription());
    }
}
