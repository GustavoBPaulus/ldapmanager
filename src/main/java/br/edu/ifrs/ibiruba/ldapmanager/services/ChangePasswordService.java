package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.util.HashMap;

import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.User;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.MainAdCrud;
import br.edu.ifrs.ibiruba.ldapmanager.useful.UserUseful;

public class ChangePasswordService {

	public boolean alterPassword(AlterPasswordModel alterPasswordModel) {

		String tipoDeAlunoOuTipoDeServidor = new UserUseful().returnSpecifiedTypeOfUser(alterPasswordModel);

		boolean userWasAutenticated = validationOfUserAndPassword(alterPasswordModel.getUser().trim(),
				alterPasswordModel.getActualPassword().trim(), alterPasswordModel.getTypeOfUser());
		boolean passwordChanged = false;
		if (userWasAutenticated)
			passwordChanged = new MainAdCrud(tipoDeAlunoOuTipoDeServidor)
					.changePassword(alterPasswordModel.getUser().trim(), alterPasswordModel.getNewPassword().trim());
		System.out.println("user was authenticated: " + userWasAutenticated);
		return passwordChanged;
	}



	private boolean validationOfUserAndPassword(String user, String actualPassword, String tipoUsuario) {

		return new MainAdCrud(tipoUsuario).validateUser(user, actualPassword);
	}

	public static void main(String[] args) {
		AlterPasswordModel alterPasswordModel = new AlterPasswordModel();
		alterPasswordModel.setUser("gustavo.paulus");
		alterPasswordModel.setActualPassword("ifrs-1286428425");
		alterPasswordModel.setNewPassword("StrongPassword");

	}
}
