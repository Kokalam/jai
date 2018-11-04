package ili.jai.lenscritique.data;

import ili.jai.tdg.api.Persistable;

/**
 * A tag allows to label the content of an article.
 * 
 * Tags are comparable, i.e. they can be sorted in lexicographic order.
 * 
 * @author leberre
 *
 */
public class Tag implements Persistable, Comparable<Tag> {
	private long id;
	private String label;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public int compareTo(Tag o) {
		return label.compareTo(o.label);
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", label=" + label + "]";
	}
}
