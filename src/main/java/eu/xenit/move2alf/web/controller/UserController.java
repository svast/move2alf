package eu.xenit.move2alf.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {
	@RequestMapping("/users")
	public ModelAndView manageUsers() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("manage-users");
		return mav;
	}
}
