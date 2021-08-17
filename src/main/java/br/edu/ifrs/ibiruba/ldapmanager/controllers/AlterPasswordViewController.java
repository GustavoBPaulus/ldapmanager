package br.edu.ifrs.ibiruba.ldapmanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/altermypassword")
public class AlterPasswordViewController {
	private static final String ALTERPASSWORD_VIEW = "AlterMyPassword";
	
	@RequestMapping
	public ModelAndView pesquisar() {
		
		ModelAndView mv = new ModelAndView("AlterMyPassword");
		
		return mv;
	}
	
}
