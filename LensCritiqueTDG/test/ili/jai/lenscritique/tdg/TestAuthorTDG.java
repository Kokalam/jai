package ili.jai.lenscritique.tdg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import ili.jai.lenscritique.data.Author;
import ili.jai.tdg.api.TDGRegistry;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAuthorTDG {

	private AuthorTDG atdg;

	@BeforeClass
	public static void createTable() throws SQLException {
		TDGRegistry.findTDG(Author.class).createTable();
	}

	@AfterClass
	public static void deleteTable() throws SQLException {
		TDGRegistry.findTDG(Author.class).deleteTable();
	}

	@Before
	public void setUp() {
		atdg = TDGRegistry.findTDG(Author.class);
	}

	@Test
	public void test1Emptybase() throws SQLException {
		atdg = TDGRegistry.findTDG(Author.class);
		assertNull(atdg.findById(10));
	}

	@Test
	public void test2InsertingElement() throws SQLException {
		Author t = new Author();
		t.setPseudo("Gile");
		t.setPassword("azerty");
		assertEquals(0, t.getId());
		atdg.insert(t);
		assertNotEquals(0, t.getId());
		Author t2 = atdg.findById(t.getId());
		assertEquals(t, t2);
		assertSame(t, t2);
	}

	@Test
	public void test3UpdatingElement() throws SQLException {
		Author t = atdg.findById(1);
		assertEquals("Gile", t.getPseudo());
		t.setPseudo("Jean");
		atdg.update(t);
		Author t2 = atdg.findById(1);
		assertSame(t, t2);
		assertEquals("Jean", t2.getPseudo());
	}

	@Test
	public void test4DeletingElement() throws SQLException {
		Author t = atdg.findById(1);
		assertEquals("Jean", t.getPseudo());
		atdg.delete(t);
		assertNull(atdg.findById(1));
	}

	@Test
	public void test5RefreshElement() throws SQLException {
		Author t = new Author();
		t.setPseudo("Gile");
		t.setPassword("azerty");
		assertEquals(0, t.getId());
		atdg.insert(t);
		t.setPseudo("Jean");
		assertEquals("Jean", t.getPseudo());
		atdg.refresh(t);
		assertEquals("Gile", t.getPseudo());
	}
}
