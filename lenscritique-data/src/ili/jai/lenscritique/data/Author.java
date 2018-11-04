package ili.jai.lenscritique.data;

import ili.jai.tdg.api.Persistable;

/**
 * The author of an article or a comment.
 * 
 * Such class is meant to allow the owner of an article to edit it or to remove
 * it.
 * 
 * A real issue will be to allow an easy authentication in the application.
 * 
 * The underlying database may be used as a UserDatabase in Tomcat to control
 * authentication.
 * 
 * See
 * http://tomcat.apache.org/tomcat-9.0-doc/config/realm.html#UserDatabase_Realm_-_org.apache.catalina.realm.UserDatabaseRealm
 * for details.
 * 
 * @author leberre
 *
 */
public class Author implements Persistable {

	private long id;
	private String pseudo;
	private String password;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Author [id=" + id + ", pseudo=" + pseudo + ", password=" + password + "]";
	}
}
