package eu.xenit.move2alf.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.dto.UserRole;
import eu.xenit.move2alf.core.enums.ERole;
import eu.xenit.move2alf.logic.UserService;
import eu.xenit.move2alf.web.dto.EditPassword;
import eu.xenit.move2alf.web.dto.EditRole;
import eu.xenit.move2alf.web.dto.User;
import eu.xenit.move2alf.web.dto.UserInfo;

@Controller
public class UserController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserController.class);

	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public UserService getUserService() {
		return userService;
	}

	@RequestMapping("/users")
	public ModelAndView manageUsers() {
		ModelAndView mav = new ModelAndView();
		List<UserInfo> userInfoList = new ArrayList();
		List<UserPswd> allUsers = getUserService().getAllUsers();
	//	mav.addObject("users", getUserService().getAllUsers());
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		
		for(int i =0;i<allUsers.size(); i++){
			UserInfo userInfo = new UserInfo();
			String userName = allUsers.get(i).getUserName();
			userInfo.setUserName(userName);
			
			Set userRole = getUserService().getUser(userName)
					.getUserRoleSet();
	
			//Makes sure the correct role is already selected
			String roleCheck="";
			Iterator roleIterator = userRole.iterator();
			while(roleIterator.hasNext()){
				String currentRole = ((UserRole) roleIterator.next()).getRole();
				if("SYSTEM_ADMIN".equals(currentRole)){
					roleCheck="System admin";
				}
				if(roleCheck=="Consumer" ||roleCheck=="Schedule admin" || roleCheck==""){
					if("JOB_ADMIN".equals(currentRole)){
						roleCheck="Job admin";
					}
				}
				if(roleCheck=="Consumer" || roleCheck==""){
					if("SCHEDULE_ADMIN".equals(currentRole)){
						roleCheck="Schedule admin";
					}
				}
				if(roleCheck==""){
					if("CONSUMER".equals(currentRole)){
						roleCheck="Consumer";
					}
				}
			}
			
			userInfo.setRole(roleCheck);
			
			userInfoList.add(userInfo);
			
		}
		
		mav.addObject("userInfoList", userInfoList);
		
		mav.setViewName("manage-users");
		return mav;
	}

	@RequestMapping(value = "/user/add", method = RequestMethod.GET)
	public ModelAndView addUserForm() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("add-user");
		List role = new ArrayList();
		for (ERole myEnum : ERole.values()) {
			role.add(myEnum.getDisplayName());
		}
		mav.addObject("roleList", role);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.addObject("user", new User());
		return mav;
	}

	@RequestMapping(value = "/user/add", method = RequestMethod.POST)
	public ModelAndView addUser(@ModelAttribute("user") @Valid User user,
			BindingResult errors) {

		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: " + errors.toString());

			ModelAndView mav = new ModelAndView("add-user");
			List role = new ArrayList();
			for (ERole myEnum : ERole.values()) {
				role.add(myEnum.getDisplayName());
			}
			mav.addObject("roleList", role);
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());
			mav.addObject("user", user);
			return mav;
		}

		ModelAndView mav = new ModelAndView();
		logger.info("adding user " + user.getUserName());
		getUserService().createUser(user.getUserName(), user.getPassword(),
				ERole.getByDisplayName(user.getRole()));
		mav.setViewName("redirect:/users/");
		return mav;
	}


	@RequestMapping(value = "/user/{userName}/delete", method = RequestMethod.GET)
	public ModelAndView confirmDeleteUser(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		logger.info("deleting user " + userName);
		mav.addObject("user", getUserService().getUser(userName));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("delete-user");
		return mav;
	}

	@RequestMapping(value = "/user/{userName}/delete", method = RequestMethod.POST)
	public ModelAndView deleteUser(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		logger.info("deleting user " + userName);
		getUserService().deleteUser(userName);
		mav.setViewName("redirect:/users/");
		return mav;
	}

	@RequestMapping("/user/profile")
	public ModelAndView profile() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", getUserService().getCurrentUser());
		
		Set userRole = getUserService().getCurrentUser()
				.getUserRoleSet();

		//Makes sure the correct role is already selected
		String roleCheck="";
		Iterator roleIterator = userRole.iterator();
		while(roleIterator.hasNext()){
			String currentRole = ((UserRole) roleIterator.next()).getRole();
			if("SYSTEM_ADMIN".equals(currentRole)){
				roleCheck="System admin";
			}
			if(roleCheck=="Consumer" ||roleCheck=="Schedule admin" || roleCheck==""){
				if("JOB_ADMIN".equals(currentRole)){
					roleCheck="Job admin";
				}
			}
			if(roleCheck=="Consumer" || roleCheck==""){
				if("SCHEDULE_ADMIN".equals(currentRole)){
					roleCheck="Schedule admin";
				}
			}
			if(roleCheck==""){
				if("CONSUMER".equals(currentRole)){
					roleCheck="Consumer";
				}
			}
		}
		
		mav.addObject("role", roleCheck);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("profile");
		return mav;
	}

	@RequestMapping(value = "/user/profile/{userName}/edit", method = RequestMethod.GET)
	public ModelAndView changePasswordForm(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("userClass", new EditPassword());
		mav.addObject("user", getUserService().getUser(userName));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("edit-password");
		return mav;
	}

	@RequestMapping(value = "/user/profile/{userName}/edit", method = RequestMethod.POST)
	public ModelAndView changePassword(@PathVariable String userName,
			@ModelAttribute("userClass") @Valid EditPassword userClass,
			BindingResult errors) {

		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: " + errors.toString());

			ModelAndView mav = new ModelAndView("edit-profile");
			mav.addObject("userClass", userClass);
			mav.addObject("user", getUserService().getUser(userName));
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());
			return mav;
		}

		ModelAndView mav = new ModelAndView();

		String oldPassword = getUserService().getUser(userName).getPassword();
		String oldPasswordEntered = Util.convertToMd5(userClass
				.getOldPassword());

		if (oldPassword.equals(oldPasswordEntered)) {

			getUserService().changePassword(userClass.getNewPassword());
			mav.setViewName("redirect:/user/profile");
		} else {
			mav.setViewName("redirect:/user/profile/edit/failed");
		}
		return mav;
	}

	@RequestMapping(value = "/user/profile/edit/failed", method = RequestMethod.GET)
	public ModelAndView changePasswordFailed() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", getUserService().getCurrentUser());
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("edit-profile-failed");
		return mav;
	}

	@RequestMapping(value = "/user/{userName}/edit/password", method = RequestMethod.GET)
	public ModelAndView editUserForm(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("editPassword", new EditPassword());
		mav.addObject("user", getUserService().getUser(userName));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("edit-user");
		return mav;
	}

	@RequestMapping(value = "/user/{userName}/edit/password", method = RequestMethod.POST)
	public ModelAndView editUser(@PathVariable String userName,
			@ModelAttribute("editPassword") @Valid EditPassword editPassword,
			BindingResult errors) {

		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: " + errors.toString());

			ModelAndView mav = new ModelAndView("edit-user");
			mav.addObject("editPassword", editPassword);
			mav.addObject("user", getUserService().getUser(userName));
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());
			return mav;
		}

		ModelAndView mav = new ModelAndView();

		String currentUserPassword = getUserService().getCurrentUser()
				.getPassword();
		String currentUserPasswordEntered = Util.convertToMd5(editPassword
				.getOldPassword());

		if (currentUserPassword.equals(currentUserPasswordEntered)) {
			getUserService().changePassword(userName,
					editPassword.getNewPassword());
			mav.setViewName("redirect:/users");
		} else {
			mav.setViewName("redirect:/user/" + userName
					+ "/edit/password/failed");
		}
		return mav;
	}

	@RequestMapping(value = "/user/{userName}/edit/password/failed", method = RequestMethod.GET)
	public ModelAndView editUserPasswordFailed(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", getUserService().getUser(userName));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("edit-user-password-failed");
		return mav;
	}

	@RequestMapping(value = "/user/{userName}/edit/role", method = RequestMethod.GET)
	public ModelAndView editRoleForm(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		EditRole editRole = new EditRole();
		editRole.setRole(((UserRole) (getUserService().getUser(userName).getUserRoleSet().toArray()[0])).getRoleType().getDisplayName());
		mav.addObject("editRole", editRole);
		mav.addObject("user", getUserService().getUser(userName));
		
		
		List roles = new ArrayList();
		for (ERole myEnum : ERole.values()) {
				roles.add(myEnum.getDisplayName());
		}
		
		mav.addObject("roleList", roles);
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("edit-user-role");
		return mav;
	}

	@RequestMapping(value = "/user/{userName}/edit/role", method = RequestMethod.POST)
	public ModelAndView editRole(@PathVariable String userName,
			@ModelAttribute("editRole") @Valid EditRole editRole,
			BindingResult errors) {

		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: " + errors.toString());

			ModelAndView mav = new ModelAndView("edit-user-role");
			mav.addObject("editRole", editRole);
			
			//Makes sure the correct role is already selected
			Set userRole = getUserService().getUser(userName)
			.getUserRoleSet();
			String roleCheck="";
			Iterator roleIterator = userRole.iterator();
			while(roleIterator.hasNext()){
				String currentRole = ((UserRole) roleIterator.next()).getRole();
				if("SYSTEM_ADMIN".equals(currentRole)){
					roleCheck=currentRole;
				}
				if(roleCheck=="Consumer" ||roleCheck=="Schedule admin" || roleCheck==""){
					if("JOB_ADMIN".equals(currentRole)){
						roleCheck=currentRole;
					}
				}
				if(roleCheck=="Consumer" || roleCheck==""){
					if("SCHEDULE_ADMIN".equals(currentRole)){
						roleCheck=currentRole;
					}
				}
				if(roleCheck==""){
					if("CONSUMER".equals(currentRole)){
						roleCheck=currentRole;
					}
				}
			}
			
			List role = new ArrayList();
			for (ERole myEnum : ERole.values()) {
				if(myEnum.toString().equals(roleCheck)){
					role.add(0,myEnum.getDisplayName());
				}else{
					role.add(myEnum.getDisplayName());
				}
			}
			mav.addObject("roleList", role);
			mav.addObject("roles", getUserService().getCurrentUser()
					.getUserRoleSet());
			mav.addObject("user", getUserService().getUser(userName));
			return mav;
		}

		ModelAndView mav = new ModelAndView();

		String currentUserPassword = getUserService().getCurrentUser()
				.getPassword();
		String currentUserPasswordEntered = Util.convertToMd5(editRole
				.getOldPassword());

		if (currentUserPassword.equals(currentUserPasswordEntered)) {
			getUserService().changeRole(userName,
					ERole.getByDisplayName(editRole.getRole()));
			mav.setViewName("redirect:/users");
		} else {
			mav.setViewName("redirect:/user/" + userName + "/edit/role/failed");
		}
		return mav;
	}

	@RequestMapping(value = "/user/{userName}/edit/role/failed", method = RequestMethod.GET)
	public ModelAndView editUserRoleFailed(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", getUserService().getUser(userName));
		mav.addObject("roles", getUserService().getCurrentUser()
				.getUserRoleSet());
		mav.setViewName("edit-user-role-failed");
		return mav;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		return mav;
	}
}
