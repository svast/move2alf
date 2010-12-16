package eu.xenit.move2alf;

import static org.junit.Assert.*;

import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.dto.UserRole;
import eu.xenit.move2alf.core.enums.ERole;
import eu.xenit.move2alf.logic.UserService;
import eu.xenit.move2alf.logic.UserServiceImpl;

public class UserPswdTests extends DatabaseTests {

	private UserService userService;

	private AuthenticationManager authenticationManager;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	@Autowired
	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
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
	public void testCreateRoleSetConsumer() {
		UserServiceImpl userService = new UserServiceImpl();
		Set<UserRole> roles = userService.createRoleSet("test", ERole.CONSUMER);
		assertEquals(roles.size(), 1);
		assertTrue(roles.contains(new UserRole("test", ERole.CONSUMER)));
	}

	@Test
	public void testCreateRoleSetScheduleAdmin() {
		UserServiceImpl userService = new UserServiceImpl();
		Set<UserRole> roles = userService.createRoleSet("test",
				ERole.SCHEDULE_ADMIN);
		assertEquals(roles.size(), 2);
		assertTrue(roles.contains(new UserRole("test", ERole.CONSUMER)));
		assertTrue(roles.contains(new UserRole("test", ERole.SCHEDULE_ADMIN)));
	}

	@Test
	public void testCreateRoleSetJobAdmin() {
		UserServiceImpl userService = new UserServiceImpl();
		Set<UserRole> roles = userService
				.createRoleSet("test", ERole.JOB_ADMIN);
		assertEquals(roles.size(), 3);
		assertTrue(roles.contains(new UserRole("test", ERole.CONSUMER)));
		assertTrue(roles.contains(new UserRole("test", ERole.SCHEDULE_ADMIN)));
		assertTrue(roles.contains(new UserRole("test", ERole.JOB_ADMIN)));
	}

	@Test
	public void testCreateRoleSetSystemAdmin() {
		UserServiceImpl userService = new UserServiceImpl();
		Set<UserRole> roles = userService.createRoleSet("test",
				ERole.SYSTEM_ADMIN);
		assertEquals(roles.size(), 4);
		assertTrue(roles.contains(new UserRole("test", ERole.CONSUMER)));
		assertTrue(roles.contains(new UserRole("test", ERole.SCHEDULE_ADMIN)));
		assertTrue(roles.contains(new UserRole("test", ERole.JOB_ADMIN)));
		assertTrue(roles.contains(new UserRole("test", ERole.SYSTEM_ADMIN)));
	}
	
	@Test
	public void testUserService() {
		assertNotNull(getUserService());
	}

	@Test
	public void testGetCurrentUser() {
		Authentication auth = getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken("admin", "admin"));
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		UserPswd user = getUserService().getCurrentUser();
		assertNotNull(user);
		assertEquals(user.getUserName(), "admin");
	}
}
