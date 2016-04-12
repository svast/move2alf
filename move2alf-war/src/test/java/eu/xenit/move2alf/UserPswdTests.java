package eu.xenit.move2alf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.DuplicateUserException;
import eu.xenit.move2alf.common.exceptions.NonexistentUserException;
import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.dto.UserRole;
import eu.xenit.move2alf.core.enums.ERole;
import eu.xenit.move2alf.logic.UserService;
import eu.xenit.move2alf.logic.UserServiceImpl;

public class UserPswdTests extends IntegrationTests {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

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

	@Test
	public void testLogin() {
		loginAsAdmin();
		assertEquals("admin", SecurityContextHolder.getContext()
				.getAuthentication().getName());
	}

	@Test
	public void testCreateRoleSetConsumer() {
		UserServiceImpl userService = new UserServiceImpl();
		Set<UserRole> roles = userService.createRoleSet("test", ERole.ROLE_CONSUMER);
		assertEquals(roles.size(), 1);
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_CONSUMER)));
	}

	@Test
	public void testCreateRoleSetScheduleAdmin() {
		UserServiceImpl userService = new UserServiceImpl();
		Set<UserRole> roles = userService.createRoleSet("test",
				ERole.ROLE_SCHEDULE_ADMIN);
		assertEquals(roles.size(), 2);
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_CONSUMER)));
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_SCHEDULE_ADMIN)));
	}

	@Test
	public void testCreateRoleSetJobAdmin() {
		UserServiceImpl userService = new UserServiceImpl();
		Set<UserRole> roles = userService
				.createRoleSet("test", ERole.ROLE_JOB_ADMIN);
		assertEquals(roles.size(), 3);
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_CONSUMER)));
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_SCHEDULE_ADMIN)));
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_JOB_ADMIN)));
	}

	@Test
	public void testCreateRoleSetSystemAdmin() {
		UserServiceImpl userService = new UserServiceImpl();
		Set<UserRole> roles = userService.createRoleSet("test",
				ERole.ROLE_SYSTEM_ADMIN);
		assertEquals(roles.size(), 4);
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_CONSUMER)));
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_SCHEDULE_ADMIN)));
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_JOB_ADMIN)));
		assertTrue(roles.contains(new UserRole("test", ERole.ROLE_SYSTEM_ADMIN)));
	}

	@Test
	public void testUserService() {
		assertNotNull(getUserService());
	}

	@Test
	public void testCreateUser() {
		loginAsAdmin();
		UserPswd user = getUserService().createUser("test", "test",
				ERole.ROLE_CONSUMER);
		assertEquals("test", user.getUserName());
		assertTrue(user.isUserInRole(ERole.ROLE_CONSUMER));
	}

	@Test(expected = DuplicateUserException.class)
	public void testCreateDuplicateUser() {
		loginAsAdmin();
		getUserService().createUser("test", "test1", ERole.ROLE_CONSUMER);
		getUserService().createUser("test", "test2", ERole.ROLE_JOB_ADMIN);
	}

	@Test
	public void testDeleteExistingUser() {
		loginAsAdmin();
		getUserService().createUser("test", "test", ERole.ROLE_CONSUMER);
		getUserService().deleteUser("test");
	}

	@Test(expected = NonexistentUserException.class)
	public void testDeleteNonexistentUser() {
		loginAsAdmin();
		getUserService().deleteUser("test");
	}

	@Test
	public void testGetUser() {
		loginAsAdmin();
		getUserService().createUser("test", "test", ERole.ROLE_CONSUMER);
		UserPswd user = getUserService().getUser("test");
		assertEquals("test", user.getUserName());
		assertTrue(user.isUserInRole(ERole.ROLE_CONSUMER));
	}

	@Test(expected = NonexistentUserException.class)
	public void testGetNonexistentUser() {
		loginAsAdmin();
		getUserService().getUser("test");
	}

	@Test
	public void testGetAllUsers() {
		loginAsAdmin();
		getUserService().createUser("test", "test", ERole.ROLE_CONSUMER);
		List<UserPswd> users = getUserService().getAllUsers();
		// assertEquals(2, users.size());
		// TODO
	}

	@Test
	public void testGetCurrentUser() {
		loginAsAdmin();
		UserPswd user = getUserService().getCurrentUser();
		assertNotNull(user);
		assertEquals("admin", user.getUserName());
	}

	@Test
	public void testChangePassword() {
		loginAsAdmin();
		getUserService().createUser("test", "test", ERole.ROLE_CONSUMER);
		getUserService().changePassword("test", "123");
		UserPswd user = getUserService().getUser("test");
		assertEquals(Util.convertToMd5("123"), user.getPassword());
	}

	@Test(expected = NonexistentUserException.class)
	public void testChangePasswordOfNonexistentUser() {
		loginAsAdmin();
		getUserService().changePassword("test", "123");
	}

	@Test
	public void testChangeOwnPassword() {
		loginAsAdmin();
		getUserService().changePassword("test");
		UserPswd user = getUserService().getUser("admin");
		assertEquals(Util.convertToMd5("test"), user.getPassword());
	}

	@Test
	public void testChangeRole() {
		loginAsAdmin();
		getUserService().createUser("test", "test", ERole.ROLE_CONSUMER);
		getUserService().changeRole("test", ERole.ROLE_JOB_ADMIN);
		UserPswd user = getUserService().getUser("test");
		assertFalse(user.isUserInRole(ERole.ROLE_SYSTEM_ADMIN));
		assertTrue(user.isUserInRole(ERole.ROLE_JOB_ADMIN));
		assertTrue(user.isUserInRole(ERole.ROLE_SCHEDULE_ADMIN));
		assertTrue(user.isUserInRole(ERole.ROLE_CONSUMER));
		getUserService().changeRole("test", ERole.ROLE_SCHEDULE_ADMIN);
		assertFalse(user.isUserInRole(ERole.ROLE_SYSTEM_ADMIN));
		assertFalse(user.isUserInRole(ERole.ROLE_JOB_ADMIN));
		assertTrue(user.isUserInRole(ERole.ROLE_SCHEDULE_ADMIN));
		assertTrue(user.isUserInRole(ERole.ROLE_CONSUMER));
	}

	@Test(expected = NonexistentUserException.class)
	public void testChangeRoleOfNonexistentUser() {
		loginAsAdmin();
		getUserService().changeRole("sdfsd", ERole.ROLE_CONSUMER);
	}
}
