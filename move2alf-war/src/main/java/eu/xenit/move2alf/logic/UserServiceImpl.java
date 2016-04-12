package eu.xenit.move2alf.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.common.exceptions.DuplicateUserException;
import eu.xenit.move2alf.common.exceptions.NonexistentUserException;
import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.dto.UserRole;
import eu.xenit.move2alf.core.enums.ERole;

@Service("userService")
public class UserServiceImpl extends AbstractHibernateService implements
		UserService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserServiceImpl.class);

	@Override
	public void changePassword(String newPassword) {
		UserPswd user = getCurrentUser();
		changePassword(user, newPassword);
	}

	@Override
	public void changePassword(String userName, String newPassword) {
		UserPswd user = getUser(userName);
		changePassword(user, newPassword);
	}

	private void changePassword(UserPswd user, String newPassword) {
		user.setPassword(Util.convertToMd5(newPassword));
		getSessionFactory().getCurrentSession().save(user);
	}

	@Override
	public void changeRole(String userName, ERole newRole) {
		UserPswd user = getUser(userName);
		Set<UserRole> userRoleSet = createRoleSet(userName, newRole);
		user.setUserRoleSet(userRoleSet);
		getSessionFactory().getCurrentSession().save(user);
	}

	@Override
	public UserPswd createUser(String userName, String password, ERole role) {
		UserPswd newUser = new UserPswd();
		newUser.setUserName(userName);
		newUser.setPassword(Util.convertToMd5(password));
		Set<UserRole> userRoleSet = createRoleSet(userName, role);
		newUser.setUserRoleSet(userRoleSet);
		try {
			sessionFactory.getCurrentSession().save(newUser);
		} catch (ConstraintViolationException e) {
			logger.debug("User already exists.");
			throw new DuplicateUserException();
		}
		return newUser;
	}

	@Override
	public void deleteUser(String userName) {
		UserPswd user = getUser(userName);
		sessionFactory.getCurrentSession().delete(user);
	}

	public Set<UserRole> createRoleSet(String userName, ERole role) {
		Set<UserRole> userRoleSet = new HashSet<UserRole>();
		// add role and all lower roles
		switch (role) {
		case ROLE_SYSTEM_ADMIN:
			userRoleSet.add(new UserRole(userName, ERole.ROLE_SYSTEM_ADMIN));
		case ROLE_JOB_ADMIN:
			userRoleSet.add(new UserRole(userName, ERole.ROLE_JOB_ADMIN));
		case ROLE_SCHEDULE_ADMIN:
			userRoleSet.add(new UserRole(userName, ERole.ROLE_SCHEDULE_ADMIN));
		case ROLE_CONSUMER:
			userRoleSet.add(new UserRole(userName, ERole.ROLE_CONSUMER));
		}
		return userRoleSet;
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public List<UserPswd> getAllUsers() {
		return getSessionFactory().getCurrentSession().createQuery(
				"from UserPswd").list();
	}

	@Override
	public UserPswd getCurrentUser() {
		return getUser(SecurityContextHolder.getContext().getAuthentication()
				.getName());
	}

	@Override
	public UserPswd getUser(String username) {
		@SuppressWarnings("unchecked")
		List users = sessionFactory.getCurrentSession().createQuery(
				"from UserPswd as u where u.userName=?").setString(0, username)
				.list();
		if (users.size() == 1) {
			return (UserPswd) users.get(0);
		} else {
			logger.debug("User does not exist.");
			throw new NonexistentUserException();
		}
	}
	
}
