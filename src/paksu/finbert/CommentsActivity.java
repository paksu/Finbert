/**
 * 
 */
package paksu.finbert;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 *
 */
public final class CommentsActivity extends Activity {
	public static final String EXTRAS_DATE = "date";

	private class CommentsAdapter extends ArrayAdapter<Comment> {

		public CommentsAdapter(Context context, int textViewResourceId, List<Comment> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d("finbert", "fetching comment for pos " + position);
			Comment comment = getItem(position);
			View commentItem;
			commentItem = LayoutInflater.from(getContext()).inflate(R.layout.comment, null);
			TextView commenterTextView = (TextView) commentItem.findViewById(R.id.commenter);
			commenterTextView.setText(comment.getName());
			TextView commentTextView = (TextView) commentItem.findViewById(R.id.comment);
			commentTextView.setText(comment.getComment());
			return commentItem;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments);

		ListView commentsListView = (ListView) findViewById(R.id.comments_list);
		List<Comment> testComments = new ArrayList<Comment>();
		testComments.add(new Comment("paska", "daddari", null));
		testComments.add(new Comment("paska", "daddari", null));
		testComments.add(new Comment("paska", "daddari", null));
		testComments.add(new Comment("paska", "daddari", null));
		testComments.add(new Comment("paska", "daddari", null));
		testComments.add(new Comment("paska", "daddari", null));
		testComments.add(new Comment("paska", "daddari", null));
		testComments.add(new Comment("paska", "daddari", null));
		commentsListView.setAdapter(new CommentsAdapter(getBaseContext(), 0, testComments));

		String date = getIntent().getExtras().getString(EXTRAS_DATE);
		setTitle("Finbert - comments - " + date);
	}
}
