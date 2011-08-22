/**
 * 
 */
package paksu.finbert;

import android.app.Activity;
import android.os.Bundle;

/**
 *
 */
public final class CommentsActivity extends Activity {
	public static final String EXTRAS_DATE = "date";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments);

		String date = getIntent().getExtras().getString(EXTRAS_DATE);
		setTitle("Finbert - comments - " + date);
	}
}
