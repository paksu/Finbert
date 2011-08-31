package paksu.finbert;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SmileySelectionDialog extends Dialog implements OnItemClickListener {
	private class SmileyAdapter extends ArrayAdapter<Smiley> {

		public SmileyAdapter(Context context, int textViewResourceId, List<Smiley> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Smiley smiley = getItem(position);
			View v = LayoutInflater.from(getContext()).inflate(R.layout.smiley_list_item, null);

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
		setTitle("Select smiley");
		ListView smileyListView = new ListView(context);
		smileyListView.setAdapter(new SmileyAdapter(context, R.layout.smiley_list_item, Smiley.getAllSupported()));
		smileyListView.setOnItemClickListener(this);
		smileyListView.setBackgroundColor(Color.WHITE);

		setContentView(smileyListView);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}
}
