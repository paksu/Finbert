package paksu.finbert;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class CommentHandler {
	private final String mac = "Wanha, eka, toka, HUUTO, kiroilu, v*ttuilu ja muu p*rseily kielletty Seuraus: IP-esto";
	private final String serverUrl = "jamssi.net";
	private final String serverPort = "4325";
	private final Gson gson = new Gson();
	private final HttpClient httpclient = HttpClientFactory.getClient();
	private static CommentHandler instance;

	protected CommentHandler() {

	}

	public static CommentHandler getInstance() {
		if (instance == null) {
			instance = new CommentHandler();
		}

		return instance;
	}

	public boolean setComment(Comment commentToAdd) throws NetworkException {
		Uri uri = new Uri.Builder()
				.scheme("http")
				.encodedAuthority(serverUrl + ":" + serverPort)
				.path("comments/insert")
				.appendQueryParameter("date", commentToAdd.getDate()) // TODO:
				.appendQueryParameter("checksum", calculateChecksum(commentToAdd.getDate()))
				.appendQueryParameter("name", commentToAdd.getName())
				.appendQueryParameter("comment", commentToAdd.getComment())
				.build();

		HttpGet request = new HttpGet(uri.toString());

		boolean isSuccess = false;

		try {
			HttpResponse response = httpclient.execute(request, new BasicHttpContext());
			String responseBody = EntityUtils.toString(response.getEntity(), "utf-8");
			isSuccess = gson.fromJson(responseBody, boolean.class);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		}

		return isSuccess;
	}

	public List<Comment> getComments(DilbertDate date) throws NetworkException {
		List<Comment> commentList = new ArrayList<Comment>();
		String commentsJSON;

		commentsJSON = readCommentsFromServer(date);
		commentList = deserializeCommentsJSON(commentsJSON);

		return commentList;
	}

	public int getCommentCount(DilbertDate date) throws NetworkException {
		Uri uri = new Uri.Builder()
				.scheme("http")
				.encodedAuthority(serverUrl + ":" + serverPort)
				.path("comments/count")
				.appendQueryParameter("date", date.toUriString())
				.appendQueryParameter("checksum", calculateChecksum(date.toUriString()))
				.build();

		Log.d("finbert", uri.toString());

		int commentCount = 0;

		HttpGet request = new HttpGet(uri.toString());

		try {
			HttpResponse response = httpclient.execute(request);
			String responseBody = EntityUtils.toString(response.getEntity(), "utf-8");
			commentCount = gson.fromJson(responseBody, Integer.class);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		}

		return commentCount;
	}

	private String readCommentsFromServer(DilbertDate date) throws NetworkException {
		Uri uri = new Uri.Builder()
				.scheme("http")
				.encodedAuthority(serverUrl + ":" + serverPort)
				.path("comments/get")
				.appendQueryParameter("date", date.toUriString())
				.appendQueryParameter("checksum", calculateChecksum(date.toUriString()))
				.build();

		HttpGet request = new HttpGet(uri.toString());
		HttpResponse response;
		try {
			response = httpclient.execute(request);
			String responseBody = EntityUtils.toString(response.getEntity(), "utf-8");
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
		Log.d("finbert", commentJSON);
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
