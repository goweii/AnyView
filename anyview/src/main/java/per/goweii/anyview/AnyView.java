package per.goweii.anyview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/1/22
 */
public class AnyView extends FrameLayout {

    private int mLayoutRes = 0;

    private SparseArray<View> views = null;

    public AnyView(@NonNull Context context) {
        this(context, null);
    }

    public AnyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initViews();
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        boolean childWidthMatchParent = false;
        boolean childHeightMatchParent = false;

        final int count = getChildCount();
        if (count >= 0) {
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                if (params.width == LayoutParams.MATCH_PARENT) {
                    childWidthMatchParent = true;
                }
                if (params.height == LayoutParams.MATCH_PARENT) {
                    childHeightMatchParent = true;
                }
                if (childWidthMatchParent && childHeightMatchParent) {
                    break;
                }
            }
        }
        int newWidthMode;
        int newHeightMode;
        if (childWidthMatchParent) {
            newWidthMode = MeasureSpec.EXACTLY;
        } else {
            newWidthMode = MeasureSpec.AT_MOST;
        }
        if (childHeightMatchParent) {
            newHeightMode = MeasureSpec.EXACTLY;
        } else {
            newHeightMode = MeasureSpec.AT_MOST;
        }

        final int newWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, newWidthMode);
        final int newHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, newHeightMode);

        super.onMeasure(newWidthSpec, newHeightSpec);
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AnyView);
        mLayoutRes = typedArray.getResourceId(R.styleable.AnyView_av_layout_res, 0);
        typedArray.recycle();
    }

    private void initViews() {
        int[] layoutRes = getChildLayoutRes();
        if (layoutRes == null || layoutRes.length == 0) {
            return;
        }
        for (int res : layoutRes) {
            if (res > 0) {
                addChild(res);
            }
        }
    }

    protected int[] getChildLayoutRes() {
        return new int[]{mLayoutRes};
    }

    public void addChild(int layoutRes) {
        inflate(getContext(), layoutRes, this);
    }

    public void addChild(View childView) {
        ViewParent parent = childView.getParent();
        if (parent != null) {
            if (this == parent) {
                return;
            }
            ((ViewGroup) parent).removeView(childView);
        }
        addView(childView);
    }

    /**
     * 获取View并缓存，以便下次获取，避免频繁调用findViewById
     *
     * @param id View的id
     * @return View
     */
    public <V extends View> V getView(@IdRes int id) {
        if (views == null) {
            views = new SparseArray<>();
        }
        View view = views.get(id);
        if (view == null) {
            view = findViewById(id);
            views.put(id, view);
        }
        return (V) view;
    }
}