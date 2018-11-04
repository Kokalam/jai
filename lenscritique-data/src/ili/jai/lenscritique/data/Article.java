package ili.jai.lenscritique.data;

import java.awt.Image;
import java.time.LocalDate;
import java.util.List;

import ili.jai.tdg.api.Persistable;

/**
 * An article presenting a product, a news, etc. That article is written by a
 * specific author, and can receive comments.
 * 
 * @author leberre
 *
 */
public class Article implements Persistable {

	private long id;
	private String title;
	private String content;
	private Author author;
	private LocalDate date;
	private Image illustration;
	private List<Tag> tags;
	private List<Comment> comments;

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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Image getIllustration() {
		return illustration;
	}

	public void setIllustration(Image illustration) {
		this.illustration = illustration;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> libelles) {
		this.tags = libelles;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Article [id=" + id + ", title=" + title + ", content=" + content + ", author=" + author + ", date="
				+ date + ", illustration=" + illustration + ", tags=" + tags + ", comments=" + comments + "]";
	}
}
