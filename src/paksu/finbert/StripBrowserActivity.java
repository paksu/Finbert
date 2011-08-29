package paksu.finbert;

import paksu.finbert.DilbertImageSwitcher.Direction;
import paksu.finbert.DilbertImageSwitcher.OnFlingListener;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.google.gson.JsonParseException;

public final class StripBrowserActivity extends Activity implements ViewFactory {
	private DilbertImageSwitcher imageSwitcher;
	private ImageView nextButton;
	private ImageView prevButton;
	private TextView commentCount;
	private final DilbertReader dilbertReader;
	private Direction nextSlideDirection;
	private DilbertDate date = DilbertDate.newest();
	private boolean isFetchingImage = false;
	private boolean isFetchingCommentCount = false;
	private final CommentHandler commentHandler = new CommentHandler();

	private class BackgroundDownloader extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			isFetchingImage = true;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				dilbertReader.readCurrent();
			} catch (NetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			isFetchingImage = false;
			updateNavigationButtonStates();
			fadeToCurrent();
		}
	}

	private class getCommentCountInBackground extends AsyncTask<Void, Void, Void> {
		private Integer fetchedCommentCount = new Integer(0);

		@Override
		protected void onPreExecute() {
			isFetchingCommentCount = true;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				fetchedCommentCount = commentHandler.getCommentCount(dilbertReader.getCurrentDate());
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NetworkException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			isFetchingCommentCount = false;
			commentCount.setText(fetchedCommentCount.toString());
		}
	}

	private final OnFlingListener imageSwitcherOnFlingListener = new OnFlingListener() {
		@Override
		public void onFling(Direction direction) {
			if (!isFetchingImage) {
				if (direction == Direction.LEFT) {
					changeToNextDayIfAvailable();
				} else if (direction == Direction.RIGHT) {
					changeToPreviousDayIfAvailable();
				}
			}
		}
	};

	public StripBrowserActivity() {
		dilbertReader = DilbertReader.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.strip_browser);

		imageSwitcher = (DilbertImageSwitcher) findViewById(R.id.dilbert_image_switcher);
		imageSwitcher.setFactory(this);
		imageSwitcher.setOnFlingListener(imageSwitcherOnFlingListener);

		nextButton = (ImageView) findViewById(R.id.next);
		prevButton = (ImageView) findViewById(R.id.previous);

		commentCount = (TextView) findViewById(R.id.comments_count);

		setFonts();
		fadeToTemporary();
		downloadAndFadeToCurrent();
		fetchCommentCount();
	}

	private void setFonts() {
		Typeface customTypeFace = Typeface.createFromAsset(getAssets(), "default_font.ttf");
		((TextView) findViewById(R.id.share_text)).setTypeface(customTypeFace);
		((TextView) findViewById(R.id.click_to_comment)).setTypeface(customTypeFace);
		((TextView) findViewById(R.id.comments)).setTypeface(customTypeFace);
		((TextView) findViewById(R.id.comments_count)).setTypeface(customTypeFace);
	}

	public void buttonListener(View v) {
		if (v.getId() == R.id.comments_bubble) {
			launchCommentsActivityForCurrentDate();
			return;
		}

		if (isFetchingImage) {
			return;
		}

		if (v.getId() == R.id.next) {
			changeToNextDayIfAvailable();
		} else if (v.getId() == R.id.previous) {
			changeToPreviousDayIfAvailable();
		}
	}

	private void changeToNextDayIfAvailable() {
		if (dilbertReader.isNextAvailable()) {
			date = date.next();
			dilbertReader.nextDay();
			nextSlideDirection = Direction.RIGHT;
			fetchNewFinbert();
			fetchCommentCount();
		}
	}

	private void fetchCommentCount() {
		new getCommentCountInBackground().execute();
	}

	private void changeToPreviousDayIfAvailable() {
		if (dilbertReader.isPreviousAvailable()) {
			date = date.previous();
			dilbertReader.previousDay();
			nextSlideDirection = Direction.LEFT;
			fetchNewFinbert();
			fetchCommentCount();
		}
	}

	private void updateNavigationButtonStates() {
		nextButton.setEnabled(dilbertReader.isNextAvailable());
		prevButton.setEnabled(dilbertReader.isPreviousAvailable());
	}

	private void updateTitle() {
		setTitle("Finbert - " + dilbertReader.getCurrentDate());
	}

	private void fetchNewFinbert() {
		if (dilbertReader.hasCurrentCached()) {
			updateTitle();
			slideToCurrent();
			updateNavigationButtonStates();
		} else {
			updateTitle();
			slideToTemporary();
			downloadAndFadeToCurrent();
		}
	}

	private void downloadAndFadeToCurrent() {
		new BackgroundDownloader().execute();
	}

	private void slideToCurrent() {
		imageSwitcher.slideToDrawable(currentDilbertDrawable(), ScaleType.FIT_CENTER, nextSlideDirection);
	}

	private void fadeToCurrent() {
		imageSwitcher.fadeToDrawable(currentDilbertDrawable(), ScaleType.FIT_CENTER);
	}

	private void fadeToTemporary() {
		imageSwitcher.fadeToDrawable(temporaryDrawable(), ScaleType.CENTER_INSIDE);
	}

	private void slideToTemporary() {
		imageSwitcher.slideToDrawable(temporaryDrawable(), ScaleType.CENTER_INSIDE, nextSlideDirection);
	}

	private Drawable currentDilbertDrawable() {
		try {
			return new BitmapDrawable(dilbertReader.readCurrent());
		} catch (NetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new BitmapDrawable();
		}
	}

	private Drawable temporaryDrawable() {
		return getResources().getDrawable(R.drawable.loading_face);
	}

	private void launchCommentsActivityForCurrentDate() {
		Intent intent = new Intent(this, CommentsActivity.class);
		intent.putExtra(CommentsActivity.EXTRAS_YEAR, date.getYear());
		intent.putExtra(CommentsActivity.EXTRAS_MONTH, date.getMonth());
		intent.putExtra(CommentsActivity.EXTRAS_DAY, date.getDay());
		startActivity(intent);
	}

	/**
	 * Generates views for {@link DilbertImageSwitcher}
	 */
	@Override
	public View makeView() {
		ImageView view = new ImageView(this);
		view.setScaleType(ScaleType.FIT_CENTER);
		view.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return view;
	}
}