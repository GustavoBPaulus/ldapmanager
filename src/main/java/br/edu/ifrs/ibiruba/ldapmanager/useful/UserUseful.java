package br.edu.ifrs.ibiruba.ldapmanager.useful;

import java.util.HashMap;

import br.edu.ifrs.ibiruba.ldapmanager.entities.AlterPasswordModel;
import br.edu.ifrs.ibiruba.ldapmanager.entities.User;
import br.edu.ifrs.ibiruba.ldapmanager.repositories.MainAdCrud;

public class UserUseful {
	public String returnSpecifiedTypeOfUser(AlterPasswordModel alterPassword) {

		String specifiedTypeOfUser = "";
		HashMap<String, User> usuarios;

		if (alterPassword.getTypeOfUser().equalsIgnoreCase("servidor")) {
			usuarios = new MainAdCrud("tae").returnUserHashMap();
			if (usuarios.containsKey(alterPassword.getUser())) {
				specifiedTypeOfUser = "tae";
			}
			else if (specifiedTypeOfUser.equals("")) {
				usuarios = new MainAdCrud("docente").returnUserHashMap();

				if (usuarios.containsKey(alterPassword.getUser())) {
					specifiedTypeOfUser = "docente";
				}
			}
			else if (specifiedTypeOfUser.equals("")) {
				usuarios = new MainAdCrud("tercerizado").returnUserHashMap();

				if (usuarios.containsKey(alterPassword.getUser())) {
					specifiedTypeOfUser = "tercerizado";
				}
			}

		} else if (alterPassword.getTypeOfUser().equalsIgnoreCase("aluno")) {
			usuarios = new MainAdCrud("integrado").returnUserHashMap();
			if (usuarios.containsKey(alterPassword.getUser())) {
				specifiedTypeOfUser = "integrado";
			}else if(specifiedTypeOfUser.equals("")) {
				usuarios = new MainAdCrud("superior").returnUserHashMap();
				if (usuarios.containsKey(alterPassword.getUser()))
					specifiedTypeOfUser = "superior";
			}
			
		}
		System.out.println("tipo de usuário: "+ specifiedTypeOfUser);
		return specifiedTypeOfUser;
	}
}
