package paksu.finbert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.helloandroid.R;

public final class StripBrowserActivity extends Activity implements ViewFactory {
	private enum Direction {
		LEFT, RIGHT
	};

	private ImageSwitcher imageSwitcher;
	private ImageView nextButton;
	private ImageView prevButton;
	private final DilbertReader dilbertReader;

	private class BackgroundDownloader extends AsyncTask<DilbertReader, Void, Bitmap> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(StripBrowserActivity.this, null, "Loading image..");
		}

		@Override
		protected Bitmap doInBackground(DilbertReader... params) {
			Bitmap downloadedImage = params[0].readCurrent();
			return downloadedImage;
		}

		@Override
		protected void onPostExecute(Bitmap downloadedImage) {
			updateNavigationButtonStates();
			updateTitle();
			setFinbertImage(downloadedImage);

			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	public StripBrowserActivity() {
		dilbertReader = DilbertReader.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.strip_browser);

		imageSwitcher = (ImageSwitcher) findViewById(R.id.dilbert_image_switcher);
		/* use fade-in for first image */
		imageSwitcher.setInAnimation(this, R.anim.fade_in_animation);
		imageSwitcher.setFactory(this);

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
		if (v == findViewById(R.id.next)) {
			dilbertReader.nextDay();
			setNextTransitionDirection(Direction.RIGHT);
		} else if (v == findViewById(R.id.previous)) {
			dilbertReader.previousDay();
			setNextTransitionDirection(Direction.LEFT);
		}
		fetchNewFinbert();
	}

	private void setNextTransitionDirection(Direction direction) {
		boolean fromLeft = direction == Direction.LEFT ? true : false;
		imageSwitcher.setInAnimation(AnimationUtils.makeInAnimation(this, fromLeft));
		imageSwitcher.setOutAnimation(AnimationUtils.makeOutAnimation(this, fromLeft));
	}

	private void fetchNewFinbert() {
		new BackgroundDownloader().execute(dilbertReader);
	}

	private void updateNavigationButtonStates() {
		nextButton.setEnabled(dilbertReader.isNextAvailable());
		prevButton.setEnabled(dilbertReader.isPreviousAvailable());
	}

	private void updateTitle() {
		setTitle("Finbert - " + dilbertReader.getCurrentDate());
	}

	private void setFinbertImage(Bitmap downloadedImage) {
		BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), downloadedImage);
		imageSwitcher.setImageDrawable(bitmapDrawable);
	}

	/**
	 * Generates views for {@link ImageSwitcher}
	 */
	@Override
	public View makeView() {
		ImageView view = new ImageView(this);
		view.setScaleType(ScaleType.FIT_CENTER);
		view.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return view;
	}
}