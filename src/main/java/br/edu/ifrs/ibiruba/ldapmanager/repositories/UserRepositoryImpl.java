package br.edu.ifrs.ibiruba.ldapmanager.repositories;

import java.util.List;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import br.edu.ifrs.ibiruba.conectaldap.domainldapprincipal.model.User;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

public class UserRepositoryImpl implements UserRepository {
	public static final String BASE_DN = "dc=ibi,dc=ifrs, dc=local";

	@Autowired
	private LdapTemplate ldapTemplate;

	@Override
    public String create(User u) {
        Name dn = buildDn(u.getUserId());
        ldapTemplate.bind(dn, null, buildAttributes(u));
        return u.getUserId() + " created successfully";
    }
 
    @Override
    public String update(User u) {
        Name dn = buildDn(u.getUserId());
        ldapTemplate.rebind(dn, null, buildAttributes(u));
        return u.getUserId() + " updated successfully";
    }
 
    @Override
    public String remove(String userId) {
        Name dn = buildDn(userId);
        // ldapTemplate.unbind(dn, true); //Remove recursively all entries
        ldapTemplate.unbind(dn);
        return userId + " removed successfully";
    }
 
    private Attributes buildAttributes(User u) {
 
        BasicAttribute ocattr = new BasicAttribute("objectclass");
        ocattr.add("top");
        ocattr.add("person");
 
        Attributes attrs = new BasicAttributes();
        attrs.put(ocattr);
        attrs.put("uid", u.getUserId());
        attrs.put("cn", u.getFullName());
        attrs.put("sn", u.getLastName());
        attrs.put("description", u.getDescription());
        attrs.put("email", u.getEmail());
        
        return attrs;
    }
 
    public Name buildDn(String userId) {
        return LdapNameBuilder.newInstance(BASE_DN).add("ou", "Users").add("uid", userId).build();
    }
 
    public Name buildBaseDn() {
        return LdapNameBuilder.newInstance(BASE_DN).add("ou", "Users").build();
    }
 
    @Override
    public List<User> retrieve() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        List<User> people = ldapTemplate.search(query().where("objectclass").is("user"),
                new PersonAttributeMapper());
        return people;
    }
 
    private class PersonAttributeMapper implements AttributesMapper<User> {
    	 
        @Override
        public User mapFromAttributes(Attributes attributes) throws NamingException {
            User user = new User();
            user.setUserId(null != attributes.get("uid") ? attributes.get("uid").get().toString() : null);
            user.setFullName(null != attributes.get("cn") ? attributes.get("cn").get().toString() : null);
            user.setLastName(null != attributes.get("sn") ? attributes.get("sn").get().toString() : null);
            user.setDescription(null != attributes.get("description") ? attributes.get("description").get().toString() : null);
            user.setEmail(null != attributes.get("email") ? attributes.get("email").get().toString() : null);
            return user;
        }
    }
}
