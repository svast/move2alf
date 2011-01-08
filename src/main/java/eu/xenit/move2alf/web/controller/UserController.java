package eu.xenit.move2alf.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.core.enums.ERole;
import eu.xenit.move2alf.logic.UserService;
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
		List<UserPswd> users = getUserService().getAllUsers();
		mav.addObject("users", users);
		mav.setViewName("manage-users");
		return mav;
	}

	@RequestMapping(value = "/users/add", method = RequestMethod.GET)
	public ModelAndView addUserForm() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("add-user");
		mav.addObject("user", new User());
		return mav;
	}

	@RequestMapping(value = "/users/add", method = RequestMethod.POST)
	public ModelAndView addUser(@ModelAttribute User user) {
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
		UserPswd user = getUserService().getUser(userName);
		mav.addObject("user", user);
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
}
