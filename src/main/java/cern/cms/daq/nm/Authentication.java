package cern.cms.daq.nm;

import java.text.MessageFormat;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class Authentication {

	private final String ldapAdress;
	private final String ldapDC;
	private final String ldapPrincipal;

	public static void main(String[] args) throws NamingException {

		boolean test = false;
		Authentication ldapAuthenticator;
		String user;
		String password;
		String searchScope;
		boolean sslEnabled;
		if (test) {
			System.out.println("Testing external ldap authentication");
			ldapAuthenticator = new Authentication(Constants.testLDAP, Constants.testDC, "uid");
			user = "tesla";
			password = "password";
			sslEnabled = false;
			searchScope = Constants.testDC;
		} else {

			System.out.println("Testing CERN ldap authentication");
			ldapAuthenticator = new Authentication();
			user = "mgladki";
			password = "a";
			sslEnabled = true;
			searchScope = "ou=people,o=cern,c=ch";
		}

		DirContext ctx = ldapAuthenticator.authenticate(user, password, sslEnabled);
		
		if(ctx == null){
			System.err.println("not authenticated");
			return;
		}

		ldapAuthenticator.getDetails(ctx, searchScope, "(uid={0})", user);

		boolean result = ctx != null;

		System.out.println("Auhenticated? " + result);

		if (ctx != null)
			ctx.close();

	}

	public Authentication() {
		this(Constants.cernLDAP, Constants.cernDC, "cn");
	}

	public Authentication(String ldapAddress, String ldapDC, String ldapPrincipal) {
		this.ldapAdress = ldapAddress;
		this.ldapDC = ldapDC;
		this.ldapPrincipal = ldapPrincipal;
	}

	public void getDetails(DirContext ctx, String scope, String searchFilter, String searchValue)
			throws NamingException {

		String attribute1 = "mail";
		String attribute2 = "mobile";
		String attribute3 = "givenname";
		String attribute4 = "telephoneNumber";
		String attribute5 = "displayName";
		String attribute6 = "employeeid";

		String[] attributesToReturn = new String[] { attribute1, attribute2, attribute3, attribute4, attribute5,
				attribute6 };

		SearchResult sr = executeSearchSingleResult(ctx, SearchControls.SUBTREE_SCOPE, scope,
				MessageFormat.format(searchFilter, new Object[] { searchValue }), attributesToReturn);

		System.out.println(sr);

		Attribute id = sr.getAttributes().get(attribute6);
		String idn = (String) id.get();
		System.out.println(idn);
	}

	public DirContext authenticate(String username, String passwd, boolean ssl) {
		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapAdress);

		if (ssl) {
			// according to
			// https://docs.oracle.com/javase/tutorial/jndi/ldap/ssl.html
			env.put(Context.SECURITY_PROTOCOL, "ssl");
		}

		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, ldapPrincipal + "=" + username + "," + ldapDC);
		env.put(Context.SECURITY_CREDENTIALS, passwd);

		// Create the initial context
		DirContext ctx;

		try {
			ctx = new InitialDirContext(env);

			return ctx;
		} catch (NamingException e) {
			e.printStackTrace();

			return null;
		}

	}

	private static NamingEnumeration executeSearch(DirContext ctx, int searchScope, String searchBase,
			String searchFilter, String[] attributes) throws NamingException {
		// Create the search controls
		SearchControls searchCtls = new SearchControls();

		// Specify the attributes to return
		if (attributes != null) {
			searchCtls.setReturningAttributes(attributes);
		}

		// Specify the search scope
		searchCtls.setSearchScope(searchScope);

		// Search for objects using the filter
		NamingEnumeration result = ctx.search(searchBase, searchFilter, searchCtls);
		return result;
	}

	private static SearchResult executeSearchSingleResult(DirContext ctx, int searchScope, String searchBase,
			String searchFilter, String[] attributes) throws NamingException {
		NamingEnumeration result = executeSearch(ctx, searchScope, searchBase, searchFilter, attributes);

		SearchResult sr = null;
		// Loop through the search results
		while (result.hasMoreElements()) {
			sr = (SearchResult) result.next();
			break;
		}
		return sr;
	}
}
