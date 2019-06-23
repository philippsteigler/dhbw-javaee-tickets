package org.dhbw.mosbach.ai.tickets.model;

import org.junit.Assert;
import org.junit.Test;

public class UserTest {

	private static final String USER_LOGIN_ID = "TestLoginId";
	private static final String USER_NAME = "TestName";
	private static final String USER_COMPANY = "TestCompany";
	private static final String USER_EMAIL = "TestEmail";


	@Test
	public void testObjectCreation()
	{
		final User user = new User(USER_LOGIN_ID, USER_NAME, USER_COMPANY, USER_EMAIL);

		Assert.assertEquals(0, user.getId());
		Assert.assertEquals(USER_LOGIN_ID, user.getLoginID());
		Assert.assertEquals(USER_NAME, user.getName());
		Assert.assertEquals(USER_COMPANY, user.getCompany());
		Assert.assertEquals(USER_EMAIL, user.getEmail());
	}
}
