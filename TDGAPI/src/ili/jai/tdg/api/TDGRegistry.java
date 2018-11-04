package ili.jai.tdg.api;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Single entry point to access implementations of of 
 * Table Data Gateway implementations for {@link Persistable} classes.
 * In order to be generic, the class assumes that a file <code>registry.properties</code>
 * is available on the classpath at the root level.
 * That file contains two important information: 
 * <ul>
 * <li>jdbcurl the jdbc url to access the database</li>
 * <li>the mapping associating each persistable class to its TDG implementation.</li>
 * </ul>  
 * Here is an example of <code>registry.properties</code> file:
 * <pre>
 * jdbcurl=jdbc:derby:DEMODB;create=true
 * jai.jdbc1.Automobile=jai.jdbc1.AutomobileTDG
 * jai.jdbc1.Personne=jai.jdbc1.PersonneTDG
 * </pre>
 * @author leberre
 *
 */
public class TDGRegistry {

	private static final Logger LOGGER = Logger.getLogger(TDGRegistry.class.getName());
	private static final Map<Class<?>, GenericTDG<?>> REGISTRY = new HashMap<>();
	private static Connection conn;
	private static String jdbcurl;

	static {
		Properties properties = new Properties();
		try {
			properties.load(TDGRegistry.class.getResourceAsStream("/registry.properties"));
			jdbcurl = properties.getProperty("jdbcurl");
			if (jdbcurl == null) {
				throw new IllegalStateException("Cannot find JDBC URL");
			}
			try {
				conn = DriverManager.getConnection(jdbcurl);
			} catch (SQLException e) {
				throw new IllegalStateException("Cannot connect to database ", e);
			}
			Enumeration<?> keys = properties.propertyNames();
			String key;
			while (keys.hasMoreElements()) {
				key = keys.nextElement().toString();
				if (!"jdbcurl".equals(key)) {
					try {
						Class<? extends Persistable> persistable = (Class<? extends Persistable>) Class.forName(key);
						Class<? extends GenericTDG> tdg = (Class<? extends GenericTDG>) Class
								.forName(properties.getProperty(key));
						register(persistable, tdg);
					} catch (IllegalArgumentException | SecurityException | ClassNotFoundException e) {
						// fail silently
						LOGGER.warning(e.getMessage());
					}
				}
			}
		} catch (IOException e1) {
			throw new IllegalStateException("Cannot access file registry.properties", e1);
		}
	}

	public static Connection getConnection() {
		try {
			if (conn.isClosed()) {
				conn = DriverManager.getConnection(jdbcurl);
			}
		} catch (SQLException e) {
			LOGGER.warning(e.getMessage());
		}
		return conn;
	}

	public static void init() {
		REGISTRY.clear();
		try {
			conn = DriverManager.getConnection(jdbcurl);
		} catch (SQLException e) {
			throw new IllegalStateException("Cannot connect to database ", e);
		}
	}

	public static <U extends Persistable, T extends GenericTDG<U>> void register(Class<U> persistable, Class<T> tdg) {
		try {
			REGISTRY.put(persistable, tdg.newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
			// fail silently
			LOGGER.warning(e.getMessage());
		}
	}

	public static <U extends Persistable, T extends GenericTDG<U>> T findTDG(Class<U> clazz) {
		return (T) REGISTRY.get(clazz);
	}
}
