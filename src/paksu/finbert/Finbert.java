package paksu.finbert;

import com.helloandroid.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;


public final class Finbert extends Activity implements ViewFactory {
	private enum Direction { LEFT, RIGHT };
	private ImageSwitcher imageSwitcher;
	private ImageButton nextButton;
	private ImageButton prevButton;
	private final DilbertReader dilbertReader;
    
	private class BackgroundDownloader extends AsyncTask<DilbertReader, Void, Bitmap> {
		private ProgressDialog dialog;
		
		protected void onPreExecute() {
			dialog = ProgressDialog.show(Finbert.this, null, "Loading image..");
		}
		
		@Override
		protected Bitmap doInBackground(DilbertReader... params) {
			Bitmap downloadedImage = params[0].readCurrent();
	    	return downloadedImage;
	    }

	    protected void onPostExecute(Bitmap downloadedImage) {
	    	updateNavigationButtonStates();
			updateTitle();
	    	setFinbertImage(downloadedImage);
	    	
	    	if(dialog.isShowing()) {
	    		dialog.dismiss();
	    	}
	    }
	}
	
	public Finbert() {
		dilbertReader = DilbertReader.getInstance();
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        imageSwitcher = (ImageSwitcher) findViewById(R.id.dilbert_image_switcher);
        /* use fade-in for first image */
        imageSwitcher.setInAnimation(this, R.anim.fade_in_animation);
        imageSwitcher.setFactory(this);
        
        nextButton = (ImageButton) findViewById(R.id.next);
        prevButton = (ImageButton) findViewById(R.id.previous);
        
        fetchNewFinbert();
    }
 
    public void buttonListener(View v) {
    	if(v == findViewById(R.id.next)) {
    		dilbertReader.nextDay();
    		setNextTransitionDirection(Direction.RIGHT);
    	} else if(v == findViewById(R.id.previous)) {
    		dilbertReader.previousDay();
    		setNextTransitionDirection(Direction.LEFT);
    	}
    	
    	fetchNewFinbert();
    }
    
    private void setNextTransitionDirection(Direction direction) {
    	boolean fromLeft = direction == Direction.LEFT? true : false;
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