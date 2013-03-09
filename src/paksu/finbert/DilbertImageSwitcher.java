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

    private class TransitionParams {
        private Animation inAnim;
        private Animation outAnim;
        private Drawable targetDrawable;
        private ScaleType scaleType;
    }

    private final Queue<TransitionParams> queuedTransitions = new LinkedBlockingQueue<TransitionParams>();

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
            if (traveledYDistanceIsLessThanMax(e1.getY(), e2.getY())
                    && traveledXDistanceIsMoreThanMin(e1.getX(), e2.getX())) {
                if (xVelocityIsWithinAllowed(xVelocity)) {
                    Direction flingDirection = e1.getX() > e2.getX() ? Direction.LEFT : Direction.RIGHT;
                    if (onFlingListener != null) {
                        onFlingListener.onFling(flingDirection);
                    }
                    return true;
                }
            }
            return false;
        }

        public boolean traveledYDistanceIsLessThanMax(float y1, float y2) {
            return Math.abs(y1 - y2) < scaledMaxYMovement;
        }

        public boolean traveledXDistanceIsMoreThanMin(float x1, float x2) {
            return Math.abs(x1 - x2) > scaledMinXMovement;
        }

        public boolean xVelocityIsWithinAllowed(float xVelocity) {
            return Math.abs(xVelocity) > scaledMinFlingVelocity && Math.abs(xVelocity) < scaledMaxFlingVelocity;
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
        TransitionParams params = new TransitionParams();
        params.targetDrawable = drawable;
        params.scaleType = scaleType;
        params.inAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        params.outAnim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        queueTransition(params);
    }

    public void slideToDrawable(Drawable drawable, ScaleType scaleType, Direction direction) {
        TransitionParams params = new TransitionParams();
        params.targetDrawable = drawable;
        params.scaleType = scaleType;
        if (direction == Direction.LEFT) {
            params.inAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_left);
            params.outAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_to_right);
        } else {
            params.inAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_from_right);
            params.outAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_to_left);
        }
        queueTransition(params);
    }

    private void queueTransition(TransitionParams params) {
        queuedTransitions.add(params);
        processTransitionQueue();
    }

    private void processTransitionQueue() {
        if (!queuedTransitions.isEmpty() && !hasUnfinishedAnimationsRunning()) {
            TransitionParams nextTransitionParams = queuedTransitions.poll();
            doTransition(nextTransitionParams);
        }
    }

    private boolean hasUnfinishedAnimationsRunning() {
        return animationsRunning > 0;
    }

    private void doTransition(TransitionParams params) {
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
        processTransitionQueue();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        ;
    }

}
