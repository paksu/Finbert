package paksu.finbert.ui.utils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import paksu.finbert.R;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageLoader {

    private final Executor imageLoadExecutor = Executors.newCachedThreadPool();
    private final BitmapCache cache;

    public ImageLoader(BitmapCache cache) {
        this.cache = cache;
    }

    public ImageLoader loadImage(ImageView v, String url) {
        v.setTag(R.id.IMAGE_LOADER, url);

        Bitmap cached = cache.get(url);
        if (cached != null) {
            v.setImageBitmap(cached);
        } else {
            imageLoadExecutor.execute(new LoadImageRunnable(cache, v, url));
        }

        return this;
    }

    private static class LoadImageRunnable implements Runnable {

        private final BitmapCache cache;
        private final WeakReference<ImageView> viewRef;
        private final String url;

        public LoadImageRunnable(BitmapCache cache, ImageView view, String url) {
            this.cache = cache;
            viewRef = new WeakReference<ImageView>(view);
            this.url = url;
        }

        @Override
        public void run() {
            try {
                final Bitmap bm = Bitmaps.loadFromUrl(url);
                if (bm != null) {
                    cache.put(url, bm);
                    postBitmap(bm);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void postBitmap(final Bitmap bm) {
            final ImageView v = viewRef.get();
            if (v != null && v.getTag(R.id.IMAGE_LOADER).equals(url)) {
                v.post(new Runnable() {

                    @Override
                    public void run() {
                        v.setImageBitmap(bm);
                    }
                });
            }
        }
    }
}
