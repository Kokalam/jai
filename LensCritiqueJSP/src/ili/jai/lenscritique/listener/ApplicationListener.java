package ili.jai.lenscritique.listener;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import ili.jai.lenscritique.data.Article;
import ili.jai.lenscritique.data.Author;
import ili.jai.lenscritique.data.Tag;

/**
 * Application Lifecycle Listener implementation class ApplicationListener
 *
 */
@WebListener
public class ApplicationListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public ApplicationListener() {
        // TODO Auto-generated constructor stub
    }
    
    public void contextDestroyed(ServletContextEvent sce) {
    	// TODO
    }
	
    public void contextInitialized(ServletContextEvent sce) {
    	Map<Long, Tag> tags = new HashMap<>();
    	ResourceBundle rb = ResourceBundle.getBundle("tags");
    	Enumeration<String> e = rb.getKeys();
    	Tag tag;
    	String key;
    	while(e.hasMoreElements()) {
    		key = e.nextElement();
    		tag = new Tag();
    		tag.setLabel(rb.getString(key));
    		tag.setId(Integer.parseInt(key));
    		tags.put(tag.getId(), tag);
    	}
    	sce.getServletContext().setAttribute("tags", tags);
    	Author a1 = new Author();
    	a1.setId(1L);
    	a1.setPseudo("Rosso");
    	a1.setPassword("XXXXXXX");
    	Author a2 = new Author();
    	a2.setId(2L);
    	a2.setPseudo("Berro");
    	a2.setPassword("XXXXXXXXXXXX");
    	Author a3 = new Author();
    	a3.setId(3L);
    	a3.setPseudo("Hugo");
    	a3.setPassword("XXXXXXX");
    	Author [] as = {a1,a2,a3};
    	Map<Long, Author> authors = new HashMap<>();
    	for(Author a: as) {
    		authors.put(a.getId(), a);
    	}
    	sce.getServletContext().setAttribute("authors", authors);
    	sce.getServletContext().setAttribute("articles", new HashMap<Long, Article>());
    }
}
