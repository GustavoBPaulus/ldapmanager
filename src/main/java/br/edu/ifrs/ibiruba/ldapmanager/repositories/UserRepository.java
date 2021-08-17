package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import java.util.List;

import br.edu.ifrs.ibiruba.conectaldap.domainldapprincipal.model.User;

public interface UserRepository {
	public List<User> retrieve();
    public String create(User u);
    public String update(User u);
    public String remove(String userId);
}
