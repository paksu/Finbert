package paksu.finbert.ui;

import org.joda.time.DateTime;

import paksu.finbert.R;
import paksu.finbert.ui.utils.StripUtils;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PagerStripFragment extends StripFragment {

	private StripAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new StripAdapter();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewPager pager = new ViewPager(getActivity());
		pager.setAdapter(adapter);
		return pager;
	}

	private class StripAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = LayoutInflater.from(getActivity()).inflate(R.layout.strip_list_item, null);
			getImageLoader().loadImage((ImageView) v.findViewById(R.id.image), StripUtils.urlForDate(DateTime.now()));
			((TextView) v.findViewById(R.id.text1)).setText(DateTime.now().toString());
			container.addView(v);
			return v;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

}
