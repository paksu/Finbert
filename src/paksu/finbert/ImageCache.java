package paksu.finbert;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageCache {
	private final HashMap<String, SoftReference<Bitmap>> imageCache;

	public ImageCache() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	public void set(String date, Bitmap picture) {
		imageCache.put(date, new SoftReference<Bitmap>(picture));
	}

	public Bitmap get(String date) {
		Bitmap image = null;
		SoftReference<Bitmap> cachedImage = imageCache.get(date);
		if (cachedImage != null) {
			image = cachedImage.get();
		}
		return image;
	}

	public void delete(String date) {
		imageCache.remove(date);
	}

	public boolean isImageCachedForDate(DilbertDate date) {
		boolean cached = false;
		if (imageCache.containsKey(date)) {
			Log.d("finbert", "Checing if image is cached for date:" + date);
			SoftReference<Bitmap> cachedImage = imageCache.get(date);
			if (cachedImage.get() != null) {
				Log.d("finbert", "Found cached image for date:" + date);
				cached = true;
			} else {
				Log.d("finbert", "No image found for date:" + date);
				imageCache.remove(date);
			}
		}
		return cached;
	}
}
