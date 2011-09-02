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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DilbertReader {
	private final String defaultDilbertURL = "http://www.taloussanomat.fi/dilbert/dilbert.php?";
	private static DilbertReader instance = null;
	private final ImageCache imageCache = ImageCache.getInstance();
	private final HttpClient httpclient = HttpClientFactory.getClient();
	private final int httpRequestIsSuccessful = 200; // HTTP/1.1 200 OK

	public static DilbertReader getInstance() {
		if (instance == null) {
			instance = new DilbertReader();
		}

		return instance;
	}

	public boolean isCachedForDate(DilbertDate date) {
		return imageCache.get(date) != null;
	}

	public Bitmap downloadFinbertForDate(DilbertDate date) throws NetworkException {
		Bitmap picture = BitmapFactory.decodeByteArray(new byte[0], 0, 0);
		Log.d("finbert", "Downloading image for date:" + date.toString());

		String dilbertURL = defaultDilbertURL;
		String dateString = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();

		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		requestParameters.add(new BasicNameValuePair("date", dateString));

		dilbertURL += URLEncodedUtils.format(requestParameters, "utf-8");
		HttpGet request = new HttpGet(dilbertURL);
		HttpResponse response = null;
		InputStream is = null;
		try {
			response = httpclient.execute(request, new BasicHttpContext());
			if (response.getStatusLine().getStatusCode() == httpRequestIsSuccessful) {
				is = response.getEntity().getContent();
				picture = BitmapFactory.decodeStream(is);
				imageCache.set(date, picture);
			} else {
				throw new NetworkException("Download failed: " + date + " " +
						response.getStatusLine().getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		Log.d("finbert", "Downloaded");
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
			response = httpclient.execute(request, new BasicHttpContext());
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
	
	public String getUrlToDilbertForDate(DilbertDate date) {
		String dilbertURL = defaultDilbertURL;
		String dateString = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		requestParameters.add(new BasicNameValuePair("date", dateString));

		dilbertURL += URLEncodedUtils.format(requestParameters, "utf-8");
		
		return dilbertURL;
	}
}
