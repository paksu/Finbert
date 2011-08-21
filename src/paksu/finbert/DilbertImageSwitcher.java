package paksu.finbert;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;

public final class DilbertImageSwitcher extends ImageSwitcher {
	public interface OnFlingListener {
		void onFling(Direction direction);
	}

	public enum Direction {
		LEFT, RIGHT
	}

	private OnFlingListener onFlingListener;

	private final int scaledMaxFlingVelocity;
	private final int scaledMinFlingVelocity;
	private final int scaledMinXMovement;
	private final int scaledMaxYMovement;

	private final SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onDown(MotionEvent arg0) {
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float xVelocity, float yVelocity) {
			if (Math.abs(e1.getY() - e2.getY()) < scaledMaxYMovement
						&& Math.abs(e1.getX() - e2.getX()) > scaledMinXMovement) {
				if (Math.abs(xVelocity) > scaledMinFlingVelocity && Math.abs(xVelocity) < scaledMaxFlingVelocity) {
					Direction flingDirection = e1.getX() > e2.getX() ? Direction.LEFT : Direction.RIGHT;
					if (onFlingListener != null) {
						onFlingListener.onFling(flingDirection);
					}
					return true;
				}
			}
			return false;
		}
	};

	private final GestureDetector gestureDetector;

	public DilbertImageSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);

		ViewConfiguration config = ViewConfiguration.get(context);
		scaledMaxFlingVelocity = config.getScaledMaximumFlingVelocity();
		scaledMinFlingVelocity = config.getScaledMinimumFlingVelocity();
		scaledMaxYMovement = config.getScaledTouchSlop() * 2;
		scaledMinXMovement = config.getScaledTouchSlop();

		gestureDetector = new GestureDetector(context, onGestureListener);

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
	}

	public void fadeToDrawable(Drawable drawable) {
		setInAnimation(getContext(), android.R.anim.fade_in);
		setOutAnimation(getContext(), android.R.anim.fade_out);
		setImageDrawable(drawable);
	}

	public void slideToDrawable(Drawable drawable, Direction direction) {
		boolean fromLeft = direction == Direction.LEFT ? true : false;
		setInAnimation(AnimationUtils.makeInAnimation(getContext(), fromLeft));
		setOutAnimation(AnimationUtils.makeOutAnimation(getContext(), fromLeft));
		setImageDrawable(drawable);
	}

	public void setOnFlingListener(OnFlingListener onFlingListener) {
		this.onFlingListener = onFlingListener;
	}
}
