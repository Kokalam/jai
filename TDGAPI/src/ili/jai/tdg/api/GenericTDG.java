package ili.jai.tdg.api;

import java.sql.SQLException;
import java.util.List;

/**
 * Generic interface to implement the Table Data Gateway for a given class.
 * 
 * @author leberre
 *
 * @param <T> The class to be persisted in the SGBD. That class must implement
 *        the {@link Persistable} interface.
 */
public interface GenericTDG<T extends Persistable> {

	/**
	 * Create the table(s) necessary for that persistable object.
	 * 
	 * @throws SQLException if something goes wrong.
	 */
	void createTable() throws SQLException;

	/**
	 * Delete the table(s) necessary for that persistable object.
	 * 
	 * @throws SQLException if something goes wrong.
	 */
	void deleteTable() throws SQLException;

	/**
	 * Find an object stored in the database from its primary key.
	 * 
	 * @param id the primary key.
	 * @return an object reflecting the information found in the database or null if
	 *         no record with that primary key is found.
	 * @throws SQLException if something goes wrong.
	 */
	T findById(long id) throws SQLException;

	/**
	 * Find all objects stored in the database.
	 * 
	 * @return all the objects available in the database.
	 * @throws SQLException if something goes wrong.
	 */
	List<T> findAll() throws SQLException;

	/**
	 * Insert a non persisted object in the database.
	 * 
	 * @param t an object implementing the {@link Persistable} interface.
	 * @return the object with its primary key set.
	 * @throws SQLException if something goes wrong.
	 */
	T insert(T t) throws SQLException;

	/**
	 * Update a persisted object in the database, i.e. reflect the changes in the
	 * object to the database.
	 * 
	 * @param t a persisted object.
	 * @return the same object.
	 * @throws SQLException if something goes wrong.
	 */
	T update(T t) throws SQLException;

	/**
	 * Reset a persisted object to the values found in the database, i.e. remove the
	 * changes made on the object.
	 * 
	 * @param t a persisted object.
	 * @return the same object with the values reflecting the ones in the database.
	 * @throws SQLException if something goes wrong.
	 */
	T refresh(T t) throws SQLException;

	/**
	 * Delete a persisted object from the database.
	 * 
	 * @param t a persisted object
	 * @return the same object
	 * @throws SQLException if something goes wrong.
	 */
	T delete(T t) throws SQLException;

	/**
	 * Makes a query to retrieve a set of objects.
	 * 
	 * @param clauseWhereWithJoker a prepared where clause
	 * @param args                 the parameters to use with the prepared where
	 *                             clause
	 * @return a list of objects
	 * @throws SQLException
	 */
	List<T> selectWhere(String clauseWhereWithJoker, Object... args) throws SQLException;
}
