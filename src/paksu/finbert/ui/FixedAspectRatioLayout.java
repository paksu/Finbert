package paksu.finbert.ui;

import paksu.finbert.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Layout that hosts a single child View and has a fixed aspect ratio.
 * 
 * @author Joonas
 * 
 */
public class FixedAspectRatioLayout extends FrameLayout {

    private float aspectRatio = 1;

    public FixedAspectRatioLayout(Context context) {
        super(context);
    }

    public FixedAspectRatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttrs(context, attrs);
    }

    public FixedAspectRatioLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        readAttrs(context, attrs);
    }

    private void readAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedAspectRatioLayout);
        aspectRatio = a.getFloat(R.styleable.FixedAspectRatioLayout_aspect_ratio, 1);
        a.recycle();
    }

    @Override
    public void addView(View child) {
        ensureNoChildren();
        super.addView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        ensureNoChildren();
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, int index) {
        ensureNoChildren();
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int width, int height) {
        ensureNoChildren();
        super.addView(child, width, height);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        ensureNoChildren();
        super.addView(child, params);
    }

    private void ensureNoChildren() {
        if (getChildCount() > 0) {
            throw new IllegalStateException("FixedAspectRatioLayout can have only one child");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width / aspectRatio);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }
}
