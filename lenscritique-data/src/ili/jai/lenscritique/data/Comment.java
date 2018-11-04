package ili.jai.lenscritique.data;

import java.time.LocalDate;

import ili.jai.tdg.api.Persistable;

/**
 * A comment is a reaction to an article.
 * 
 * A comment does not refer to another comment, for sake of simplicity.
 * 
 * Comments can be ordered naturally by increasing date order.
 * 
 * @author leberre
 *
 */
public class Comment implements Persistable, Comparable<Comment> {
	private long id;
	private String title;
	private String content;
	private Author author;
	private Article article;
	private LocalDate date;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public int compareTo(Comment c) {
		return date.compareTo(c.date);
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", title=" + title + ", content=" + content + ", author=" + author + ", article="
				+ article + ", date=" + date + "]";
	}
}
