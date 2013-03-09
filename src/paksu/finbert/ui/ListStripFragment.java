package paksu.finbert.ui;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import paksu.finbert.R;
import paksu.finbert.ui.utils.StripUtils;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;

public class ListStripFragment extends StripFragment {

    private StripAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new StripAdapter(getActivity(), Lists.newArrayList(DateTime.now()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView v = new ListView(getActivity());
        v.setOnScrollListener(listScrollListener);
        v.setAdapter(adapter);
        return v;
    }

    private final OnScrollListener listScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (isNearBottom(firstVisibleItem, visibleItemCount, totalItemCount)) {
                addMoreDatesToAdapter(adapter, 20);
            }
        }

        private boolean isNearBottom(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            return firstVisibleItem + visibleItemCount > totalItemCount - 5;
        }
    };

    private static void addMoreDatesToAdapter(StripAdapter adapter, int datesToAdd) {
        DateTime last = adapter.getItem(adapter.getCount() - 1);
        int datesAdded = 0;
        while (datesAdded < datesToAdd) {
            do {
                last = last.minusDays(1);
            } while (last.getDayOfWeek() == DateTimeConstants.SATURDAY || last.getDayOfWeek() == DateTimeConstants.SUNDAY);
            adapter.add(last);
            datesAdded++;
        }

        adapter.notifyDataSetChanged();
    }

    private class StripAdapter extends ArrayAdapter<DateTime> {

        public StripAdapter(Context context, List<DateTime> dates) {
            super(context, -1, dates);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.strip_list_item, null);
            }

            getImageLoader().loadImage((ImageView) v.findViewById(R.id.image), StripUtils.urlForDate(getItem(position)));
            ((TextView) v.findViewById(R.id.text1)).setText(getItem(position).toString());
            return v;
        }

    }
}
