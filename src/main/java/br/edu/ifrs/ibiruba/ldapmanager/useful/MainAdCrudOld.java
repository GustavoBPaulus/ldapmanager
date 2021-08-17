package br.edu.ifrs.ibiruba.ldapmanager.useful;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;

import br.edu.ifrs.ibiruba.conectaldap.domainldapprincipal.model.User;

public class MainAdCrudOld {

	static DirContext connection;

	public DirContext newConnection() {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://192.168.56.8:389/DC=IBI,DC=IFRS,DC=LOCAL");
		env.put(Context.SECURITY_PRINCIPAL, "Administrator@IBI.IFRS.LOCAL");
		env.put(Context.SECURITY_CREDENTIALS, "print12");
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

	public void getAllUsers() throws NamingException {
		String searchFilter = "(User)";
		String[] reqAtt = { "cn", "sn" };
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(reqAtt);

		NamingEnumeration users = connection.search("CN=Users", searchFilter, controls);

		SearchResult result = null;
		while (users.hasMore()) {
			result = (SearchResult) users.next();
			Attributes attr = result.getAttributes();
			String name = attr.get("cn").get(0).toString();
			// deleteUserFromGroup(name,"Administrators");
			System.out.println(attr.get("cn"));
			System.out.println(attr.get("sn"));
		}

	}

	public void addUser() throws UnsupportedEncodingException {
		Attributes attributes = new BasicAttributes();
		Attribute attribute = new BasicAttribute("objectClass");
		attribute.add("User");

		attributes.put(attribute);

		// usuário
		attributes.put("samAccountName", "maik.teste");
		// nome completo, preferir usar o mesmo que o do usuário
		attributes.put("cn", "maik.teste");
		// Primeiro nome
		attributes.put("givenName", "maik");
		// sobrenome
		attributes.put("sn", "teste");
		// nome do usuário para o display
		attributes.put("displayName", "maik.teste");
		// email
		attributes.put("mail", "gustavo.paulus@ibiruba.ifrs.edu.br");
		// password
		attributes.put("userpassword", "print12");

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
			connection.createSubcontext("CN=maik.teste,CN=Users", attributes);
			System.out.println("success");

			// set password is a ldap modfy operation
			// and we'll update the userAccountControl
			// enabling the acount and force the user to update ther password
			// the first time they login
			ModificationItem[] mods = new ModificationItem[2];

			// Replace the "unicdodePwd" attribute with a new value
			// Password must be both Unicode and a quoted string
			String newQuotedPassword = "\"print12\"";
			// s String newQuotedPassword = "\"" + password + "\"";
			// String newQuotedPassword = "print12";
			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");

			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newUnicodePassword));
			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("userAccountControl", Integer.toString(UF_NORMAL_ACCOUNT)));

			// Perform the update
			connection.modifyAttributes("CN=maik.teste,CN=Users", mods);
			System.out.println("Set password & updated userccountControl");

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static byte[] getPassword(String password) throws UnsupportedEncodingException {
		String newQuotedPassword = "\"" + password + "\"";
		return newQuotedPassword.getBytes("UTF-16LE");
	}

	public void changePassword(DirContext ldapContext, String userCN, String newPassword)
			throws NamingException, UnsupportedEncodingException, IOException {

		modifyAdAttribute(ldapContext, userCN, "unicodePwd", getPassword(newPassword));
		System.out.println("Password changed for " + userCN);
	}

	private void modifyAdAttribute(DirContext ldapContext, String userCN, String attribute, Object value)
			throws NamingException {
		ModificationItem[] modificationItem = new ModificationItem[1];
		modificationItem[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(attribute, value));
		ldapContext.modifyAttributes(userCN, modificationItem);
	}

	public void addUserToGroup(String username, String groupName) {
		ModificationItem[] mods = new ModificationItem[1];
		Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + username + ",ou=users,ou=system");
		mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attribute);
		try {
			connection.modifyAttributes("cn=" + groupName + ",ou=groups,ou=system", mods);
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void deleteUser() {
		try {
			connection.destroySubcontext("cn=Tommy,ou=users");
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteUserFromGroup(String username, String groupName) {
		ModificationItem[] mods = new ModificationItem[1];
		Attribute attribute = new BasicAttribute("uniqueMember", "cn=" + username + ",ou=users,ou=system");
		mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute);
		try {
			connection.modifyAttributes("cn=" + groupName + ",ou=groups,ou=system", mods);
			System.out.println("success");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void searchUsers() throws NamingException {
		// String searchFilter = "(uid=1)"; // for one user
		// String searchFilter = "(&(uid=1)(cn=Smith))"; // and condition
		String searchFilter = "(CN=maik.teste,CN=Users)"; // or condition
		String[] reqAtt = { "cn", "sn" };
		SearchControls controls = new SearchControls();
		controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		controls.setReturningAttributes(reqAtt);

		NamingEnumeration users = connection.search("CN=Users", searchFilter, controls);

		SearchResult result = null;
		while (users.hasMore()) {
			result = (SearchResult) users.next();
			Attributes attr = result.getAttributes();
			String name = attr.get("cn").get(0).toString();
			// deleteUserFromGroup(name,"Administrators");
			System.out.println(attr.get("cn"));
			System.out.println(attr.get("sn"));

		}

	}

public HashMap<String, User> returnUserHashMap() {
		
	HashMap<String, User> usersHash = new HashMap<String, User>();	
	try {
			connection =  newConnection();
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=user)";
			NamingEnumeration values = connection.search("cn=Users",filter,searchCtrls);
			
			
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
						}
						}
				}
				usersHash.put(user.getSamaccountname(), user);
				System.out.println(user.toString());
			}
 
			connection.close();
 
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return usersHash;
	}

	public void listAllUser() {

		try {
			connection = newConnection();
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=user)";
			NamingEnumeration values = connection.search("cn=Users", filter, searchCtrls);
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

		try {
			connection = newConnection();
			SearchControls searchCtrls = new SearchControls();
			searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectClass=*)";
			NamingEnumeration values = connection.search("cn=Users", filter, searchCtrls);
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

		MainAdCrudOld app = new MainAdCrudOld();
		app.newConnection();
		/*
		 * try { app.addUser(); } catch (UnsupportedEncodingException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * try { app.changePassword(connection, "CN=maik.teste,CN=Users", "@lface#236");
		 * } catch (NamingException | IOException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 */
		// app.getAllUsers();
		// app.deleteUser();
		// app.searchUsers();
		// app.listAllUsersAndAllAttributes();
		//app.listAllUser();
		app.returnUserHashMap();
	}
}