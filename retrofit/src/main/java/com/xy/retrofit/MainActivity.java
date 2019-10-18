package com.xy.retrofit;

import android.widget.TextView;

import com.xy.common.base.BaseActivity;
import com.xy.common.utils.LogUtils;
import com.xy.retrofit.data.Translation;
import com.xy.retrofit.data.TranslationManager;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {
    @BindView(R.id.tv_describe)
    TextView mTvDescribe;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }

    @OnClick(R.id.bt_translate)
    public void translate(){
        TranslationManager.translation("试试看", new TranslationManager.OnTranslationResultListener() {
            @Override
            public void success(Translation translation) {
                mTvDescribe.setText(translation.toString());
            }

            @Override
            public void fail(String error) {
                mTvDescribe.setText("翻译失败");
                LogUtils.d("s = "+error);
            }
        });
    }
}
