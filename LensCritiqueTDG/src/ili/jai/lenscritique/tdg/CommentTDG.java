package ili.jai.lenscritique.tdg;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ili.jai.lenscritique.data.Article;
import ili.jai.lenscritique.data.Author;
import ili.jai.lenscritique.data.Comment;
import ili.jai.tdg.api.AbstractTDG;
import ili.jai.tdg.api.TDGRegistry;

public class CommentTDG extends AbstractTDG<Comment> {
	private static final String CREATE = "CREATE TABLE Comment (ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, TITLE VARCHAR(100) NOT NULL, CONTENT VARCHAR(1000) NOT NULL, DATE DATE NOT NULL, ARTICLE_ID BIGINT REFERENCES Article(ID), AUTHOR_ID BIGINT REFERENCES Author(ID))";
	private static final String DROP = "DROP TABLE Comment";

	private static final String INSERT = "INSERT INTO Comment (TITLE, CONTENT, AUTHOR_ID, ARTICLE_ID, DATE) VALUES(?,?,?,?,?)";
	private static final String UPDATE = "UPDATE Comment t SET t.TITLE = ?, CONTENT = ?, AUTHOR_ID = ?, ARTICLE_ID = ?, DATE = ? WHERE t.ID = ?";
	private static final String FIND_BY_ID = "SELECT ID, TITLE, CONTENT, AUTHOR_ID, ARTICLE_ID, DATE FROM Comment t WHERE t.ID = ?";
	private static final String FIND_ALL_IDS = "SELECT ID FROM Comment t";
	private static final String DELETE = "DELETE FROM Comment t WHERE t.ID = ?";
	private static final String WHERE = "SELECT ID FROM Comment t WHERE ";

	@Override
	public void createTable() throws SQLException {
		try (final Statement pst = TDGRegistry.getConnection().createStatement()) {
			pst.executeUpdate(CREATE);
		}
	}

	@Override
	public void deleteTable() throws SQLException {
		try (final Statement pst = TDGRegistry.getConnection().createStatement()) {
			pst.executeUpdate(DROP);
		}
	}

	@Override
	public List<Comment> selectWhere(final String clauseWhereWithJoker, final Object... args) throws SQLException {
		final List<Comment> result = new ArrayList<>();
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(WHERE + clauseWhereWithJoker)) {
			int index = 1;
			for (Object arg : args) {
				pst.setObject(index++, arg);
			}
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					result.add(findById(rs.getLong(1)));
				}
			}
		}
		return result;
	}

	@Override
	protected Comment retrieveFromDB(long id) throws SQLException {
		Comment t = null;
		try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(FIND_BY_ID)) {
			t = new Comment();
			try (ResultSet res = stm.executeQuery()) {
				if (res.next()) {
					t.setId(res.getLong(1));
					t.setTitle(res.getString(2));
					t.setContent(res.getString(3));
					t.setAuthor(TDGRegistry.findTDG(Author.class).findById(res.getLong(4)));
					t.setArticle(TDGRegistry.findTDG(Article.class).findById(res.getLong(5)));
					t.setDate(res.getDate(6).toLocalDate());
				}
			}
		}
		return t;
	}

	@Override
	protected Comment insertIntoDB(Comment t) throws SQLException {
		try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(INSERT)) {
			stm.setString(1, t.getTitle());
			stm.setString(2, t.getContent());
			stm.setLong(3, t.getAuthor().getId());
			stm.setLong(4, t.getArticle().getId());
			stm.setDate(5, Date.valueOf(t.getDate()));

			int res = stm.executeUpdate();
			assert res == 1;
			try (ResultSet keys = stm.getGeneratedKeys()) {
				if (keys.next()) {
					t.setId(keys.getLong(1));
				}
			}
			return t;
		}
	}

	@Override
	protected Comment updateIntoDB(Comment t) throws SQLException {
		try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(UPDATE)) {
			assert findById(t.getId()) == t;
			stm.setString(1, t.getTitle());
			stm.setString(2, t.getContent());
			stm.setLong(3, t.getAuthor().getId());
			stm.setLong(4, t.getArticle().getId());
			stm.setDate(5, Date.valueOf(t.getDate()));

			stm.setLong(6, t.getId());
			int res = stm.executeUpdate();
			assert res == 1;
			return t;
		}
	}

	@Override
	protected Comment deleteFromDB(Comment t) throws SQLException {
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(DELETE)) {
			assert findById(t.getId()) == t;
			pst.setLong(1, t.getId());
			int result = pst.executeUpdate();
			assert result == 1;
			return t;
		}
	}

	@Override
	protected Comment refreshFromDB(Comment t) throws SQLException {
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(FIND_BY_ID)) {
			pst.setLong(1, t.getId());
			try (final ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					t.setId(rs.getLong(1));
					t.setTitle(rs.getString(2));
					t.setContent(rs.getString(3));
					t.setAuthor(TDGRegistry.findTDG(Author.class).findById(rs.getLong(4)));
					t.setArticle(TDGRegistry.findTDG(Article.class).findById(rs.getLong(5)));
					t.setDate(rs.getDate(6).toLocalDate());

				}
			}
		}
		return t;
	}

	@Override
	protected List<Long> findAllIds() throws SQLException {
		final List<Long> ids = new ArrayList<>();
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(FIND_ALL_IDS)) {
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					ids.add(rs.getLong(1));
				}
			}
		}
		return ids;
	}
}
