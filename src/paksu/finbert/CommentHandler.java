package paksu.finbert;

import java.io.IOException;
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
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class CommentHandler {
	private final String mac = "Wanha, eka, toka, HUUTO, kiroilu, v*ttuilu ja muu p*rseily kielletty Seuraus: IP-esto";
	private final String serverUrl = "http://jamssi.net";
	private final String serverPort = "4325";
	private final Gson gson = new Gson();

	public CommentHandler() {
	}

	public boolean setComment(Comment commentToAdd) throws NetworkException {
		HttpClient httpclient = new DefaultHttpClient();
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
			ParsedResult queryResult = getResponseStatus(responseBody);
			if (queryResult.getStatus() == ParsedResult.Responsecode.SUCCESS) {
				isSuccess = true;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new NetworkException(e);
		}

		return isSuccess;
	}

	private ParsedResult getResponseStatus(String resultJSON) {
		return gson.fromJson(resultJSON, ParsedResult.class);
	}

	public List<Comment> getComments(String date) throws NetworkException, JsonParseException {
		List<Comment> commentList = new ArrayList<Comment>();
		String commentsJSON;

		commentsJSON = readCommentsFromServer(date.toString());
		commentList = deserializeCommentsJSON(commentsJSON);

		return commentList;
	}

	private String readCommentsFromServer(String date) throws NetworkException {
		HttpClient httpclient = new DefaultHttpClient();
		String commentsUrl = serverUrl + ":" + serverPort + "/comments/get?";
		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		requestParameters.add(new BasicNameValuePair("date", date));
		requestParameters.add(new BasicNameValuePair("checksum", calculateChecksum(date)));

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

	private List<Comment> deserializeCommentsJSON(String commentJSON) throws JsonParseException {
		List<Comment> commentList = new ArrayList<Comment>();
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(commentJSON).getAsJsonArray();
		ParsedResult queryResult = getResponseStatus(array.get(0).toString());
		if (queryResult.getStatus() == ParsedResult.Responsecode.SUCCESS) {
			try {
				for (int i = 1; i < array.size(); i++) {
					Comment nextComment = gson.fromJson(array.get(i), Comment.class);
					commentList.add(nextComment);
				}
			} catch (JsonParseException e) {
				e.printStackTrace();
				throw new JsonParseException(e);
			}
		} else {
			// TODO: Serveri sanoo, että haku epäonnistui esim sen takia, että
			// checksummi ei täsmää
			// heitellään poikkeusta?
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
