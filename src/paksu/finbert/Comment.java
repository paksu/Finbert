package paksu.finbert;

public class Comment {
    private final String comment;
    private final String name;
    private final String date; // TODO => DateTime ?

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

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}
