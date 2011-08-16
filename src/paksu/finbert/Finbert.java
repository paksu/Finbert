package paksu.finbert;

import com.helloandroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class Finbert extends Activity {

	private ImageView imageViewHandle;
	private Button nextButton;
	private Button prevButton;
	private DilbertReader dilbertReader;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.setImageViewHandle((ImageView) findViewById(R.id.imageview));
        this.nextButton = (Button) findViewById(R.id.next);
        this.prevButton = (Button) findViewById(R.id.previous);
        
        dilbertReader = DilbertReader.getInstance();
        new BackgroundDownloader(imageViewHandle).execute(dilbertReader);

    	//nextButton.setEnabled(DilbertReader.isNextAvailable());
    	//prevButton.setEnabled(DilbertReader.isPreviousAvailable());

    }
    
    public void buttonListener(View v) {
    	if(v == findViewById(R.id.next)) {
    		dilbertReader.nextDay();
    	} else if(v == findViewById(R.id.previous)) {
    		dilbertReader.previousDay();
    	}
    	
    	new BackgroundDownloader(imageViewHandle).execute(dilbertReader);
    	
    	//nextButton.setEnabled(DilbertReader.isNextAvailable());
    	//prevButton.setEnabled(DilbertReader.isPreviousAvailable());
    	
    }
    
	public ImageView getImageViewHandle() {
		return imageViewHandle;
	}
	public void setImageViewHandle(ImageView imageViewHandle) {
		this.imageViewHandle = imageViewHandle;
	}
}