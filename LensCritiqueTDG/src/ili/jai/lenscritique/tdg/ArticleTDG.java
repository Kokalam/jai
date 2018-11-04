package ili.jai.lenscritique.tdg;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import ili.jai.lenscritique.data.Article;
import ili.jai.lenscritique.data.Author;
import ili.jai.lenscritique.data.Comment;
import ili.jai.lenscritique.data.Tag;
import ili.jai.tdg.api.AbstractTDG;
import ili.jai.tdg.api.TDGRegistry;

public class ArticleTDG extends AbstractTDG<Article> {

	private static final Logger LOGGER = Logger.getLogger(ArticleTDG.class.getName());

	private static final String CREATE = "CREATE TABLE Article (ID BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY, TITLE VARCHAR(100) NOT NULL, CONTENT VARCHAR(10000) NOT NULL, DATE DATE NOT NULL, ILLUSTRATION BLOB NOT NULL, AUTHOR_ID BIGINT REFERENCES Author(ID))";
	private static final String CREATE_LINK_TAG = "CREATE TABLE LinkTagArticle (ARTICLE_ID BIGINT NOT NULL REFERENCES Article(ID), TAG_ID BIGINT NOT NULL REFERENCES Tag(ID))";
	private static final String DROP = "DROP TABLE Article";
	private static final String DROP_LINK_TAG = "DROP TABLE LinkTagArticle";

	private static final String INSERT = "INSERT INTO Article (TITLE, CONTENT, AUTHOR_ID, DATE, ILLUSTRATION) VALUES(?,?,?,?,?)";
	private static final String UPDATE = "UPDATE Article t SET t.TITLE = ?, CONTENT = ?, AUTHOR_ID = ?, DATE = ?, ILLUSTRATION = ? WHERE t.ID = ?";
	private static final String FIND_BY_ID = "SELECT ID, TITLE, CONTENT, AUTHOR_ID, DATE, ILLUSTRATION FROM Article t WHERE t.ID = ?";
	private static final String FIND_ALL_IDS = "SELECT ID FROM Article t";
	private static final String DELETE = "DELETE FROM Article t WHERE t.ID = ?";
	private static final String WHERE = "SELECT ID FROM Article t WHERE ";

	private static final String INSERT_LINK = "INSERT INTO LinkTagArticle (ARTICLE_ID, TAG_ID) VALUES(?, ?)";
	private static final String FIND_TAG_IDS = "SELECT TAG_ID FROM LinkTagArticle WHERE ARTICLE_ID = ?";

	@Override
	public void createTable() throws SQLException {
		try (final Statement pst = TDGRegistry.getConnection().createStatement()) {
			pst.executeUpdate(CREATE);
		}
		try (final Statement pst = TDGRegistry.getConnection().createStatement()) {
			pst.executeUpdate(CREATE_LINK_TAG);
		}
	}

	@Override
	public void deleteTable() throws SQLException {
		try (final Statement pst = TDGRegistry.getConnection().createStatement()) {
			pst.executeUpdate(DROP);
		}
		try (final Statement pst = TDGRegistry.getConnection().createStatement()) {
			pst.executeUpdate(DROP_LINK_TAG);
		}
	}

	@Override
	public List<Article> selectWhere(final String clauseWhereWithJoker, final Object... args) throws SQLException {
		final List<Article> result = new ArrayList<>();
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
	protected Article retrieveFromDB(long id) throws SQLException {
		Article t = null;
		try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(FIND_BY_ID)) {
			t = new Article();
			try (ResultSet res = stm.executeQuery()) {
				if (res.next()) {
					t.setId(res.getLong(1));
					t.setTitle(res.getString(2));
					t.setContent(res.getString(3));
					t.setAuthor(TDGRegistry.findTDG(Author.class).findById(res.getLong(4)));
					t.setDate(res.getDate(5).toLocalDate());
					Blob blobImage = res.getBlob(6);
					try {
						t.setIllustration(ImageIO.read(blobImage.getBinaryStream()));
					} catch (IOException e) {
						LOGGER.severe(e.getMessage());
					}
					t.setComments(TDGRegistry.findTDG(Comment.class).selectWhere("ARTICLE_ID = ?", t.getId()));
					try (PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(FIND_TAG_IDS)) {
						pst.setLong(1, t.getId());
						List<Tag> tags = new ArrayList<>();
						try (ResultSet rs = pst.executeQuery()) {
							while (rs.next())
								tags.add(TDGRegistry.findTDG(Tag.class).findById(rs.getLong(1)));
						}
						t.setTags(tags);
					}
				}
			}
		}
		return t;
	}

