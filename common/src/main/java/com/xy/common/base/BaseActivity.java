package com.xy.common.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xy.common.utils.ActivityUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xieying on 2019/4/16.
 * Description：
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mUnbinder;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        onCreateTask(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mUnbinder = ButterKnife.bind(this);
        initData();
        initEvent();
        ActivityUtils.addActivity(this);
    }

    protected void onCreateTask(@Nullable Bundle saveInstanceState) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        ActivityUtils.removeActivity(this);
    }


    protected abstract int getLayoutId();

    protected abstract void initData();

    protected abstract void initEvent();


}
