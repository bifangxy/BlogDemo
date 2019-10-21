package com.xy.rxjavademo;

import android.content.Intent;

import com.xy.common.base.BaseActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

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

    @OnClick(R.id.bt_create_operator)
    public void createOperator() {
        startActivity(new Intent(this, CreateOperatorActivity.class));
    }
}