	@Override
	protected Article insertIntoDB(Article t) throws SQLException {
		try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(INSERT)) {
			stm.setString(1, t.getTitle());
			stm.setString(2, t.getContent());
			stm.setLong(3, t.getAuthor().getId());
			stm.setDate(4, Date.valueOf(t.getDate()));

			BufferedImage bImage = new BufferedImage(t.getIllustration().getWidth(null),
					t.getIllustration().getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D gbImage = bImage.createGraphics();
			gbImage.drawImage(t.getIllustration(), null, null);
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				ImageIO.write((RenderedImage) bImage, "png", baos);
			} catch (IOException e) {

			} finally {
				try {
					baos.close();
				} catch (Exception e) {
				}
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			stm.setBlob(5, bais);
			int res = stm.executeUpdate();
			assert res == 1;
			try (ResultSet keys = stm.getGeneratedKeys()) {
				if (keys.next()) {
					t.setId(keys.getLong(1));
				}
			}
			for (Comment comment : t.getComments()) {
				if (comment.getId() == 0)
					TDGRegistry.findTDG(Comment.class).insert(comment);

			}
			for (Tag tag : t.getTags()) {
				if (tag.getId() == 0)
					TDGRegistry.findTDG(Tag.class).insert(tag);
				try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(INSERT_LINK)) {
					pst.setLong(1, t.getId());
					pst.setLong(2, tag.getId());
					pst.executeUpdate();
				}
			}
			return t;
		}
	}

	@Override
	protected Article updateIntoDB(Article t) throws SQLException {
		try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(UPDATE)) {
			assert findById(t.getId()) == t;
			stm.setString(1, t.getTitle());
			stm.setString(2, t.getContent());
			stm.setLong(3, t.getAuthor().getId());
			stm.setDate(4, Date.valueOf(t.getDate()));

			BufferedImage bImage = new BufferedImage(t.getIllustration().getWidth(null),
					t.getIllustration().getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D gbImage = bImage.createGraphics();
			gbImage.drawImage(t.getIllustration(), null, null);
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				ImageIO.write((RenderedImage) bImage, "png", baos);
			} catch (IOException e) {

			} finally {
				try {
					baos.close();
				} catch (Exception e) {
				}
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			stm.setBlob(5, bais);
			stm.setLong(6, t.getId());

			int res = stm.executeUpdate();
			assert res == 1;
			for (Comment comment : t.getComments()) {
				if (comment.getId() == 0)
					TDGRegistry.findTDG(Comment.class).insert(comment);
				else
					TDGRegistry.findTDG(Comment.class).update(comment);

			}
			for (Tag tag : t.getTags()) {
				if (tag.getId() == 0)
					TDGRegistry.findTDG(Tag.class).insert(tag);
				else
					TDGRegistry.findTDG(Tag.class).update(tag);
				try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(INSERT_LINK)) {
					pst.setLong(1, t.getId());
					pst.setLong(2, tag.getId());
					pst.executeUpdate();
				}
			}
			return t;
		}
	}

	@Override
	protected Article deleteFromDB(Article t) throws SQLException {
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(DELETE)) {
			assert findById(t.getId()) == t;
			pst.setLong(1, t.getId());
			int result = pst.executeUpdate();
			assert result == 1;
			for (Comment comment : t.getComments()) {
				TDGRegistry.findTDG(Comment.class).delete(comment);
			}
			return t;
		}
	}

	@Override
	protected Article refreshFromDB(Article t) throws SQLException {
		try (final PreparedStatement pst = TDGRegistry.getConnection().prepareStatement(FIND_BY_ID)) {
			pst.setLong(1, t.getId());
			try (final ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					t.setId(rs.getLong(1));
					t.setTitle(rs.getString(2));
					t.setContent(rs.getString(3));
					t.setAuthor(TDGRegistry.findTDG(Author.class).findById(rs.getLong(4)));
					t.setDate(rs.getDate(5).toLocalDate());
					Blob blobImage = rs.getBlob(6);
					try {
						t.setIllustration(ImageIO.read(blobImage.getBinaryStream()));
					} catch (IOException e) {
						LOGGER.severe(e.getMessage());
					}
					t.setComments(TDGRegistry.findTDG(Comment.class).selectWhere("ARTICLE_ID = ?", t.getId()));
					try (PreparedStatement stm = TDGRegistry.getConnection().prepareStatement(FIND_TAG_IDS)) {
						stm.setLong(1, t.getId());
						List<Tag> tags = new ArrayList<>();
						try (ResultSet res = stm.executeQuery()) {
							while (res.next())
								tags.add(TDGRegistry.findTDG(Tag.class).findById(res.getLong(1)));
						}
						t.setTags(tags);
					}
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
