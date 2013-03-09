package paksu.finbert.ui;

import paksu.finbert.FinbertApplication;
import paksu.finbert.ui.utils.BitmapCache;
import paksu.finbert.ui.utils.ImageLoader;
import android.app.Fragment;
import android.os.Bundle;

public class StripFragment extends Fragment {

	private ImageLoader loader;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BitmapCache cache = ((FinbertApplication) getActivity().getApplication()).getDefaultBitmapCache();
		loader = new ImageLoader(cache);
	}

	protected ImageLoader getImageLoader() {
		return loader;
	}

}
