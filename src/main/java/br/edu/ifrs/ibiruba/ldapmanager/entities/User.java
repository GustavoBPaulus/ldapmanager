package br.edu.ifrs.ibiruba.ldapmanager.entities;

public class User {

	private String name;
	private String samaccountname;
	private String givenName;
	private String cn;
	private String sn;
	private String mail;
	private String password;
	
	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSamaccountname() {
		return samaccountname;
	}

	public void setSamaccountname(String samaccountname) {
		this.samaccountname = samaccountname;
	}

	
	
	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", samaccountname=" + samaccountname + ", givenName=" + givenName + ", cn=" + cn
				+ ", sn=" + sn + ", mail=" + mail + "]";
	}
	
	
}
