package unionware.base.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class DynamicHeightViewPager extends ViewPager {


    /**
     * Constructor
     *
     * @param context the context
     */
    public DynamicHeightViewPager(Context context) {
        super(context);
    }

    /**
     * Constructor
     *
     * @param context the context
     * @param attrs   the attribute set
     */
    public DynamicHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 解决pager中视图高度不一致的情况 动态更改pager高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, int heightMeasureSpec) {
        int index = getCurrentItem();
        int height = 0;
        View v = getChildAt(index);
        if (v != null) {
            v.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height = v.getMeasuredHeight() - 60;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + 80, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final ViewPager pager = this;
        pager.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            private int position;
            private int oldPosition;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                this.position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 2) {
                    requestLayout();
                }
            }

        });
    }
}
