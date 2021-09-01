package br.edu.ifrs.ibiruba.ldapmanager.repositories;


import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

import br.edu.ifrs.ibiruba.ldapmanager.entities.User;




public class MainAdCrud {
	
	private String caminhoCnOuUnidadeOrganizacional = "";
	
	//por enquanto o tipo de usuário é Aluno e User, não temos
	public MainAdCrud(String tipoUsuario) {
		if(tipoUsuario.equalsIgnoreCase("integrado")) caminhoCnOuUnidadeOrganizacional = "OU=Integrado,".trim()+"OU=Alunos".trim(); 
		else if(tipoUsuario.equalsIgnoreCase("superior")) caminhoCnOuUnidadeOrganizacional = "OU=Superior,".trim()+"OU=Alunos".trim(); 
		else if(tipoUsuario.equalsIgnoreCase("tae")) caminhoCnOuUnidadeOrganizacional = "OU=Taes,".trim() +"OU=Servidores".trim();
		else if(tipoUsuario.equalsIgnoreCase("docente")) caminhoCnOuUnidadeOrganizacional = "OU=Docentes,".trim() +"OU=Servidores".trim();
		else if(tipoUsuario.equalsIgnoreCase("tercerizado")) caminhoCnOuUnidadeOrganizacional = "OU=Tercerizados,".trim() +"OU=Servidores".trim();
		else caminhoCnOuUnidadeOrganizacional ="CN=Users".trim();
	}
	
	
	public boolean validateUser(String user, String actualPassword) {
		 DirContext connection = null;
		 boolean connected = false;
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://192.168.56.48:389/DC=IBIRUBA,DC=IFRS");
		env.put(Context.SECURITY_PRINCIPAL, user+"@IBIRUBA.IFRS");
		env.put(Context.SECURITY_CREDENTIALS, actualPassword );
		try {
			connection = new InitialDirContext(env);
			System.out.println("connection: "+ connection);
			connected = true;
			connection.close();

		} catch (AuthenticationException ex) {
			System.out.println(ex.getMessage());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connected;
	}

	 
	public DirContext newConnection() {
		 DirContext connection = null;
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://192.168.56.48:389/DC=IBIRUBA,DC=IFRS");
		env.put(Context.SECURITY_PRINCIPAL, "Administrator@IBIRUBA.IFRS");
		env.put(Context.SECURITY_CREDENTIALS, "@lface#81");
		try {
			connection = new InitialDirContext(env);
			System.out.println("Hello World!" + connection);

		} catch (AuthenticationException ex) {
			System.out.println(ex.getMessage());
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

		public String addUser(User user) throws UnsupportedEncodingException {
	
			
		String answerResult = "";
		
		DirContext connection =  newConnection();	
		Attributes attributes = new BasicAttributes();
		Attribute attribute = new BasicAttribute("objectClass");
		attribute.add("User");

		attributes.put(attribute);

		// usuário
		attributes.put("samAccountName", user.getSamaccountname());
		// nome completo, preferir usar o mesmo que o do usuário
		attributes.put("cn", user.getCn());
		// Primeiro nome
		attributes.put("givenName", user.getGivenName());
		// sobrenome
		attributes.put("sn", user.getSn());
		// nome do usuário para o display
		//attributes.put("displayName", user.ge);
		
		// email
		attributes.put("mail", user.getMail());
		// password
		attributes.put("userpassword", user.getPassword());

		// some useful constants from lmaccess.h
		int UF_ACCOUNTDISABLE = 0x0002;
		int UF_PASSWD_NOTREQD = 0x0020;
		int UF_PASSWD_CANT_CHANGE = 0x0040;
		int UF_NORMAL_ACCOUNT = 0x0200;
		int UF_DONT_EXPIRE_PASSWD = 0x10000;
		int UF_PASSWORD_EXPIRED = 0x800000;
		// UF_PASSWORD_EXPIRED + UF_ACCOUNTDISABLE));
		attributes.put("userAccountControl", Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD));

		try {
			connection.createSubcontext("CN="+user.getCn()+","+caminhoCnOuUnidadeOrganizacional, attributes);
			answerResult = "success";
			System.out.println(answerResult);

			
			changePassword(user.getCn(), user.getPassword());
			System.out.println("Set password & updated userccountControl");

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connection.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return answerResult;
	}

	private static byte[] getPassword(String password) throws UnsupportedEncodingException {
		String newQuotedPassword = "\"" + password + "\"";
		return newQuotedPassword.getBytes("UTF-16LE");
	}

	public boolean changePassword(String userCN, String newPassword){
		boolean changed = false;
		
		DirContext connection = newConnection();
		String userCNComplete = "CN="+userCN+","+caminhoCnOuUnidadeOrganizacional;
		try {
			modifyAdAttribute(connection, userCNComplete, "unicodePwd", getPassword(newPassword));
			changed = true;
			System.out.println("Password changed for " + userCNComplete);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				connection.close();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return changed;
	}

	private void modifyAdAttribute(DirContext ldapContext, String userCN, String attribute, Object value)
			throws NamingException {
		ModificationItem[] modificationItem = new ModificationItem[1];
		modificationItem[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attribute, value));
		ldapContext.modifyAttributes(userCN, modificationItem);
	}

	public void addUserToGroup(String username, String groupName) {
		 DirContext connection =  newConnection();	
		ModificationItem[] mods = new ModificationItem[1];
		Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + username + ",ou=users");
		mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
		try {
			connection.modifyAttributes("cn=" + groupName + ",ou=groups", mods);
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connection.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteUser(User user) {
		 DirContext connection =  newConnection();	
		try {
			connection.destroySubcontext("cn="+user.getCn()+",ou=users");
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connection.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteUserFromGroup(String username, String groupName) {
		 DirContext connection =  newConnection();	
		ModificationItem[] mods = new ModificationItem[1];
		Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + username + ",ou=users,ou=system");
		mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
		try {
			connection.modifyAttributes("cn=" + groupName + ",ou=groups", mods);
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			connection.close();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
public HashMap<String, User> returnUserHashMap() {
	 DirContext connection =  newConnection();		
	HashMap<String, User> usersHash = new HashMap<String, User>();	
	try {
			connection =  newConnection();
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=user)";
			NamingEnumeration values = connection.search(caminhoCnOuUnidadeOrganizacional,filter,searchCtrls);
			
			
			while (values.hasMoreElements())
			{
				User user = new User();
				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();
 
				if (null != attribs)
				{
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();)
					{
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll();vals.hasMoreElements();) { 
							Object actualEnumaration = vals.nextElement();
								//System.out.println(attributeID +": "+ actualEnumaration.toString());
							
								if(attributeID.trim().equals("sn")) 
									user.setSn(actualEnumaration.toString());
								else if(attributeID.trim().equals("sAMAccountName".trim()))
									user.setSamaccountname(actualEnumaration.toString());
								else if(attributeID.trim().equals("mail".trim()))
									user.setMail(actualEnumaration.toString());
								else if(attributeID.trim().equals("cn".trim()))
									user.setCn(actualEnumaration.toString());
								else if(attributeID.trim().equals("givenName".trim()))
									user.setGivenName(actualEnumaration.toString());
								else if(attributeID.trim().equals("name".trim()))
									user.setName(actualEnumaration.toString());
								else if(attributeID.trim().equals("ou".trim()))
									user.setOu(actualEnumaration.toString());
						}
						}
				}
				usersHash.put(user.getSamaccountname(), user);
				//System.out.println(user.toString());
			}
 
			connection.close();
 
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return usersHash;
	}

	public void listAllUser() {

		 DirContext connection =  newConnection();	
		try {
			
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=user)";
			NamingEnumeration values = connection.search(caminhoCnOuUnidadeOrganizacional, filter, searchCtrls);
			while (values.hasMoreElements()) {

				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();

				if (null != attribs) {
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll(); vals.hasMoreElements(); System.out
								.println(attributeID + ": " + vals.nextElement()))
							;
					}
				}
			}

			connection.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public void listAllUsersAndAllAttributes() {
		 DirContext connection =  newConnection();	
		try {
			
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=*)";
			NamingEnumeration values = connection.search(caminhoCnOuUnidadeOrganizacional, filter, searchCtrls);
			while (values.hasMoreElements()) {
				SearchResult result = (SearchResult) values.next();
				Attributes attribs = result.getAttributes();

				if (null != attribs) {
					for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
						Attribute atr = (Attribute) ae.next();
						String attributeID = atr.getID();
						for (Enumeration vals = atr.getAll(); vals.hasMoreElements(); System.out
								.println(attributeID + ": " + vals.nextElement()))
							;
					}
				}
			}

			connection.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws NamingException {

		MainAdCrud app = new MainAdCrud("tae");
		app.newConnection();
		app.listAllUsersAndAllAttributes();
		app.validateUser("gustavo.paulus", "StrongPassword080811");
		app.listAllUsersAndAllAttributes();
		
		app.changePassword("gustavo.paulus", "StrongPassword123");
		 
		 //app.listAllUser();
		/*
		User user = new User();
		user.setCn("teste.gustavo");
		user.setGivenName("teste");
		user.setMail("teste@fda.com");
		user.setName("Teste tustavo");
		user.setPassword("print12");
		user.setSamaccountname("teste.gustavo");
		user.setSn("gustavo");
		try {
			app.addUser(user);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		//app.returnUserHashMap();
	//	boolean validou = app.validateUser("gustavo.paulus", "ifrs-1286428425");
		//System.out.println(validou);
		
		try {
			System.out.println(getPassword("teste"));
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}