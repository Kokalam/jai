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

import ili.jai.lenscritique.data.Tag;
import ili.jai.tdg.api.TDGRegistry;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTagTDG {

	private TagTDG ttdg;
	
	@BeforeClass
	public static void createTable() throws SQLException {
		TDGRegistry.findTDG(Tag.class).createTable();
	}
	
	@AfterClass
	public static void deleteTable() throws SQLException {
		TDGRegistry.findTDG(Tag.class).deleteTable();
	}
	
	@Before
	public void setUp() {
		ttdg = TDGRegistry.findTDG(Tag.class);
	}
	
	@Test
	public void test1Emptybase() throws SQLException {
		assertNull(ttdg.findById(10));
	}
	
	@Ignore
	public void test2InsertingElement() throws SQLException {
		Tag t = new Tag();
		t.setLabel("label");
		assertEquals(0, t.getId());
		ttdg.insert(t);
		assertNotEquals(0, t.getId());
		Tag t2 = ttdg.findById(t.getId());
		assertEquals(t, t2);
		assertSame(t, t2);
	}
	
	@Ignore
	public void test3UpdatingElement() throws SQLException {
		Tag t = ttdg.findById(1);
		assertEquals("label", t.getLabel());
		t.setLabel("label5");
		ttdg.update(t);
		Tag t2 = ttdg.findById(1);
		assertSame(t, t2);
		assertEquals("label5", t2.getLabel());
	}
	
	@Ignore
	public void test4DeletingElement() throws SQLException {
		Tag t = ttdg.findById(1);
		assertEquals("label5", t.getLabel());
		ttdg.delete(t);
		assertNull(ttdg.findById(1));
	}
	
	@Ignore
	public void test5RefreshElement() throws SQLException {
		Tag t = new Tag();
		t.setLabel("label");
		assertEquals(0, t.getId());
		ttdg.insert(t);
		t.setLabel("label5");
		assertEquals("label5", t.getLabel());
		ttdg.refresh(t);
		assertEquals("label", t.getLabel());
	}
}
