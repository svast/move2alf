package eu.xenit.move2alf.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import eu.xenit.move2alf.core.dto.UserPswd;
import eu.xenit.move2alf.logic.UserService;

@Controller
public class UserController {
	
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
}
