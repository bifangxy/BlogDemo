package com.xy.customizeview;

import com.xy.common.base.BaseActivity;
import com.xy.common.utils.LogUtils;
import com.xy.customizeview.widget.view.SliderView;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.slider_view)
    SliderView mSliderView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        mSliderView.addOnSlideListener(new SliderView.OnSlideListener() {
            @Override
            public void left() {
                LogUtils.d("---left---");
            }

            @Override
            public void right() {
                LogUtils.d("---right---");

            }
        });
    }
}
