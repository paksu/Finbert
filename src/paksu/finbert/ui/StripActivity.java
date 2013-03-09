package paksu.finbert.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;

public class StripActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Fragment current = getFragmentManager().findFragmentById(android.R.id.content);

		if (isLandscape()) {
			if (current == null || !(current instanceof PagerStripFragment)) {
				getFragmentManager().beginTransaction().replace(android.R.id.content, new PagerStripFragment()).commit();
			}
		} else {
			if (current == null || !(current instanceof ListStripFragment)) {
				getFragmentManager().beginTransaction().replace(android.R.id.content, new ListStripFragment()).commit();
			}
		}
	}

	private boolean isLandscape() {
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}
}
