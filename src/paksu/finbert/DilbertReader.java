package paksu.finbert;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.joda.time.DateTime;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public final class DilbertReader {
	private final String defaultDilbertURL = "http://www.taloussanomat.fi/dilbert/dilbert.php?";
	private DateTime dt = new DateTime();
	private boolean nextAvailable = false;
	private boolean previousAvailable = false;
	private static DilbertReader instance = null;
	private final HashMap<String, Boolean> availabilityCache;
	private final ImageCache imageCache;
	private final HttpClient httpclient;
	private final int socketTimeoutDelay = 5 * 1000; // 5 seconds
	private final int connectionTimeoutDelay = 10 * 1000; // 10 second
	private final int httpRequestIsSuccessful = 200; // HTTP/1.1 200 OK

	protected DilbertReader() {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeoutDelay);
		HttpConnectionParams.setSoTimeout(httpParameters, socketTimeoutDelay);
		httpclient = new DefaultHttpClient(httpParameters);
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

	public Bitmap readCurrent() throws NetworkException {
		Bitmap picture = null;
		String date = getCurrentDate();

		if (imageCache.imageIsCachedFor(date)) {
			picture = imageCache.get(date);
		} else {
			Log.d("finbert", "Downloading image for date:" + date);

			String dilbertURL = defaultDilbertURL;

			List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
			requestParameters.add(new BasicNameValuePair("date", date));

			dilbertURL += URLEncodedUtils.format(requestParameters, "utf-8");

			HttpGet request = new HttpGet(dilbertURL);
			HttpResponse response;
			try {
				response = httpclient.execute(request);
				InputStream is = response.getEntity().getContent();
				BufferedInputStream buf = new BufferedInputStream(is, (int) response.getEntity().getContentLength());
				picture = BitmapFactory.decodeStream(buf);
				buf.close();
				is.close();
				imageCache.set(date, picture);
				availabilityCache.put(date, true);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				throw new NetworkException(e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new NetworkException(e);
			}
		}

		checkAvailability();

		return picture;
	}

	private void checkAvailability() throws NetworkException {
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

	private boolean isDilbertAvailable(String dateString) throws NetworkException {
		boolean dilbertIsAvailable = false;

		if (availabilityCache.containsKey(dateString)) {
			dilbertIsAvailable = availabilityCache.get(dateString);
			Log.d("finbert", "isDilbertAvailable found cached value for date:"
					+ dateString);
		} else {
			Log.d("finbert", "isDilbertAvailable polling date:" + dateString);

			String dilbertURL = defaultDilbertURL;
			List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();

			requestParameters.add(new BasicNameValuePair("date", dateString));
			dilbertURL += URLEncodedUtils.format(requestParameters, "utf-8");

			HttpGet request = new HttpGet(dilbertURL);

			try {
				HttpResponse response = httpclient.execute(request);
				if (response.getStatusLine().getStatusCode() == httpRequestIsSuccessful) {
					dilbertIsAvailable = true;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				throw new NetworkException(e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new NetworkException(e);
			}
		}
		return dilbertIsAvailable;
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
