package paksu.finbert;

import com.helloandroid.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class Finbert extends Activity {

	private ImageView imageViewHandle;
	private Button nextButton;
	private Button prevButton;
	private DilbertReader dilbertReader;
	private class BackgroundDownloader extends AsyncTask<DilbertReader, Void, Bitmap> {
	    private ImageView imageView;
	    
		@Override
		protected Bitmap doInBackground(DilbertReader... params) {
			Bitmap downloadedImage = params[0].readCurrent();
	    	return downloadedImage;
	    }

	    protected void onPostExecute(Bitmap downloadedImage) {
	    	nextButton.setEnabled(dilbertReader.isNextAvailable());
	    	prevButton.setEnabled(dilbertReader.isPreviousAvailable());
	    	Log.d("finbert","BackgroundDownloader onPostExecute");
	    	imageView.setImageBitmap(downloadedImage);
	    }
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.setImageViewHandle((ImageView) findViewById(R.id.imageview));
        this.nextButton = (Button) findViewById(R.id.next);
        this.prevButton = (Button) findViewById(R.id.previous);
        
    	nextButton.setEnabled(false);
    	prevButton.setEnabled(false);
    	
        dilbertReader = DilbertReader.getInstance();
        new BackgroundDownloader().execute(dilbertReader);

    }
    
    public void buttonListener(View v) {
    	if(v == findViewById(R.id.next)) {
    		dilbertReader.nextDay();
    	} else if(v == findViewById(R.id.previous)) {
    		dilbertReader.previousDay();
    	}
    	nextButton.setEnabled(false);
    	prevButton.setEnabled(false);
    	new BackgroundDownloader().execute(dilbertReader);
    }
    
	public ImageView getImageViewHandle() {
		return imageViewHandle;
	}
	public void setImageViewHandle(ImageView imageViewHandle) {
		this.imageViewHandle = imageViewHandle;
	}
}