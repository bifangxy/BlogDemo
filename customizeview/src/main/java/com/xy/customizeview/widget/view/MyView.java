package com.xy.customizeview.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xieying on 2019/6/19.
 * Description：
 */
public class MyView extends View {

    //默认的宽度大小
    private int mDefaultWidth;

    //默认的高度大小
    private int mDefaultHeight;


    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


    }

    private int getMeasureSize(int measureSpec, int defaultSize) {
        int size = defaultSize;

        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);

        switch (measureMode) {
            //父容器对当前View没有任何限制，当前View可以取任意值
            case MeasureSpec.UNSPECIFIED:
                break;
            //当前尺寸就是当前View最大的取值
            case MeasureSpec.AT_MOST:
                break;
            //指定了大小，当前尺寸就是View应该的取值
            case MeasureSpec.EXACTLY:
                break;
        }
        return size;


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
