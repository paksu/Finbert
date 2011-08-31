package paksu.finbert;

import android.app.Dialog;
import android.content.Context;
import android.widget.ListView;

public class SmileySelectionDialog extends Dialog {
	public SmileySelectionDialog(Context context, int theme) {
		super(context, theme);
		setTitle("Select smiley");
		ListView smileyListView = new ListView(context);
		setContentView(smileyListView);
	}

}
