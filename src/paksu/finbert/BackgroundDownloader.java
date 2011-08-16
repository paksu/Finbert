package paksu.finbert;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

class BackgroundDownloader extends AsyncTask<DilbertReader, Void, Bitmap> {
    private ImageView imageView;
    
    public BackgroundDownloader(ImageView imageViewHandle) {
        super();
        imageView = imageViewHandle;
    }
    
	@Override
	protected Bitmap doInBackground(DilbertReader... params) {
		Bitmap downloadedImage = params[0].readCurrent();
    	return downloadedImage;
    }

    protected void onPostExecute(Bitmap downloadedImage) {
    	Log.d("finbert","BackgroundDownloader onPostExecute");
    	this.imageView.setImageBitmap(downloadedImage);
    }
}
