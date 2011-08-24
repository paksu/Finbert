package paksu.finbert;

public class Comment {
	private String comment;
	private String name;
	private String date; // TODO => DateTime ?

	public Comment(String comment, String name, String date) {
		this.comment = comment;
		this.name = name;
		this.date = date;
	}

	@Override
	public String toString() {
		return "Comment [comment=" + comment + ", name=" + name + ", date=" + date + "]";
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
