package paksu.finbert.ui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bitmaps {

    /**
     * Loads bitmap from network url.
     * 
     * @param url
     * @return
     * @throws IOException
     *             if failed due to network error.
     */
    public static Bitmap loadFromUrl(String url) throws IOException {
        HttpURLConnection connection = null;
        InputStream in = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.addRequestProperty("Cache-Control", "no-cache");
            in = connection.getInputStream();
            return BitmapFactory.decodeStream(in);
        } finally {
            if (in != null) {
                in.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private Bitmaps() {

    }
}
