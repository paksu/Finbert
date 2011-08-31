/**
 * 
 */
package paksu.finbert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import paksu.finbert.SmileySelectionDialog.OnSmileySelectedListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 *
 */
public final class CommentsActivity extends Activity implements OnSmileySelectedListener {
	public static final String EXTRAS_YEAR = "year";
	public static final String EXTRAS_MONTH = "month";
	public static final String EXTRAS_DAY = "day";
	private final CommentHandler commentHandler = CommentHandler.getInstance();
	private DilbertDate date;
	private ListView commentsListView;
	private EditText commentEditText;

	private class CommentsAdapter extends ArrayAdapter<Comment> {
		private final List<Smiley> smileys = Smiley.getAllSupported();

		public CommentsAdapter(Context context, int textViewResourceId, List<Comment> objects) {
			super(context, textViewResourceId, objects);
		}

		private final ImageGetter smileyImageGetter = new ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				Drawable drawable = getResources().getDrawable(Integer.parseInt(source));
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				return drawable;
			}
		};

		private String replaceSmileysWithImageLinks(String source) {
			String edited = source;
			for (Smiley smiley : smileys) {
				edited = edited.replaceAll(Pattern.quote(smiley.getPresentation()),
						"<img src=\"" + smiley.getDrawableId() + "\"/>");
			}
			return edited;
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
			String formatedComment = replaceSmileysWithImageLinks(comment.getComment());
			commentTextView.setText(Html.fromHtml(formatedComment, smileyImageGetter, null));
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

			new GetCommentsForDateTask().execute(date);
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
			for (Comment comment : comments) {
				Log.d("finbert", comment.toString());
			}
			Collections.reverse(comments);
			((EditText) findViewById(R.id.comment_edit_text)).setText("");
			commentsListView.setAdapter(new CommentsAdapter(CommentsActivity.this, 0, comments));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments);

		commentsListView = (ListView) findViewById(R.id.comments_list);
		commentEditText = (EditText) findViewById(R.id.comment_edit_text);

		Bundle extras = getIntent().getExtras();
		int year = extras.getInt(EXTRAS_YEAR);
		int month = extras.getInt(EXTRAS_MONTH);
		int day = extras.getInt(EXTRAS_DAY);
		date = DilbertDate.exactlyForDate(year, month, day);

		setTitle("Finbert - comments - " + date);

		new GetCommentsForDateTask().execute(date);
	}

	public void smileyButtonClicked(View v) {
		SmileySelectionDialog dialog = new SmileySelectionDialog(this);
		dialog.setOnSmileySelectedListener(this);
		dialog.show();
	}

	public void buttonListener(View v) {
		if (v.getId() == R.id.post_comment) {
			if (commentInputIsValid()) {
				postComment(getCommentInput());
			}
		}
	}

	private boolean commentInputIsValid() {
		String commentInput = commentEditText.getText().toString();
		return commentInput.length() > 0;
	}

	private String getCommentInput() {
		return commentEditText.getText().toString();
	}

	private void postComment(String text) {
		String name = "daddari";
		String date = this.date.toString();
		Comment comment = new Comment(text, name, date);
		new PostNewCommentForDateDateTask().execute(comment);
	}

	@Override
	public void onSmileySelected(Smiley selected) {
		StringBuilder smileyAppendingBuilder = new StringBuilder();
		String input = getCommentInput();
		smileyAppendingBuilder.append(input);
		if (!input.endsWith(" ")) {
			smileyAppendingBuilder.append(" ");
		}

		smileyAppendingBuilder.append(selected.getPresentation());
		smileyAppendingBuilder.append(" ");
		String inputWithAppendedSmiley = smileyAppendingBuilder.toString();
		commentEditText.setText(inputWithAppendedSmiley);
		commentEditText.setSelection(inputWithAppendedSmiley.length() - 1);
	}
}
