package ili.jai.tdg.api;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTDG<T extends Persistable> implements GenericTDG<T> {

    private Map<Long, T> identityMap = new HashMap<>();

    @Override
    public final T findById(long id) throws SQLException {
        T cached = (T) getCached(id);
        if (cached != null) {
            assert cached.getId() == id;
            return cached;
        }
        T fromDB = retrieveFromDB(id);
        if (fromDB != null) {
            cache(id, fromDB);
        }
        return fromDB;
    }

    protected abstract T retrieveFromDB(long id) throws SQLException;

    @Override
    public final T insert(T t) throws SQLException {
        if (identityMap.containsValue(t)) {
            throw new IllegalArgumentException("Object already persisted in databse!");
        }
        T fromDB = insertIntoDB(t);
        if (t.getId()==0) {
            throw new IllegalStateException("No generated ID");
        }
        cache(t.getId(), fromDB);
        return fromDB;
    }

    protected abstract T insertIntoDB(T t) throws SQLException;
    
    @Override
    public final T update(T t) throws SQLException {
        if (!identityMap.containsValue(t)) {
            throw new IllegalArgumentException("Object not persisted in databse!");
        }
        return updateIntoDB(t);
    }

    protected abstract T updateIntoDB(T t) throws SQLException;
    
    @Override
    public final T refresh(T t) throws SQLException {
        if (!identityMap.containsValue(t)) {
            throw new IllegalArgumentException("Object not persisted in databse!");
        }
        return refreshFromDB(t);
    }

    protected abstract T refreshFromDB(T t) throws SQLException;
    
    @Override
    public final T delete(T t) throws SQLException {
        if (!identityMap.containsValue(t)) {
            throw new IllegalArgumentException("Object not persisted in databse!");
        }
        T fromDB = deleteFromDB(t);
        identityMap.remove(fromDB.getId());
        return fromDB;
    }

    protected abstract T deleteFromDB(T t) throws SQLException;
    
    protected T getCached(long id) {
        return identityMap.get(id);
    }
    
    protected void cache(long id, T value) {
        identityMap.put(id, value);
    }
    
    /**
     * To clean up the identity map.
     * Use at your own risks (e.g. for unit tests).
     * Once cleaned, the one to one relationship 
     * between an object on the domain side and
     * a row in the relational side is lost.
     */
    public void clearCache() {
    		identityMap.clear();
    }
    
    
    @Override
    public final List<T> findAll() throws SQLException {
    		List<Long> ids = findAllIds();
    		List<T> objects = new ArrayList<>(ids.size());
    		for (Long id : ids) {
    			objects.add(findById(id));
    		}
    		return objects;
    }
    
    protected abstract List<Long> findAllIds() throws SQLException;
    
}
