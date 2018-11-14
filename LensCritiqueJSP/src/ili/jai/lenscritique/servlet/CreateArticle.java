package ili.jai.lenscritique.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ili.jai.lenscritique.data.Article;
import ili.jai.lenscritique.data.Author;
import ili.jai.lenscritique.data.Tag;

/**
 * Servlet implementation class CreateArticle
 */
@WebServlet("/createarticle")
public class CreateArticle extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Article a = new Article();
		a.setAuthor((Author)request.getSession().getAttribute("author"));
		String title = request.getParameter("title");
		a.setTitle(title);
		if(title == null || title.isEmpty()) {
			request.getRequestDispatcher("/article.jsp").forward(request, response);
		}
		a.setContent(request.getParameter("content"));
		Map<Long, Tag> tags = (Map<Long, Tag>) request.getServletContext().getAttribute("tags");
		String[] tag = request.getParameterValues("tags");
		if(tag != null) {
			for(String tagid : request.getParameterValues("tags")) {
				a.getTags().add(tags.get(tagid));
			}
		}
		Map<Long, Article> articles = (Map<Long, Article>) request.getServletContext().getAttribute("articles");
		a.setId(articles.size()+1);
		articles.put(a.getId(), a);
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
