package paksu.finbert;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public final class DilbertImageSwitcher extends ImageSwitcher implements AnimationListener {
	public interface OnFlingListener {
		void onFling(Direction direction);
	}

	public enum Direction {
		LEFT, RIGHT
	}

	private class AnimationParams {
		private Animation inAnim;
		private Animation outAnim;
		private Drawable targetDrawable;
		private ScaleType scaleType;
	}

	private final Queue<AnimationParams> queuedAnims = new LinkedBlockingQueue<AnimationParams>();

	private OnFlingListener onFlingListener;

	private volatile int animationsRunning = 0;

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
		scaledMaxYMovement = config.getScaledTouchSlop() * 5;
		scaledMinXMovement = config.getScaledTouchSlop();

		gestureDetector = new GestureDetector(context, onGestureListener);

		setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
	}

	public void fadeToDrawable(Drawable drawable, ScaleType scaleType) {
		AnimationParams params = new AnimationParams();
		params.targetDrawable = drawable;
		params.scaleType = scaleType;
		params.inAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
		params.outAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
		queueAnimation(params);
	}

	public void slideToDrawable(Drawable drawable, ScaleType scaleType, Direction direction) {
		AnimationParams params = new AnimationParams();
		params.targetDrawable = drawable;
		params.scaleType = scaleType;
		if (direction == Direction.LEFT) {
			params.inAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_left);
			params.outAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_to_right);
		} else {
			params.inAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_right);
			params.outAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_to_left);
		}
		queueAnimation(params);
	}

	private void queueAnimation(AnimationParams params) {
		queuedAnims.add(params);
		processAnimQueue();
	}

	private void processAnimQueue() {
		if (!queuedAnims.isEmpty() && animationsRunning == 0) {
			AnimationParams nextAnimParams = queuedAnims.poll();
			playAnimation(nextAnimParams);
		}
	}

	private void playAnimation(AnimationParams params) {
		ImageView nextView = (ImageView) getNextView();
		nextView.setScaleType(params.scaleType);
		setInAnimation(params.inAnim);
		setOutAnimation(params.outAnim);
		params.inAnim.setAnimationListener(this);
		params.outAnim.setAnimationListener(this);
		setImageDrawable(params.targetDrawable);
	}

	public void setOnFlingListener(OnFlingListener onFlingListener) {
		this.onFlingListener = onFlingListener;
	}

	@Override
	public void onAnimationStart(Animation animation) {
		animationsRunning++;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		animationsRunning--;
		processAnimQueue();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		;
	}

}
