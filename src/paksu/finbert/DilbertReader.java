package paksu.finbert;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.joda.time.DateTime;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public final class DilbertReader {
	private final String defaultDilbertURL = "http://www.taloussanomat.fi/dilbert/dilbert.php?date=";
	private DateTime dt = new DateTime();
	private boolean nextAvailable = false;
	private boolean previousAvailable = false;
	private static DilbertReader instance = null;
	private final HashMap<String, Boolean> availabilityCache;
	private final ImageCache imageCache;

	protected DilbertReader() {
		availabilityCache = new HashMap<String, Boolean>();
		imageCache = new ImageCache();
	}

	public static DilbertReader getInstance() {
		if (instance == null) {
			instance = new DilbertReader();
		}

		return instance;
	}

	public boolean hasCurrentCached() {
		return imageCache.get(getCurrentDate()) != null;
	}

	public Bitmap readCurrent() {
		Bitmap picture = null;
		URL dilbertUrl;
		String date = getCurrentDate();

		if (imageCache.imageIsCachedFor(date)) {
			picture = imageCache.get(date);
		} else {
			Log.d("finbert", "Downloading image for date:" + date);

			try {
				dilbertUrl = new URL(defaultDilbertURL + date);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			try {
				HttpURLConnection conn = (HttpURLConnection) dilbertUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				picture = BitmapFactory.decodeStream(is);
				imageCache.set(date, picture);
				availabilityCache.put(date, true);
				Log.d("finbert", "Downloaded and cached image for date:" + date);

			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		checkAvailability();

		return picture;
	}

	private void checkAvailability() {
		Log.d("finbert", "Checking availability");
		if (isDilbertAvailable(peekNextDate())) {
			Log.d("finbert", "Next day is available");
			setNextAvailable(true);
		} else {
			Log.d("finbert", "Next day is not available");
			setNextAvailable(false);
		}

		if (isDilbertAvailable(peekPreviousDate())) {
			Log.d("finbert", "Previous day is available");
			setPreviousAvailable(true);
		} else {
			Log.d("finbert", "Previous day is not available");
			setPreviousAvailable(false);
		}

	}

	public void previousDay() {
		dt = dt.minusDays(1);
	}

	public void nextDay() {
		dt = dt.plusDays(1);
		if (dt.getDayOfWeek() > 5) {
			while (dt.getDayOfWeek() != 1) {
				dt = dt.plusDays(1);
			}
		}
	}

	public String getCurrentDate() {
		while (dt.getDayOfWeek() > 5) {
			dt = dt.minusDays(1);
		}
		Log.d("finbert",
				"Current date:" + Integer.toString(dt.getYear()) + "-"
						+ Integer.toString(dt.getMonthOfYear()) + "-"
						+ Integer.toString(dt.getDayOfMonth()));
		return Integer.toString(dt.getYear()) + "-"
				+ Integer.toString(dt.getMonthOfYear()) + "-"
				+ Integer.toString(dt.getDayOfMonth());
	}

	private String peekNextDate() {
		DateTime nextDay = dt;
		nextDay = nextDay.plusDays(1);
		if (nextDay.getDayOfWeek() > 5) {
			while (nextDay.getDayOfWeek() != 1) {
				nextDay = nextDay.plusDays(1);
			}
		}
		return Integer.toString(nextDay.getYear()) + "-"
				+ Integer.toString(nextDay.getMonthOfYear()) + "-"
				+ Integer.toString(nextDay.getDayOfMonth());
	}

	private String peekPreviousDate() {
		DateTime previousDay = dt;
		previousDay = previousDay.minusDays(1);
		while (previousDay.getDayOfWeek() > 5) {
			previousDay = previousDay.minusDays(1);
		}
		return Integer.toString(previousDay.getYear()) + "-"
				+ Integer.toString(previousDay.getMonthOfYear()) + "-"
				+ Integer.toString(previousDay.getDayOfMonth());
	}

	private boolean isDilbertAvailable(String dateString) {
		URL dilbertUrl;

		if (availabilityCache.containsKey(dateString)) {
			boolean cached = availabilityCache.get(dateString);
			Log.d("finbert", "isDilbertAvailable found cached value for date:"
					+ dateString);
			return cached;
		} else {
			Log.d("finbert", "isDilbertAvailable polling date:" + dateString);
			try {
				dilbertUrl = new URL(defaultDilbertURL + dateString);
			} catch (MalformedURLException e) {
				return false;
			}

			try {
				HttpURLConnection conn = (HttpURLConnection) dilbertUrl.openConnection();
				conn.setDoInput(true);
				conn.connect();
				conn.getInputStream();
				availabilityCache.put(dateString, true);
			} catch (IOException e) {
				availabilityCache.put(dateString, false);
				return false;
			}
		}
		return true;
	}

	public boolean isNextAvailable() {
		return nextAvailable;
	}

	public void setNextAvailable(boolean isNextAvailable) {
		nextAvailable = isNextAvailable;
	}

	public boolean isPreviousAvailable() {
		return previousAvailable;
	}

	public void setPreviousAvailable(boolean isPreviousAvailable) {
		previousAvailable = isPreviousAvailable;
	}
}
