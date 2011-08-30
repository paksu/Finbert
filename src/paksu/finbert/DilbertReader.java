package paksu.finbert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;

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

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeoutDelay);
		HttpConnectionParams.setSoTimeout(params, socketTimeoutDelay);

		ConnManagerParams.setTimeout(params, connectionTimeoutDelay);
		ConnManagerParams.setMaxTotalConnections(params, 100);
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {

			@Override
			public int getMaxForRoute(HttpRoute route) {
				return 10;
			}
		});

		HttpClientParams.setRedirecting(params, false);

		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);

		httpclient = new DefaultHttpClient(manager, params);
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
