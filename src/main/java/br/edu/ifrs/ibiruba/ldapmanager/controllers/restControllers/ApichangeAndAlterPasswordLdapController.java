package br.edu.ifrs.ibiruba.ldapmanager.controllers.restControllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.services.ChangePasswordService;
import br.edu.ifrs.ibiruba.ldapmanager.services.ResetPasswordService;


@RestController
@RequestMapping("/ldapprincipal")
public class ApichangeAndAlterPasswordLdapController {



	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgetPassword(@RequestBody AlterPasswordModel alterPasswordModel) {
		boolean passwordChangedAndSended = new ResetPasswordService().forgetPassword(alterPasswordModel.getUser()
				, alterPasswordModel.getTypeOfUser());
		return ResponseEntity.ok(passwordChangedAndSended);
	}

	@PostMapping("/changepassword")
	
	//@ResponseStatus(HttpStatus.OK)
	public  ResponseEntity<?> changePassword(@RequestBody AlterPasswordModel alterPasswordModel) {
		boolean passwordChanged = false;
		passwordChanged =	new ChangePasswordService().alterPassword(alterPasswordModel);
		
		return ResponseEntity.ok(passwordChanged);
	}

}
