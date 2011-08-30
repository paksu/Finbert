/**
 * 
 */
package paksu.finbert;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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
	public static final String EXTRAS_YEAR = "year";
	public static final String EXTRAS_MONTH = "month";
	public static final String EXTRAS_DAY = "day";
	private final CommentHandler commentHandler = CommentHandler.getInstance();
	private ListView commentsListView;

	private static class CommentsAdapter extends ArrayAdapter<Comment> {

		public CommentsAdapter(Context context, int textViewResourceId, List<Comment> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Comment comment = getItem(position);
			View commentItem = LayoutInflater.from(getContext()).inflate(R.layout.comment, null);

			View commentLayout = commentItem.findViewById(R.id.comment_layout);
			if (position % 2 == 0) {
				commentLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bubble_right));
			} else {
				commentLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bubble_left));
			}

			TextView commenterTextView = (TextView) commentItem.findViewById(R.id.commenter);
			commenterTextView.setText(comment.getName());

			TextView commentTextView = (TextView) commentItem.findViewById(R.id.comment);
			commentTextView.setText(comment.getComment());
			return commentItem;
		}

	}

	private class PostNewCommentForDateDateTask extends AsyncTask<Comment, Void, Boolean> {
		private Comment comment;

		@Override
		protected Boolean doInBackground(Comment... params) {
			comment = params[0];
			try {
				return commentHandler.setComment(comment);
			} catch (NetworkException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean wasInsertSuccessful) {
			// TODO: messua käyttäjälle ?
			if (wasInsertSuccessful) {
				Log.d("finbert", "Great success");
			} else {
				Log.d("finbert", "Epic fail");
			}
		}
	}

	private class GetCommentsForDateTask extends AsyncTask<DilbertDate, Void, List<Comment>> {
		private DilbertDate date;

		@Override
		protected List<Comment> doInBackground(DilbertDate... params) {
			List<Comment> comments = new ArrayList<Comment>();
			date = params[0];
			try {
				comments = commentHandler.getComments(date);
			} catch (NetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return comments;
		}

		@Override
		protected void onPostExecute(List<Comment> comments) {
			commentsListView.setAdapter(new CommentsAdapter(getBaseContext(), 0, comments));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments);

		commentsListView = (ListView) findViewById(R.id.comments_list);
		/*
		 * List<Comment> testComments = new ArrayList<Comment>();
		 * testComments.add(new Comment("paska", "daddari", null));
		 * testComments.add(new Comment("paska", "daddari", null));
		 * testComments.add(new Comment("paska", "daddari", null));
		 * testComments.add(new Comment("paska", "daddari", null));
		 * testComments.add(new Comment("paska", "daddari", null));
		 * testComments.add(new Comment("paska", "daddari", null));
		 * testComments.add(new Comment("paska", "daddari", null));
		 * testComments.add(new Comment("paska", "daddari", null));
		 * commentsListView.setAdapter(new CommentsAdapter(getBaseContext(), 0,
		 * testComments));
		 */

		Bundle extras = getIntent().getExtras();
		int year = extras.getInt(EXTRAS_YEAR);
		int month = extras.getInt(EXTRAS_MONTH);
		int day = extras.getInt(EXTRAS_DAY);
		DilbertDate date = DilbertDate.exactlyForDate(year, month, day);
		setTitle("Finbert - comments - " + date);
		new GetCommentsForDateTask().execute(date);
	}
}
