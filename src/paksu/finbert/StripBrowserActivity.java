package paksu.finbert;

import paksu.finbert.DilbertImageSwitcher.Direction;
import paksu.finbert.DilbertImageSwitcher.OnFlingListener;
import android.app.Activity;
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

public final class StripBrowserActivity extends Activity implements ViewFactory {
	private DilbertImageSwitcher imageSwitcher;
	private ImageView nextButton;
	private ImageView prevButton;
	private final DilbertReader dilbertReader;
	private Direction nextSlideDirection;
	private boolean isFetchingImage = false;

	private class BackgroundDownloader extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			isFetchingImage = true;
		}

		@Override
		protected Void doInBackground(Void... params) {
			dilbertReader.readCurrent();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			isFetchingImage = false;
			fadeToCurrent();
		}
	}

	private final OnFlingListener imageSwitcherOnFlingListener = new OnFlingListener() {
		@Override
		public void onFling(Direction direction) {
			if (!isFetchingImage) {
				if (direction == Direction.LEFT) {
					if (dilbertReader.isNextAvailable()) {
						changeToNextDay();
					}
				} else if (direction == Direction.RIGHT) {
					if (dilbertReader.isPreviousAvailable()) {
						changeToPreviousDay();
					}
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

		setFonts();
		fetchNewFinbert();
	}

	private void setFonts() {
		Typeface customTypeFace = Typeface.createFromAsset(getAssets(), "default_font.ttf");
		((TextView) findViewById(R.id.share_text)).setTypeface(customTypeFace);
	}

	public void buttonListener(View v) {
		if (isFetchingImage) {
			return;
		}

		if (v == findViewById(R.id.next)) {
			if (dilbertReader.isNextAvailable()) {
				changeToNextDay();
			}
		} else if (v == findViewById(R.id.previous)) {
			if (dilbertReader.isPreviousAvailable()) {
				changeToPreviousDay();
			}
		}
	}

	private void changeToNextDay() {
		dilbertReader.nextDay();
		nextSlideDirection = Direction.RIGHT;
		updateGUI();
		fetchNewFinbert();
	}

	private void changeToPreviousDay() {
		dilbertReader.previousDay();
		nextSlideDirection = Direction.LEFT;
		updateGUI();
		fetchNewFinbert();
	}

	private void updateGUI() {
		updateNavigationButtonStates();
		updateTitle();
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
			slideToCurrent();
		} else {
			slideToTemporary();
			new BackgroundDownloader().execute();
		}
	}

	private void slideToCurrent() {
		imageSwitcher.slideToDrawable(currentDilbertDrawable(), ScaleType.FIT_CENTER, nextSlideDirection);
	}

	private void fadeToCurrent() {
		imageSwitcher.fadeToDrawable(currentDilbertDrawable(), ScaleType.FIT_CENTER);
	}

	private void slideToTemporary() {
		imageSwitcher.slideToDrawable(temporaryDrawable(), ScaleType.CENTER_INSIDE, nextSlideDirection);
	}

	private Drawable currentDilbertDrawable() {
		return new BitmapDrawable(dilbertReader.readCurrent());
	}

	private Drawable temporaryDrawable() {
		return getResources().getDrawable(R.drawable.loading_face);
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