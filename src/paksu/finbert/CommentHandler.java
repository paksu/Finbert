package paksu.finbert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;

import com.google.gson.Gson;

public class CommentHandler {
	private final String mac = "Wanha, eka, toka, HUUTO, kiroilu, v*ttuilu ja muu p*rseily kielletty Seuraus: IP-esto";
	private final String serverUrl = "http://jamssi.net";
	private final String serverPort = "4235";
	private final Gson gson = new Gson();

	public CommentHandler() {
	}

	public boolean setComment(String comment, String name, DateTime date) {
		return false;
	}

	public List<Comment> getComments(DateTime date) {
		List<Comment> commentList = new ArrayList<Comment>();
		String commentsJSON = readCommentJSON(date.toString());
		commentList = parseJSON(commentsJSON);
		return commentList;
	}

	private String readCommentJSON(String date) {
		HttpClient httpclient = new DefaultHttpClient();
		String getCommentPath = "comments/get";
		HttpPost request = new HttpPost(serverUrl + ":" + serverPort + "/" + getCommentPath);
		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();

		requestParameters.add(new BasicNameValuePair("date", date));
		requestParameters.add(new BasicNameValuePair("checksum", calculateChecksum(date)));

		try {
			request.setEntity(new UrlEncodedFormEntity(requestParameters));
			try {
				HttpResponse response = httpclient.execute(request);
				String responseBody = EntityUtils.toString(response.getEntity());
				return responseBody;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private List<Comment> parseJSON(String commentJSON) {

		List<Comment> commentList = new ArrayList<Comment>();
		Comment[] comments = gson.fromJson(commentJSON, Comment[].class);
		for (Comment c : comments) {
			commentList.add(c);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
