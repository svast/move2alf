package eu.xenit.move2alf.web.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import eu.xenit.move2alf.core.dto.UserRole;
import eu.xenit.move2alf.logic.UserService;

public abstract class AbstractController {

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	protected String getRole(){
		Set<UserRole> roles = getUserService().getCurrentUser().getUserRoleSet();
		return Collections.max(roles, new Comparator<UserRole>(){
			@Override
			public int compare(UserRole o1, UserRole o2) {
				return new ERoleComparator().compare(o1.getRoleType(), o2.getRoleType());
			}
		}).getRole();
	}
}
