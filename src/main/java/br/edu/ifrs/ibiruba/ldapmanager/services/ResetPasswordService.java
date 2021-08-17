package br.edu.ifrs.ibiruba.ldapmanager.services;

import java.util.Random;

import br.edu.ifrs.ibiruba.conectaldap.domainldapprincipal.model.MessageModel;
import br.edu.ifrs.ibiruba.ldapmanager.useful.MainAdCrud;
import br.edu.ifrs.ibiruba.ldapmanager.useful.SendMail;

public class ResetPasswordService {

	public boolean forgetPassword(String user, String tipoUsuario) {
		boolean userIsAstudent = false;
		
		String urlAlterarSenha = "http://timanager.ibiruba.ifrs.edu.br/alterar-senha";
		
		// instância um objeto da classe Random usando o construtor padrão
		Random gerador = new Random();
		// Gera uma senha aleatória
		String senhaTemporaria = "ifrs" + gerador.nextInt();
		
		
		boolean changed = false;
		// enviar email com a senha nova e link para alterar a senha com base na antiga,
		// aí ele primeiro autentica se autenticar certo permite alterar a senha
		MessageModel message = new MessageModel();
		message.setDestination(getMailFromUser(user, tipoUsuario));
		message.setRemetent("timanager@ibiruba.ifrs.edu.br");
		message.setSubject("Nova senha temporária");
		message.setText("A nova senha para o usuário: "+user+" é: "+senhaTemporaria+"\n Para alterar essa senha "
				+ "para uma de sua preferência acesse: "+urlAlterarSenha);

		boolean sended = new SendMail().sendMailLogic(message);
		if(sended) changed = new MainAdCrud(tipoUsuario).changePassword(user, senhaTemporaria);
		
		return changed && sended;
	}

	private String getMailFromUser(String user, String tipoUsuario) {
		System.out.println("email retornado do ldap: "+new MainAdCrud(tipoUsuario).returnUserHashMap().get(user).getMail());
		return  new MainAdCrud(tipoUsuario).returnUserHashMap().get(user).getMail();
		
	}
	
	public static void main(String[] args) {
		new ResetPasswordService().forgetPassword("gustavo.paulus", "aluno");
	}
}