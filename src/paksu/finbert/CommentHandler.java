package paksu.finbert;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class CommentHandler {
	private final String mac = "Wanha, eka, toka, HUUTO, kiroilu, v*ttuilu ja muu p*rseily kielletty Seuraus: IP-esto";
	private final String serverUrl = "http://jamssi.net";
	private final String serverPort = "4325";
	private final Gson gson = new Gson();
	private final HttpClient httpclient;
	private final int socketTimeoutDelay = 5 * 1000; // 5 seconds
	private final int connectionTimeoutDelay = 10 * 1000; // 10 second

	public CommentHandler() {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeoutDelay);
		HttpConnectionParams.setSoTimeout(httpParameters, socketTimeoutDelay);
		httpclient = new DefaultHttpClient(httpParameters);
	}

	public boolean setComment(Comment commentToAdd) throws NetworkException {
		String commentsUrl = serverUrl + ":" + serverPort + "/comments/insert?";
		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();

		requestParameters.add(new BasicNameValuePair("comment", commentToAdd.getComment()));
		requestParameters.add(new BasicNameValuePair("name", commentToAdd.getName()));
		requestParameters.add(new BasicNameValuePair("date", commentToAdd.getDate()));
		requestParameters.add(new BasicNameValuePair("checksum", calculateChecksum(commentToAdd.getDate())));

		commentsUrl += URLEncodedUtils.format(requestParameters, "utf-8");

		HttpGet request = new HttpGet(commentsUrl);

		boolean isSuccess = false;

		try {
			HttpResponse response = httpclient.execute(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			isSuccess = gson.fromJson(responseBody, boolean.class);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		}

		return isSuccess;
	}

	public List<Comment> getComments(DilbertDate date) throws NetworkException, JsonParseException {
		List<Comment> commentList = new ArrayList<Comment>();
		String commentsJSON;

		commentsJSON = readCommentsFromServer(date);
		commentList = deserializeCommentsJSON(commentsJSON);

		return commentList;
	}

	public Integer getCommentCount(DilbertDate date) throws NetworkException, JsonParseException {
		Log.d("finbert", "date " + date);
		String commentsUrl = serverUrl + ":" + serverPort + "/comments/count?";
		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		Integer commentCount = new Integer(0);

		requestParameters.add(new BasicNameValuePair("date", date.toUriString()));
		requestParameters.add(new BasicNameValuePair("checksum", calculateChecksum(date.toUriString())));

		commentsUrl += URLEncodedUtils.format(requestParameters, "utf-8");

		HttpGet request = new HttpGet(commentsUrl);
		HttpResponse response;

		try {
			response = httpclient.execute(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			commentCount = gson.fromJson(responseBody, Integer.class);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		}
		return commentCount;

	}

	private String readCommentsFromServer(DilbertDate date) throws NetworkException {
		String commentsUrl = serverUrl + ":" + serverPort + "/comments/get?";
		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		requestParameters.add(new BasicNameValuePair("date", date.toUriString()));
		requestParameters.add(new BasicNameValuePair("checksum", calculateChecksum(date.toUriString())));

		commentsUrl += URLEncodedUtils.format(requestParameters, "utf-8");

		HttpGet request = new HttpGet(commentsUrl);
		HttpResponse response;
		try {
			response = httpclient.execute(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			return responseBody;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}

	private List<Comment> deserializeCommentsJSON(String commentJSON) throws NetworkException {
		List<Comment> commentList = new ArrayList<Comment>();

		try {
			Type collectionType = new TypeToken<List<Comment>>() {
			}.getType();
			commentList = gson.fromJson(commentJSON, collectionType);
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		}

		return commentList;
	}

	private String calculateChecksum(String date) {
		MessageDigest digest;

		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			String combinedArguments = date + mac;
			digest.update(combinedArguments.getBytes());
			String hash = new BigInteger(1, digest.digest()).toString(16);
			return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
