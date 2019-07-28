package com.xy.customizeview.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xy.common.utils.DipPixelUtil;
import com.xy.customizeview.R;

/**
 * Created by xieying on 2019/6/19.
 * Description：
 */
public class SliderView extends View {

    private Paint mPaint;

    private Bitmap mBackgroundBitmap;

    private Bitmap mSliderBitmap;

    //默认的宽度大小
    private int mDefaultWidth = DipPixelUtil.dip2px(74);

    //默认的高度大小
    private int mDefaultHeight = DipPixelUtil.dip2px(24);

    private int mWidth;

    private int mHeight;

    private int mBackgroundBitmapWidth;

    private int mSliderBitmapWidth;

    private int mBackgroundBitmapHeight;

    private int mSliderBitmapHeight;

    private int mSliderLeft;

    private int maxSlideValue;

    private int minSlideValue;

    private int color;

    private String text;

    private int size;

    private int sex;

    private OnSlideListener mListener;

    public SliderView(Context context) {
        this(context, null);
    }

    public SliderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomAttrs(context, attrs);
        init();
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        //获取自定义的属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliderView);
        size = (int) typedArray.getDimension(R.styleable.SliderView_size, 16);
        color = typedArray.getColor(R.styleable.SliderView_color, Color.BLUE);
        text = typedArray.getString(R.styleable.SliderView_text);
        sex = typedArray.getInt(R.styleable.SliderView_sex, 0);
        typedArray.recycle();

    }

    private void init() {
        mBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow_crane);
        mSliderBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.slider_crane);

        mBackgroundBitmapWidth = mBackgroundBitmap.getWidth();
        mBackgroundBitmapHeight = mBackgroundBitmap.getHeight();

        mSliderBitmapWidth = mSliderBitmap.getWidth();
        mSliderBitmapHeight = mSliderBitmap.getHeight();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasureSize(widthMeasureSpec, mDefaultWidth);
        mHeight = getMeasureSize(heightMeasureSpec, mDefaultHeight);
        setMeasuredDimension(mWidth, mHeight);
        maxSlideValue = ((mWidth + mBackgroundBitmapWidth) >> 1) - mSliderBitmapWidth;
        minSlideValue = (mWidth - mBackgroundBitmapWidth) >> 1;

        mSliderLeft = (mWidth - mSliderBitmapWidth) >> 1;
    }

    private int getMeasureSize(int measureSpec, int defaultSize) {
        int size = defaultSize;

        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);

        switch (measureMode) {
            //父容器对当前View没有任何限制，当前View可以取任意值
            case MeasureSpec.UNSPECIFIED:
                size = defaultSize;
                break;
            //当前尺寸就是当前View最大的取值
            case MeasureSpec.AT_MOST:
                size = measureSize;
                break;
            //指定了大小，当前尺寸就是View应该的取值
            case MeasureSpec.EXACTLY:
                size = measureSize;
                break;
        }
        return size;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBackgroundBitmap, (mWidth - mBackgroundBitmapWidth) >> 1
                , (mHeight - mBackgroundBitmapHeight) >> 1, mPaint);
        canvas.drawBitmap(mSliderBitmap, mSliderLeft,
                (mHeight - mSliderBitmapHeight) >> 1, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!isInSlider(event.getX(), event.getY()))
                    return true;
                break;
            case MotionEvent.ACTION_MOVE:
                float newX = event.getX();
                if (newX > maxSlideValue)
                    newX = maxSlideValue;
                else if (newX < minSlideValue)
                    newX = minSlideValue;
                mSliderLeft = (int) newX;
                //添加滑动左右的监听
                if (mSliderLeft < (mWidth - mSliderBitmapWidth >> 1)) {
                    if (mListener != null)
                        mListener.left();
                } else {
                    if (mListener != null)
                        mListener.right();
                }
                break;
            case MotionEvent.ACTION_UP:
                mSliderLeft = (mWidth - mSliderBitmapWidth) >> 1;
                break;
            default:
                break;
        }

        invalidate();

        return true;
    }


    /**
     * 判断点是否在滑块内
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isInSlider(float x, float y) {
        if (x <= mSliderLeft + mSliderBitmapWidth && x >= mSliderLeft
                && y <= mHeight - mSliderBitmapHeight >> 2 && y >= (mHeight - mSliderBitmapHeight) >> 1)
            return true;
        return false;
    }

    public void addOnSlideListener(OnSlideListener mListener) {
        this.mListener = mListener;
    }

    public interface OnSlideListener {
        void left();

        void right();
    }


}
