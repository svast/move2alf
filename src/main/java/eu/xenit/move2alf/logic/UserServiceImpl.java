package eu.xenit.move2alf.logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.xenit.move2alf.common.Util;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void changePassword(UserPswd user, String newPassword) {
		Util.convertToMd5(newPassword);
	}

	@Override
	public void changeRole(UserPswd user, ERole newRole) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createUser(String userName, String password, ERole role) {
		UserPswd newUser = new UserPswd();
		newUser.setUserName(userName);
		newUser.setPassword(Util.convertToMd5(password));
		Set<UserRole> userRoleSet = createRoleSet(userName, role);
		newUser.setUserRoleSet(userRoleSet);
		sessionFactory.getCurrentSession().save(newUser);
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
	public List<UserPswd> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPswd getCurrentUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserPswd getUser(String username) {
		List users = sessionFactory.getCurrentSession().createQuery(
				"from UserPswd as u where u.userName=?").setString(0, username)
				.list();
		if (users.size() == 1) {
			return (UserPswd) users.get(0);
		} else {
			return null;
		}
	}
}
