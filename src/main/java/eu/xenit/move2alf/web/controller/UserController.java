package eu.xenit.move2alf.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.xenit.move2alf.common.Util;
import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.enums.ERole;
import eu.xenit.move2alf.logic.UserService;
import eu.xenit.move2alf.web.dto.DestinationInfo;
import eu.xenit.move2alf.web.dto.EditPassword;
import eu.xenit.move2alf.web.dto.EditRole;
import eu.xenit.move2alf.web.dto.JobConfig;
import eu.xenit.move2alf.web.dto.User;

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
		mav.addObject("users", getUserService().getAllUsers());
		mav.setViewName("manage-users");
		return mav;
	}

	@RequestMapping(value = "/users/add", method = RequestMethod.GET)
	public ModelAndView addUserForm() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("add-user");
		List role = new ArrayList();
		for(ERole myEnum : ERole.values()) {
			role.add(myEnum);
		}
		mav.addObject("roleList", role);
		mav.addObject("user", new User());
		return mav;
	}

	@RequestMapping(value = "/users/add", method = RequestMethod.POST)
	public ModelAndView addUser(@ModelAttribute("user") @Valid User user, BindingResult errors) {
		
		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: "+errors.toString());
			
			ModelAndView mav = new ModelAndView("add-user");
			List role = new ArrayList();
			for(ERole myEnum : ERole.values()) {
				role.add(myEnum);
			}
			mav.addObject("roleList", role);
			mav.addObject("user",user);
            return mav;
		}
		
		ModelAndView mav = new ModelAndView();
		logger.info("adding user " + user.getUserName());
		getUserService().createUser(user.getUserName(), user.getPassword(),
				ERole.valueOf(user.getRole()));
		mav.setViewName("redirect:/users/");
		return mav;
	}

	@RequestMapping(value = "/user/{userName}/delete", method = RequestMethod.GET)
	public ModelAndView confirmDeleteUser(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		logger.info("deleting user " + userName);
		mav.addObject("user", getUserService().getUser(userName));
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
		mav.setViewName("profile");
		return mav;
	}
	
	@RequestMapping(value = "/user/profile/{userName}/edit", method = RequestMethod.GET)
	public ModelAndView changePasswordForm(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("userClass", new EditPassword());
		mav.addObject("user", getUserService().getUser(userName));
		mav.setViewName("edit-profile");
		return mav;
	}
	
	@RequestMapping(value = "/user/profile/{userName}/edit", method = RequestMethod.POST)
	public ModelAndView changePassword(@PathVariable String userName, @ModelAttribute("userClass") @Valid EditPassword userClass, BindingResult errors) {
		
		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: "+errors.toString());
			
			ModelAndView mav = new ModelAndView("edit-profile");
			mav.addObject("userClass",userClass);
			mav.addObject("user", getUserService().getUser(userName));
            return mav;
		}
		
		ModelAndView mav = new ModelAndView();
		
		String oldPassword = getUserService().getUser(userName).getPassword();
		String oldPasswordEntered = Util.convertToMd5(userClass.getOldPassword());
		
		if(oldPassword.equals(oldPasswordEntered)){
			
			getUserService().changePassword(userClass.getNewPassword());
			mav.setViewName("redirect:/user/profile");
		}
		else{
			mav.setViewName("redirect:/user/profile/edit/failed");
		}
		return mav;
	}
	
	@RequestMapping(value = "/user/profile/edit/failed", method = RequestMethod.GET)
	public ModelAndView changePasswordFailed() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", getUserService().getCurrentUser());
		mav.setViewName("edit-profile-failed");
		return mav;
	}
	
	@RequestMapping(value = "/user/{userName}/edit/password", method = RequestMethod.GET)
	public ModelAndView editUserForm(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("userClass", new EditPassword());
		mav.addObject("user", getUserService().getUser(userName));
		mav.setViewName("edit-user");
		return mav;
	}
	
	@RequestMapping(value = "/user/{userName}/edit/password", method = RequestMethod.POST)
	public ModelAndView editUser(@PathVariable String userName, @ModelAttribute("userClass") @Valid EditPassword userClass, BindingResult errors) {
		
		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: "+errors.toString());
			
			ModelAndView mav = new ModelAndView("edit-user");
			mav.addObject("userClass",userClass);
			mav.addObject("user", getUserService().getUser(userName));
            return mav;
		}
		
		ModelAndView mav = new ModelAndView();
		
		String currentUserPassword = getUserService().getCurrentUser().getPassword();
		String currentUserPasswordEntered = Util.convertToMd5(userClass.getOldPassword());
		
		if(currentUserPassword.equals(currentUserPasswordEntered)){
			getUserService().changePassword(userName, userClass.getNewPassword());
			mav.setViewName("redirect:/users");
		}
		else{
			mav.setViewName("redirect:/user/"+userName+"/edit/password/failed");
		}
		return mav;
	}
	
	@RequestMapping(value = "/user/{userName}/edit/password/failed", method = RequestMethod.GET)
	public ModelAndView editUserPasswordFailed(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", getUserService().getUser(userName));
		mav.setViewName("edit-user-password-failed");
		return mav;
	}
	
	@RequestMapping(value = "/user/{userName}/edit/role", method = RequestMethod.GET)
	public ModelAndView editRoleForm(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("userClass", new EditRole());
		mav.addObject("user", getUserService().getUser(userName));
		List role = new ArrayList();
		for(ERole myEnum : ERole.values()) {
			role.add(myEnum);
		}
		mav.addObject("roleList", role);
		mav.setViewName("edit-user-role");
		return mav;
	}
	
	@RequestMapping(value = "/user/{userName}/edit/role", method = RequestMethod.POST)
	public ModelAndView editRole(@PathVariable String userName, @ModelAttribute("userClass") @Valid EditRole userClass, BindingResult errors) {
		
		if (errors.hasErrors()) {
			System.out.println("THE ERRORS: "+errors.toString());
			
			ModelAndView mav = new ModelAndView("edit-user-role");
			mav.addObject("userClass",userClass);
			List role = new ArrayList();
			for(ERole myEnum : ERole.values()) {
				role.add(myEnum);
			}
			mav.addObject("roleList", role);
			mav.addObject("user", getUserService().getUser(userName));
            return mav;
		}
		
		ModelAndView mav = new ModelAndView();
		
		String currentUserPassword = getUserService().getCurrentUser().getPassword();
		String currentUserPasswordEntered = Util.convertToMd5(userClass.getOldPassword());
		
		if(currentUserPassword.equals(currentUserPasswordEntered)){
			getUserService().changeRole(userName, ERole.valueOf(userClass.getRole()));
			mav.setViewName("redirect:/users");
		}
		else{
			mav.setViewName("redirect:/user/"+userName+"/edit/role/failed");
		}
		return mav;
	}
	
	@RequestMapping(value = "/user/{userName}/edit/role/failed", method = RequestMethod.GET)
	public ModelAndView editUserRoleFailed(@PathVariable String userName) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("user", getUserService().getUser(userName));
		mav.setViewName("edit-user-role-failed");
		return mav;
	}
}
