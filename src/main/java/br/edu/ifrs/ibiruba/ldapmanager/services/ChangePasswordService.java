package br.edu.ifrs.ibiruba.ldapmanager.services;


import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.MainAdCrud;

public class ChangePasswordService {
	
	public boolean alterPassword(AlterPasswordModel alterPasswordModel) {
		
		
		boolean userWasAutenticated = validationOfUserAndPassword(alterPasswordModel.getUser().trim(), alterPasswordModel.getActualPassword().trim(), 
				alterPasswordModel.getTypeOfUser() );
	    boolean passwordChanged = false;
			if(userWasAutenticated) 
			passwordChanged = new MainAdCrud(alterPasswordModel.getTypeOfUser()).changePassword(alterPasswordModel.getUser().trim(), alterPasswordModel.getNewPassword().trim());
		System.out.println("user was authenticated: "+userWasAutenticated);
		return passwordChanged;
	}
	
	private boolean validationOfUserAndPassword(String user, String actualPassword, String tipoUsuario) {
		
		return new MainAdCrud(tipoUsuario).validateUser(user, actualPassword);
	}

	
	public static void main(String [] args) {
		AlterPasswordModel alterPasswordModel = new AlterPasswordModel();
		alterPasswordModel.setUser("gustavo.paulus");
		alterPasswordModel.setActualPassword("ifrs-1286428425");
		alterPasswordModel.setNewPassword("StrongPassword");
		System.out.println(new ChangePasswordService().alterPassword(alterPasswordModel));
	}
}
