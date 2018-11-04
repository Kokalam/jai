package ili.jai.tdg.api;

/**
 * Une interface pour marquer les objets
 * persistés en base de données.
 * 
 * @author leberre
 *
 */
public interface Persistable {

	/**
	 * La clé primaire de l'objet.
	 * 
	 * @return un entier positif non null si l'objet est stocké en base de données.
	 */
	long getId();
}
