package paksu.finbert;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SmileySelectionDialog extends Dialog implements OnItemClickListener {
	public interface OnSmileySelectedListener {
		void onSmileySelected(Smiley selected);
	}

	private OnSmileySelectedListener listener;

	private class SmileyAdapter extends ArrayAdapter<Smiley> {

		public SmileyAdapter(Context context, int textViewResourceId, List<Smiley> objects) {
			super(context, textViewResourceId, objects);
			Collections.sort(objects, new Comparator<Smiley>() {
				@Override
				public int compare(Smiley first, Smiley second) {
					String firstName = getContext().getResources().getString(first.getStringId());
					String secondName = getContext().getResources().getString(second.getStringId());
					return firstName.compareTo(secondName);
				}
			});

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView != null) {
				v = convertView;
			} else {
				v = LayoutInflater.from(getContext()).inflate(R.layout.smiley_list_item, null);
			}

			Smiley smiley = getItem(position);
			ImageView smileyImage = (ImageView) v.findViewById(R.id.smiley_image);
			smileyImage.setImageResource(smiley.getDrawableId());

			TextView smileyName = (TextView) v.findViewById(R.id.smiley_name);
			smileyName.setText(getContext().getString(smiley.getStringId()));

			TextView smileyPresentation = (TextView) v.findViewById(R.id.smiley_presentation);
			smileyPresentation.setText(smiley.getPresentation());

			return v;
		}

	}

	public SmileySelectionDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);

		ListView smileyListView = new ListView(context);
		smileyListView.setAdapter(new SmileyAdapter(context, R.layout.smiley_list_item, Smiley.getAllSupported()));
		smileyListView.setBackgroundColor(Color.WHITE);
		smileyListView.setCacheColorHint(Color.WHITE);
		smileyListView.setClickable(true);
		smileyListView.setFocusable(true);
		smileyListView.requestFocus();
		smileyListView.setOnItemClickListener(this);
		smileyListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setContentView(smileyListView);
		setTitle(R.string.select_smiley);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
	}

	@Override
	protected void onStop() {
		super.onStop();
		listener = null;
	}

	public void setOnSmileySelectedListener(OnSmileySelectedListener listener) {
		this.listener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("finbert", "click");
		SmileyAdapter adapter = (SmileyAdapter) parent.getAdapter();
		listener.onSmileySelected(adapter.getItem(position));
		dismiss();
	}
}
