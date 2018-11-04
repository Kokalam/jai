package ili.jai.lenscritique.tdg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ili.jai.lenscritique.data.Author;
import ili.jai.tdg.api.AbstractTDG;
import ili.jai.tdg.api.TDGRegistry;

public class AuthorTDG extends AbstractTDG<Author>{
	private static final String CREATE = "CREATE TABLE Author (ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, PSEUDO VARCHAR(100) NOT NULL, PASSWORD VARCHAR(100) NOT NULL)";
	private static final String DROP = "DROP TABLE Author";
	
	private static final String INSERT = "INSERT INTO Author (PSEUDO, PASSWORD) VALUES(?, ?)";
	private static final String UPDATE = "UPDATE Author t SET t.PSEUDO = ?, t.PASSWORD = ? WHERE t.ID = ?";
	private static final String FIND_BY_ID = "SELECT ID, PSEUDO, PASSWORD FROM Author t WHERE t.ID = ?";
	private static final String FIND_ALL_IDS = "SELECT ID FROM Author t";
	private static final String DELETE = "DELETE FROM Author t WHERE t.ID = ?";
	private static final String WHERE = "SELECT ID FROM Author t WHERE ";

	@Override
	public void createTable() throws SQLException {
		try (Statement stm = TDGRegistry.getConnection().createStatement()) {
			stm.executeUpdate(CREATE);
		}
	}

	@Override
	public void deleteTable() throws SQLException {
		try (Statement stm = TDGRegistry.getConnection().createStatement()) {
			stm.executeUpdate(DROP);
		}
	}

	@Override
	public List<Author> selectWhere(final String clauseWhereWithJoker, final Object... args) throws SQLException {
		final List<Author> result = new ArrayList<>();
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(WHERE + clauseWhereWithJoker)) {
			int index = 1;
			for(Object arg : args) {
				pst.setObject(index++,  arg);
			}
			try (ResultSet rs = pst.executeQuery()) {
				while(rs.next()) {
					result.add(findById(rs.getLong(1)));
				}
			}
		}
		return result;
	}

	@Override
	protected Author retrieveFromDB(long id) throws SQLException {
		Author t = null;
		try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(FIND_BY_ID)) {
			t = new Author();
			try (ResultSet res = stm.executeQuery()) {
				if(res.next()) {
					t.setId(res.getLong(1));
					t.setPseudo(res.getString(2));
					t.setPassword(res.getString(3));
				}
			}
		}
		return t;
	}

	@Override
	protected Author insertIntoDB(Author t) throws SQLException {
		try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(INSERT)) {
			stm.setString(1, t.getPseudo());
			stm.setString(2, t.getPassword());
			int res = stm.executeUpdate();
			assert res == 1;
			try (ResultSet keys = stm.getGeneratedKeys()) {
				if(keys.next()) {
					t.setId(keys.getLong(1));
				}
			}
			return t;
		}
	}

	@Override
	protected Author updateIntoDB(Author t) throws SQLException {
		try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(UPDATE)) {
			assert findById(t.getId()) == t;
			stm.setString(1, t.getPseudo());
			stm.setString(2, t.getPassword());
			stm.setLong(3, t.getId());
			int res = stm.executeUpdate();
			assert res == 1;
			return t;
		}
	}


	@Override
	protected Author deleteFromDB(Author t) throws SQLException {
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(DELETE)) {
			assert findById(t.getId()) == t;
			pst.setLong(1, t.getId());
			int result = pst.executeUpdate();
			assert result == 1;
			return t;
		}
	}

	@Override
	protected Author refreshFromDB(Author t) throws SQLException {
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(FIND_BY_ID)) {
			pst.setLong(1, t.getId());
			try (final ResultSet rs = pst.executeQuery()) {
				if(rs.next()) {
					t.setId(rs.getLong(1));
					t.setPseudo(rs.getString(2));
					t.setPassword(rs.getString(3));
				}
			}
		}
		return t;
	}

	@Override
	protected List<Long> findAllIds() throws SQLException {
		final List<Long> ids = new ArrayList<>();
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(FIND_ALL_IDS)) {
			try(ResultSet rs = pst.executeQuery()) {
				while(rs.next()) {
					ids.add(rs.getLong(1));
				}
			}
		}
		return ids;
	}
	
}
