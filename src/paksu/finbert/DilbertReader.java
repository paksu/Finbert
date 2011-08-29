package paksu.finbert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DilbertReader {
	private final String defaultDilbertURL = "http://www.taloussanomat.fi/dilbert/dilbert.php?";
	private static DilbertReader instance = null;
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
		imageCache = ImageCache.getInstance();
	}

	public static DilbertReader getInstance() {
		if (instance == null) {
			instance = new DilbertReader();
		}

		return instance;
	}

	public boolean isCachedForDate(DilbertDate date) {
		return imageCache.get(date) != null;
	}

	public synchronized Bitmap downloadFinbertForDate(DilbertDate date) throws NetworkException {
		Bitmap picture = null;
		Log.d("finbert", "Downloading image for date:" + date.toString());

		String dilbertURL = defaultDilbertURL;
		String dateString = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
		Log.d("finbert", dateString);

		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		requestParameters.add(new BasicNameValuePair("date", dateString));

		dilbertURL += URLEncodedUtils.format(requestParameters, "utf-8");

		HttpGet request = new HttpGet(dilbertURL);
		HttpResponse response;
		InputStream is = null;
		try {
			response = httpclient.execute(request);
			if (response.getStatusLine().getStatusCode() == httpRequestIsSuccessful) {
				is = response.getEntity().getContent();
				picture = BitmapFactory.decodeStream(is);
				is.close();
				imageCache.set(date, picture);
			} else {
				throw new NetworkException("Download failed: " + response.getStatusLine().getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} finally {
			try {
				Log.d("finbert", "closingz");
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return picture;
	}

	public boolean isDilbertAvailableForDate(DilbertDate date) {

		boolean isAvailable = false;

		String dilbertURL = defaultDilbertURL;
		String dateString = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();

		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		requestParameters.add(new BasicNameValuePair("date", dateString));

		dilbertURL += URLEncodedUtils.format(requestParameters, "utf-8");

		HttpGet request = new HttpGet(dilbertURL);
		HttpResponse response;

		try {
			response = httpclient.execute(request);
			if (response.getStatusLine().getStatusCode() == httpRequestIsSuccessful) {
				isAvailable = true;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isAvailable;
	}
}
