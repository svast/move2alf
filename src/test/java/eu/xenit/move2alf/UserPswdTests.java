package eu.xenit.move2alf;

import static org.junit.Assert.assertEquals;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;

import eu.xenit.move2alf.core.dto.UserPswd;

public class UserPswdTests extends DatabaseTests {
	@Test
	public void testUserPswd() {
		UserPswd user = new UserPswd();
		user.setUserName("testUser");
		user.setPassword("password");
		session.save(user);

		UserPswd testUser = (UserPswd) session.createQuery(
				"from UserPswd as u where u.userName=:userName").setString(
				"userName", "testUser").uniqueResult();
		assertEquals("password", testUser.getPassword());
	}

	@Test(expected = ConstraintViolationException.class)
	public void testUserPswdUsernameShouldBeUnique() {
		UserPswd user1 = new UserPswd();
		user1.setUserName("testUser");
		user1.setPassword("password");
		session.save(user1);
		UserPswd user2 = new UserPswd();
		user2.setUserName("testUser");
		user2.setPassword("password");
		session.save(user2);
	}
}
