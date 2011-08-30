package paksu.finbert;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public final class HttpClientFactory {
	private static final int CONNECTION_TIMEOUT_DELAY = 10 * 1000; // 10 seconds
	private static final int SOCKET_TIMEOUT_DELAY = 5 * 1000; // 5 seconds
	private static final int MAX_CONNECTIONS_PER_ROUTE = 20;

	private static HttpClient client;

	public static HttpClient getClient() {
		if (client == null) {
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpClientParams.setRedirecting(params, false);

			HttpConnectionParams.setStaleCheckingEnabled(params, false);
			HttpConnectionParams.setSocketBufferSize(params, 8192);
			HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT_DELAY);
			HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_DELAY);

			ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT_DELAY);
			ConnManagerParams.setMaxTotalConnections(params, 100);
			ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {

				@Override
				public int getMaxForRoute(HttpRoute route) {
					return MAX_CONNECTIONS_PER_ROUTE;
				}
			});

			ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, schemeRegistry);
			client = new DefaultHttpClient(manager, params);
		}

		return client;
	}
}
