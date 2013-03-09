
package paksu.finbert.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;

public class StripUtils {

	private static final String BASE_STRIP_URL = "http://www.taloussanomat.fi/dilbert/dilbert.php?";

	public static String urlForDate(DateTime date) {
		String dateString = date.getYear() + "-" + date.getMonthOfYear() + "-" + date.getDayOfMonth();

		List<NameValuePair> requestParameters = new ArrayList<NameValuePair>();
		requestParameters.add(new BasicNameValuePair("date", dateString));

		return BASE_STRIP_URL + URLEncodedUtils.format(requestParameters, "utf-8");
	}
}
